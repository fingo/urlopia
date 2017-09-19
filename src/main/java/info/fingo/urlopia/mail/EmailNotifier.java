package info.fingo.urlopia.mail;

//import info.fingo.urlopia.ad.LocalTeam;
//import info.fingo.urlopia.events.*;
//import info.fingo.urlopia.request.Request;
//import info.fingo.urlopia.request.RequestDTO;
//import info.fingo.urlopia.request.acceptance.AcceptanceDTO;
//import info.fingo.urlopia.user.UserDTO;
//import info.fingo.urlopia.user.UserFactory;
//import info.fingo.urlopia.user.UserRepository;
//import info.fingo.urlopia.user.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Async
//@Component
//public class EmailNotifier {
//
//    @Value("${app.url}")
//    private String appUrl;
//
//    @Autowired
//    private UserFactory userFactory;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private EmailTemplateLoader emailTemplateLoader;
//
//    @Autowired
//    private EmailSender emailSender;
//
//    @EventListener(value = NewRequestNotificationEvent.class)
//    public void notifyLeaderAboutNewAcceptance(NewRequestNotificationEvent event) {
//        AcceptanceDTO acceptance = event.getAcceptance();
//        UserDTO leader = acceptance.getLeader();
//        Mail mail = createNewAcceptanceMail(acceptance, leader.getLang());
//        notify(leader, mail);
//    }
//
//    private Mail createNewAcceptanceMail(AcceptanceDTO acceptance, String languageCode) {
//        UserDTO requester = acceptance.getRequest().getRequester();
//        EmailTemplate template = emailTemplateLoader.load("newRequestNotification", languageCode)
//                .addProperty("requester", requester.getName())
//                .addProperty("term", acceptance.getRequest().getTerm())
//                .addProperty("acceptanceId", acceptance.getId())
//                .addProperty("appUrl", appUrl);
//        return new Mail(template);
//    }
//
//    @EventListener(value = DecisionResultEvent.class)
//    public void notifyRequesterAboutLeaderDecision(DecisionResultEvent event) {
//        AcceptanceDTO acceptance = event.getAcceptance();
//        UserDTO requester = acceptance.getRequest().getRequester();
//        Mail mail = createDecisionResultMail(acceptance, requester.getLang());
//        notify(requester, mail);
//    }
//
//    private Mail createDecisionResultMail(AcceptanceDTO acceptance, String languageCode) {
//        EmailTemplate template = emailTemplateLoader.load("decisionResult", languageCode)
//                .addProperty("decision", acceptance.isAccepted())
//                .addProperty("decider", acceptance.getDecider().getName())
//                .addProperty("acceptanceId", acceptance.getId());
//        return new Mail(template);
//    }
//
//    @EventListener(value = MailParsingProblemEvent.class)
//    public void notifySenderAboutMailParsingProblem(MailParsingProblemEvent event) {
//        UserDTO sender = event.getUser();
//        Mail mail = createMailParsingProblemMail(sender.getLang());
//        notify(sender, mail);
//    }
//
//    private Mail createMailParsingProblemMail(String languageCode) {
//        EmailTemplate template = emailTemplateLoader.load("mailParsingProblem", languageCode);
//        return new Mail(template);
//    }
//
//    @EventListener(value = RequestFailedEvent.class)
//    public void notifyRequesterAboutMissingVacationDays(RequestFailedEvent event) {
//        UserDTO requester = event.getUser();
//        Mail mail = createRequestFailedMail(requester.getLang());
//        notify(requester, mail);
//    }
//
//    private Mail createRequestFailedMail(String languageCode) {
//        EmailTemplate template = emailTemplateLoader.load("requestFailed", languageCode);
//        return new Mail(template);
//    }
//
//    @EventListener(value = OccasionalResponseEvent.class)
//    public void notifyRequesterAboutRequiredOccasionalDocuments(OccasionalResponseEvent event) {
//        RequestDTO request = event.getRequest();
//        UserDTO requester = request.getRequester();
//        Mail mail = createOccasionalResponseMail(requester.getLang());
//        notify(requester, mail);
//    }
//
//    private Mail createOccasionalResponseMail(String languageCode) {
//        EmailTemplate template = emailTemplateLoader.load("occasionalResponse", languageCode);
//        return new Mail(template);
//    }
//
//    @EventListener(value = OccasionalInfoEvent.class)
//    public void notifyLeaderAboutNewOccasionalRequest(OccasionalInfoEvent event) {
//        RequestDTO request = event.getRequest();
//        List<LocalTeam> teams = request.getRequester().getTeams();
//
//        Set<String> leadersMails = teams.stream()
//                .map(t -> t.getLeader().getPrincipalName())
//                .collect(Collectors.toSet());
//        for (String leaderMail : leadersMails) {
//            UserDTO leader = userFactory.create(userRepository.findFirstByMail(leaderMail));
//            Mail mail = createNewOccasionalRequestMail(request, leader.getLang());
//            notify(leader, mail);
//        }
//    }
//
//    @EventListener(value = OccasionalInfoEvent.class, condition = "#event.request.requester.B2B")
//    public void notifyAdminsAboutNewOccasionalRequest(OccasionalInfoEvent event) {
//        RequestDTO request = event.getRequest();
//        List<UserDTO> admins = userService.getAdmins();
//        for (UserDTO admin : admins) {
//            Mail mail = createNewOccasionalRequestMail(request, admin.getLang());
//            notify(admin, mail);
//        }
//    }
//
//    private Mail createNewOccasionalRequestMail(RequestDTO request, String languageCode) {
//        EmailTemplate template = emailTemplateLoader.load("occasionalInfo", languageCode)
//                .addProperty("requester", request.getRequester().getName())
//                .addProperty("type", ((Request.OccasionalType) request.getTypeInfo()).getIndex())
//                .addProperty("startDate", request.getStartDate())
//                .addProperty("endDate", request.getEndDate());
//        return new Mail(template);
//    }
//
//    @EventListener(value = RequestAcceptedEvent.class, condition = "#event.request.requester.B2B")
//    public void notifyAdminsAboutNewAcceptedRequest(RequestAcceptedEvent event) {
//        RequestDTO request = event.getRequest();
//        List<UserDTO> admins = userService.getAdmins();
//        for (UserDTO admin : admins) {
//            Mail mail = createNewAcceptedRequestMail(request, admin.getLang());
//            notify(admin, mail);
//        }
//    }
//
//    private Mail createNewAcceptedRequestMail(RequestDTO request, String languageCode) {
//        EmailTemplate template = emailTemplateLoader.load("requestAccepted", languageCode)
//                .addProperty("requester", request.getRequester().getName())
//                .addProperty("startDate", request.getStartDate().toString())
//                .addProperty("endDate", request.getEndDate().toString());
//        return new Mail(template);
//    }
//
//    private void notify(UserDTO receiver, Mail mail) {
//        mail.setRecipientName(receiver.getName());
//        mail.setRecipientAddress(receiver.getMail());
//        emailSender.send(mail);
//    }
//}
