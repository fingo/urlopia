package info.fingo.urlopia.api.v2.slack;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.reactions.ReactionsAddRequest;
import com.slack.api.methods.request.users.profile.UsersProfileGetRequest;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationInputOutput;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.user.NoSuchUserException;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackPresenceConfirmationEventHandler {
    public static final String SUCCESS_EMOJI_NAME = "white_check_mark";
    public static final String FAILURE_EMOJI_NAME = "x";

    private static final String MENTION_ONLY_REGEX = "^ *<.*> *$";
    private static final Pattern MENTION_ONLY_PATTERN = Pattern.compile(MENTION_ONLY_REGEX);

    private static final String TIME_REGEX = "(<.*>) *(\\d{1,2}:\\d{2}|\\d{1,2}) *- *(\\d{1,2}:\\d{2}|\\d{1,2}) *$";
    private static final Pattern TIME_PATTERN = Pattern.compile(TIME_REGEX);

    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(8, 0);

    private final App slack;
    private final UserService userService;
    private final PresenceConfirmationService presenceConfirmationService;

    @PostConstruct
    private void initialize() {
        slack.event(AppMentionEvent.class, this::handleMention);
        slack.event(MessageEvent.class, this::handleDirectMessage);
    }

    public Response handleMention(EventsApiPayload<AppMentionEvent> payload, EventContext ctx) {
        AppMentionEvent event = payload.getEvent();
        confirmPresence(event.getUser(), event.getChannel(), event.getText(), event.getTs());
        return ctx.ack();
    }

    public Response handleDirectMessage(EventsApiPayload<MessageEvent> payload, EventContext ctx) {
        MessageEvent event = payload.getEvent();
        confirmPresence(event.getUser(), event.getChannel(), event.getText(), event.getTs());
        return ctx.ack();
    }

    @SneakyThrows
    private void confirmPresence(String userSlackId,
                                 String channel,
                                 String eventMessage,
                                 String latestMessageTimestamp) {
        try {
            var userEmail = getUserEmail(userSlackId);
            var user = userService.get(userEmail);
            confirmUserTodaysPresence(user, eventMessage);
            addReactionToLatestMessage(channel, latestMessageTimestamp, SUCCESS_EMOJI_NAME);
        } catch (NoSuchUserException | SlackBotException ex) {
            addReactionToLatestMessage(channel, latestMessageTimestamp, FAILURE_EMOJI_NAME);
        }
    }

    private String getUserEmail(String userSlackId) throws SlackApiException, IOException {
        var request = UsersProfileGetRequest.builder()
                .token(slack.config().getSingleTeamBotToken())
                .user(userSlackId)
                .build();
        var response = slack.client().usersProfileGet(request);
        return response.isOk() ? response.getProfile().getEmail() : "";
    }

    private void confirmUserTodaysPresence(User user, String eventMessage) {
        var startTime = DEFAULT_START_TIME;
        var endTime = startTime.plusMinutes(Math.round(60 * user.getWorkTime()));

        if (!messageContainsMentionOnly(eventMessage)) {
            List<LocalTime> presenceTime = extractPresenceTimeFrom(eventMessage);
            if (presenceTime.isEmpty()) {
                log.error("Couldn't parse incoming message: {}", eventMessage);
                throw SlackBotException.invalidPresenceTimeFormat();
            }
            startTime = presenceTime.get(0);
            endTime = presenceTime.get(1);
        }

        var dto = new PresenceConfirmationInputOutput();
        dto.setUserId(user.getId());
        dto.setDate(LocalDate.now());
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);

        presenceConfirmationService.confirmPresence(user, dto);
    }

    private boolean messageContainsMentionOnly(String eventMessage) {
        var matcher = MENTION_ONLY_PATTERN.matcher(eventMessage);
        return matcher.matches();
    }

    private List<LocalTime> extractPresenceTimeFrom(String eventMessage) {
        var matcher = TIME_PATTERN.matcher(eventMessage);

        if (matcher.find()) {
            var startTimeString = matcher.group(2);
            var endTimeString = matcher.group(3);
            return List.of(getLocalTimeFrom(startTimeString), getLocalTimeFrom(endTimeString));
        }

        return Collections.emptyList();
    }

    private LocalTime getLocalTimeFrom(String string) {
        if (string.contains(":")) {
            var splitted = string.split(":");
            int hour = Integer.parseInt(splitted[0]);
            int minutes = Integer.parseInt(splitted[1]);
            return LocalTime.of(hour, minutes);
        } else {
            int hour = Integer.parseInt(string);
            return LocalTime.of(hour, 0);
        }
    }

    private void addReactionToLatestMessage(String channel,
                                            String latestMessageTimestamp,
                                            String reactionName) throws SlackApiException, IOException {
        var request = ReactionsAddRequest.builder()
                .token(slack.config().getSingleTeamBotToken())
                .channel(channel)
                .name(reactionName)
                .timestamp(latestMessageTimestamp)
                .build();
        slack.client().reactionsAdd(request);
    }
}
