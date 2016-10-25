package info.fingo.urlopia.mail;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import info.fingo.urlopia.ad.LocalTeam;
import info.fingo.urlopia.events.*;
import info.fingo.urlopia.request.AcceptanceDTO;
import info.fingo.urlopia.request.AcceptanceService;
import info.fingo.urlopia.request.RequestDTO;
import info.fingo.urlopia.user.UserDTO;
import info.fingo.urlopia.user.UserFactory;
import info.fingo.urlopia.user.UserRepository;
import info.fingo.urlopia.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sends messages via email
 *
 * @author Tomasz Urbas
 */

@Component
public class MailBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailBot.class);

    @Value("${mail.bot.name}")
    private String privateBotName;
    private static String BOT_NAME;

    @Value("${mail.bot.address}")
    private String privateBotAddress;
    private static String BOT_ADDRESS;

    @Value("${mail.template.prefix:/}")
    private String privateTemplatePrefix;
    private static String TEMPLATE_PREFIX;

    @Value("${mail.template.suffix:.hbs}")
    private String privateTemplateSuffix;
    private static String TEMPLATE_SUFFIX;

    @Value("${app.url}")
    private String appUrl;

    @PostConstruct
    public void init(){
        BOT_NAME = privateBotName;
        BOT_ADDRESS = privateBotAddress;

        TEMPLATE_PREFIX = privateTemplatePrefix;
        TEMPLATE_SUFFIX = privateTemplateSuffix;
    }

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    @Autowired
    private AcceptanceService acceptanceService;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private UserRepository userRepository;

    private void send(Mail mail) {
        mail.setSenderName(BOT_NAME);
        mail.setSenderAddress(BOT_ADDRESS);

        mailSender.send(new MailConverter(mail).toMimeMessage());
    }


    private Optional<Template> getTemplate(String templateName, String locale) {
        // create template loader
        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix(TEMPLATE_PREFIX);
        loader.setSuffix(TEMPLATE_SUFFIX);

        // create template
        Template template = null;

        try {
            Handlebars handlebars = new Handlebars(loader);
            handlebars.registerHelpers(new HandlebarsHelper());

            template = handlebars.compile(templateName + "_" + locale);
        } catch (IOException e) {
            LOGGER.error("IOException when trying to load a template", e);
        }

        return Optional.ofNullable(template);
    }

    private String getTemplateContent(Template template, Map<String, Object> model) {
        String content = "";

        try {
            Context context = Context.newBuilder(model)
                    .resolver(MapValueResolver.INSTANCE)
                    .build();
            content = template.apply(context);
        } catch (IOException e) {
            LOGGER.error("IOException when trying to get mail content", e);
        }

        return content;
    }

    private String parseMail(String templateContent, SectionFunction sectionFunction) {
        Pattern pattern = Pattern.compile("--(-)+");
        Matcher matcher = pattern.matcher(templateContent);

        String text = "";
        if (matcher.find()) {
            text = sectionFunction.getSection(templateContent, matcher);
        }

        return text.trim();
    }

    private String getMailSubject(String templateContent) {
        SectionFunction function = (content, matcher) -> content.substring(0, matcher.start());
        return parseMail(templateContent, function);
    }

    private String getMailContent(String templateContent) {
        SectionFunction function = (content, matcher) -> content.substring(matcher.end());
        return parseMail(templateContent, function);
    }

    private Mail prepareMail(Template template, Map<String, Object> model, UserDTO receiver) {
        String templateContent = getTemplateContent(template, model);
        Mail mail = new Mail();

        mail.setSubject(getMailSubject(templateContent));
        mail.setContent(getMailContent(templateContent));
        mail.setRecipientName(receiver.getName());
        mail.setRecipientAddress(receiver.getMail());

        return mail;
    }

    @TransactionalEventListener(classes = NewRequestNotificationEvent.class)
    @Async
    public void sendNewRequestNotification(NewRequestNotificationEvent event) {
        long acceptanceId = event.getId();

        // getting data
        AcceptanceDTO acceptance = acceptanceService.getAcceptance(acceptanceId);
        UserDTO requester = acceptance.getRequest().getRequester();

        // creating map of variables
        Map<String, Object> model = new HashMap<>();
        model.put("requester", requester.getName());
        model.put("term", acceptance.getRequest().getTerm());
        model.put("acceptanceId", acceptance.getId());
        model.put("appUrl", appUrl);

        // creating & sending mail
        for (LocalTeam team : requester.getTeams()) {
            UserDTO leader = userService.getUser(team.getLeader().getPrincipalName());
            String lang = leader.getLang();

            Optional<Template> template = getTemplate("newRequestNotification", lang);
            if (template.isPresent()) {
                send(prepareMail(template.get(), model, leader));
            }
        }
    }

    @TransactionalEventListener(classes = DecisionResultEvent.class)
    @Async
    public void sendDecisionResult(DecisionResultEvent event) {
        long acceptanceId = event.getId();

        // getting data
        AcceptanceDTO acceptance = acceptanceService.getAcceptance(acceptanceId);
        UserDTO requester = acceptance.getRequest().getRequester();
        String lang = requester.getLang();

        // creating map of variables
        Map<String, Object> model = new HashMap<>();
        model.put("decision", acceptance.isAccepted());
        model.put("decider", acceptance.getDecider().getName());
        model.put("acceptanceId", acceptance.getId());

        // creating & sending mail
        Optional<Template> template = getTemplate("decisionResult", lang);
        if (template.isPresent()) {
            send(prepareMail(template.get(), model, acceptance.getRequest().getRequester()));
        }
    }

    @TransactionalEventListener(classes = MailParsingProblemEvent.class)
    @Async
    public void sendMailParsingProblem(MailParsingProblemEvent event) {
        sendMailParsingProblem(event.getUser());
    }

    public void sendMailParsingProblem(UserDTO user) {
        // getting data
        String lang = user.getLang();

        // creating map of variables
        Map<String, Object> model = new HashMap<>();

        // creating & sending mail
        Optional<Template> template = getTemplate("mailParsingProblem", lang);
        if (template.isPresent()) {
            send(prepareMail(template.get(), model, user));
        }
    }

    @TransactionalEventListener(classes = RequestFailedEvent.class)
    @Async
    public void sendRequestFailed(RequestFailedEvent event) {
        sendRequestFailed(event.getUser());
    }

    public void sendRequestFailed(UserDTO user) {
        // getting data
        String lang = user.getLang();

        // creating map of variables
        Map<String, Object> model = new HashMap<>();

        // creating & sending mail
        Optional<Template> template = getTemplate("requestFailed", lang);
        if (template.isPresent()) {
            send(prepareMail(template.get(), model, user));
        }
    }

    @TransactionalEventListener(classes = OccasionalResponseEvent.class)
    @Async
    public void sendOccasionalResponse(OccasionalResponseEvent event) {
        UserDTO requester = event.getUser();
        Optional<Template> template = getTemplate("occasionalResponse", requester.getLang());
        Map<String, Object> model = new HashMap<>();
        if (template.isPresent()) {
            send(prepareMail(template.get(), model, requester));
        }
    }

    @TransactionalEventListener(classes = OccasionalInfoEvent.class)
    @Async
    public void sendOccasionalInfo(OccasionalInfoEvent event) {
        UserDTO requester = event.getUser();
        //List<User> allUsers = userRepository.findAll();
        int type = event.getType();
        float hours = event.getHours();
        LocalDate startDate = event.getStartDate();
        LocalDate endDate = event.getEndDate();

        List<LocalTeam> teams = requester.getTeams();

        // creating map of variables
        Map<String, Object> model = new HashMap<>();
        model.put("requester", requester.getName());
        model.put("type", type);
        model.put("hours", -hours);
        model.put("startDate", startDate);
        model.put("endDate", endDate);

        //informing team leaders about the occasion
        for (LocalTeam team : teams) {
            String leaderMail = team.getLeader().getMail();
            UserDTO leader = userFactory.create(userRepository.findFirstByMail(leaderMail));
            Optional<Template> leaderTemplate = getTemplate("occasionalInfo", leader.getLang());
            if (leaderTemplate.isPresent()) {
                send(prepareMail(leaderTemplate.get(), model, leader));
            }
        }

        //admins also
        List<UserDTO> admins = userService.getAdmins();
        for (UserDTO admin : admins) {
            Optional<Template> adminTemplate = getTemplate("occasionalInfo", admin.getLang());
            if (adminTemplate.isPresent()) {
                    send(prepareMail(adminTemplate.get(), model, admin));
            }
        }
    }

    @TransactionalEventListener(classes = RequestAcceptedEvent.class)
    @Async
    public void sendRequestAccepted(RequestAcceptedEvent event) {
        RequestDTO request = event.getRequest();

        // getting data
        UserDTO requester = request.getRequester();

        // creating map of variables
        Map<String, Object> model = new HashMap<>();
        model.put("requester", request.getRequester().getName());
        model.put("startDate", request.getStartDate().toString());
        model.put("endDate", request.getEndDate().toString());

        // creating & sending mails
        List<UserDTO> admins = userService.getAdmins();
        for (UserDTO admin : admins) {
            Optional<Template> template = getTemplate("requestAccepted", admin.getLang());
            if (template.isPresent()) {
                send(prepareMail(template.get(), model, admin));
            }
        }
    }

    @FunctionalInterface
    private interface SectionFunction {
        String getSection(String templateContent, Matcher matcher);
    }
}


