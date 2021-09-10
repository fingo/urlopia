package info.fingo.urlopia.api.v2.slack

import com.slack.api.app_backend.events.payload.EventsApiPayload
import com.slack.api.bolt.App
import com.slack.api.bolt.AppConfig
import com.slack.api.bolt.context.builtin.EventContext
import com.slack.api.bolt.response.Response
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.reactions.ReactionsAddRequest
import com.slack.api.methods.request.users.profile.UsersProfileGetRequest
import com.slack.api.methods.response.users.profile.UsersProfileGetResponse
import com.slack.api.model.event.AppMentionEvent
import com.slack.api.model.event.MessageEvent
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationInputOutput
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService
import info.fingo.urlopia.user.NoSuchUserException
import info.fingo.urlopia.user.User
import info.fingo.urlopia.user.UserService
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalTime

class SlackPresenceConfirmationEventHandlerSpec extends Specification {
    def sampleBotToken = "xoxb-xxx-xxx-xxx"
    def slack = Mock(App) {
        config() >> Mock(AppConfig) {
            getSingleTeamBotToken() >> sampleBotToken
        }
    }
    def userService = Mock(UserService)
    def presenceConfirmationService = Mock(PresenceConfirmationService)
    def handler = new SlackPresenceConfirmationEventHandler(slack, userService, presenceConfirmationService)

    def sampleChannelId = "HJKL123DA"
    def sampleTimeStamp = "9781213532.9234"

    def sampleMentionEvent = Mock(AppMentionEvent) {
        getChannel() >> sampleChannelId
        getTs() >> sampleTimeStamp
    }
    def sampleMentionPayload = Mock(EventsApiPayload) {
        getEvent() >> sampleMentionEvent
    }

    def sampleMessageEvent = Mock(MessageEvent) {
        getChannel() >> sampleChannelId
        getTs() >> sampleTimeStamp
    }
    def sampleMessagePayload = Mock(EventsApiPayload) {
        getEvent() >> sampleMessageEvent
    }

    def sampleContext = Mock(EventContext) {
        ack() >> Mock(Response)
    }

    def sampleSuccessReactionRequest = ReactionsAddRequest.builder()
            .token(sampleBotToken)
            .channel(sampleChannelId)
            .timestamp(sampleTimeStamp)
            .name(SlackPresenceConfirmationEventHandler.SUCCESS_EMOJI_NAME)
            .build()

    def sampleFailureReactionRequest = ReactionsAddRequest.builder()
            .token(sampleBotToken)
            .channel(sampleChannelId)
            .timestamp(sampleTimeStamp)
            .name(SlackPresenceConfirmationEventHandler.FAILURE_EMOJI_NAME)
            .build()

    def sampleUserId = 1L
    def sampleUserSlackId = "HSK12KDS1"
    def sampleUserMail = "some.user@fingo.pl"
    def sampleUser = Mock(User) {
        getId() >> sampleUserId
        getMail() >> sampleUserMail
        getWorkTime() >> 8.0f
    }

    def "handleMention() WHEN mentioning user is present and didn't specify presence time SHOULD confirm his presence with time based on his work time"() {
        given:
        slack.client() >> Mock(MethodsClient) {
            usersProfileGet(_ as UsersProfileGetRequest) >> Mock(UsersProfileGetResponse) {
                isOk() >> true
                getProfile() >> Mock(com.slack.api.model.User.Profile) {
                    getEmail() >> sampleUserMail
                }
            }
        }
        sampleMentionEvent.getText() >> "<@FPA12L32>"
        sampleMentionEvent.getUser() >> sampleUserSlackId
        userService.get(sampleUserMail) >> sampleUser

        when:
        handler.handleMention(sampleMentionPayload, sampleContext)

        then:
        1 * presenceConfirmationService.confirmPresence(sampleUser, {
            it instanceof PresenceConfirmationInputOutput
            it.getDate() == LocalDate.now()
            it.getStartTime() == LocalTime.of(8, 0)
            it.getEndTime() == LocalTime.of(8, 0).plusMinutes((60 * sampleUser.getWorkTime()) as long)
            it.getUserId() == sampleUserId
        })
        1 * slack.client().reactionsAdd(sampleSuccessReactionRequest)
    }

    def "handleMention() WHEN mentioning user is present and specified correct presence time SHOULD parse time from message correctly"() {
        given:
        slack.client() >> Mock(MethodsClient) {
            usersProfileGet(_ as UsersProfileGetRequest) >> Mock(UsersProfileGetResponse) {
                isOk() >> true
                getProfile() >> Mock(com.slack.api.model.User.Profile) {
                    getEmail() >> sampleUserMail
                }
            }
        }
        sampleMentionEvent.getText() >> testText
        sampleMentionEvent.getUser() >> sampleUserSlackId
        userService.get(sampleUserMail) >> sampleUser

        when:
        handler.handleMention(sampleMentionPayload, sampleContext)

        then:
        1 * presenceConfirmationService.confirmPresence(sampleUser, {
            it instanceof PresenceConfirmationInputOutput
            it.getDate() == LocalDate.now()
            it.getStartTime() == LocalTime.of(stHour, stMinute)
            it.getEndTime() == LocalTime.of(etHour, etMinute)
            it.getUserId() == sampleUserId
        })
        1 * slack.client().reactionsAdd(sampleSuccessReactionRequest)

        where:
        testText                  | stHour | stMinute | etHour | etMinute
        "<@FPA12L32> 9-9"         | 9      | 0        | 9      | 0
        "<@FPA12L32> 09-9"        | 9      | 0        | 9      | 0
        "<@FPA12L32> 9:30-9"      | 9      | 30       | 9      | 0
        "<@FPA12L32> 09:30-9"     | 9      | 30       | 9      | 0
        "<@FPA12L32> 9-09"        | 9      | 0        | 9      | 0
        "<@FPA12L32> 09-09"       | 9      | 0        | 9      | 0
        "<@FPA12L32> 9:30-09"     | 9      | 30       | 9      | 0
        "<@FPA12L32> 09:30-09"    | 9      | 30       | 9      | 0
        "<@FPA12L32> 9-9:30"      | 9      | 0        | 9      | 30
        "<@FPA12L32> 09-9:30"     | 9      | 0        | 9      | 30
        "<@FPA12L32> 9:30-9:30"   | 9      | 30       | 9      | 30
        "<@FPA12L32> 09:30-9:30"  | 9      | 30       | 9      | 30
        "<@FPA12L32> 9-09:30"     | 9      | 0        | 9      | 30
        "<@FPA12L32> 09-09:30"    | 9      | 0        | 9      | 30
        "<@FPA12L32> 9:30-09:30"  | 9      | 30       | 9      | 30
        "<@FPA12L32> 09:30-09:30" | 9      | 30       | 9      | 30
    }

    def "handleMention() WHEN mentioning user is present and specified invalid presence time SHOULD mark message with failure emoji"() {
        given:
        slack.client() >> Mock(MethodsClient) {
            usersProfileGet(_ as UsersProfileGetRequest) >> Mock(UsersProfileGetResponse) {
                isOk() >> true
                getProfile() >> Mock(com.slack.api.model.User.Profile) {
                    getEmail() >> sampleUserMail
                }
            }
        }
        sampleMentionEvent.getText() >> testText
        sampleMentionEvent.getUser() >> sampleUserSlackId
        userService.get(sampleUserMail) >> sampleUser

        when:
        handler.handleMention(sampleMentionPayload, sampleContext)

        then:
        0 * presenceConfirmationService.confirmPresence(sampleUser, _ as PresenceConfirmationInputOutput)
        1 * slack.client().reactionsAdd(sampleFailureReactionRequest)

        where:
        testText                 | _
        "<@FPA12L32> 9:3-9"      | _
        "<@FPA12L32> 09:3-9"     | _
        "<@FPA12L32> 9:3-09"     | _
        "<@FPA12L32> 09:3-09"    | _
        "<@FPA12L32> 9-9:3"      | _
        "<@FPA12L32> 09-9:3"     | _
        "<@FPA12L32> 9:3-9:3"    | _
        "<@FPA12L32> 9:30-9:3"   | _
        "<@FPA12L32> 09:3-9:3"   | _
        "<@FPA12L32> 09:30-9:3"  | _
        "<@FPA12L32> 9:3-9:30"   | _
        "<@FPA12L32> 09:3-9:30"  | _
        "<@FPA12L32> 9-09:3"     | _
        "<@FPA12L32> 09-09:3"    | _
        "<@FPA12L32> 9:3-09:3"   | _
        "<@FPA12L32> 9:30-09:3"  | _
        "<@FPA12L32> 09:3-09:3"  | _
        "<@FPA12L32> 09:30-09:3" | _
        "<@FPA12L32> 9:3-09:30"  | _
        "<@FPA12L32> 09:3-09:30" | _
    }

    def "handleMention() WHEN mentioning user is not found SHOULD mark message with failure emoji"() {
        given:
        slack.client() >> Mock(MethodsClient) {
            usersProfileGet(_ as UsersProfileGetRequest) >> Mock(UsersProfileGetResponse) {
                isOk() >> false
            }
        }
        sampleMentionEvent.getText() >> "<@FPA12L32>"
        sampleMentionEvent.getUser() >> sampleUserSlackId
        userService.get(_ as String) >> {throw NoSuchUserException.invalidEmail()}

        when:
        handler.handleMention(sampleMentionPayload, sampleContext)

        then:
        0 * presenceConfirmationService.confirmPresence(sampleUser, _ as PresenceConfirmationInputOutput)
        1 * slack.client().reactionsAdd(sampleFailureReactionRequest)
    }

    def "handleDirectMessage() WHEN mentioning user is present and didn't specify presence time SHOULD confirm his presence with time based on his work time"() {
        given:
        slack.client() >> Mock(MethodsClient) {
            usersProfileGet(_ as UsersProfileGetRequest) >> Mock(UsersProfileGetResponse) {
                isOk() >> true
                getProfile() >> Mock(com.slack.api.model.User.Profile) {
                    getEmail() >> sampleUserMail
                }
            }
        }
        sampleMessageEvent.getText() >> "<@FPA12L32>"
        sampleMessageEvent.getUser() >> sampleUserSlackId
        userService.get(sampleUserMail) >> sampleUser

        when:
        handler.handleDirectMessage(sampleMessagePayload, sampleContext)

        then:
        1 * presenceConfirmationService.confirmPresence(sampleUser, {
            it instanceof PresenceConfirmationInputOutput
            it.getDate() == LocalDate.now()
            it.getStartTime() == LocalTime.of(8, 0)
            it.getEndTime() == LocalTime.of(8, 0).plusMinutes((60 * sampleUser.getWorkTime()) as long)
            it.getUserId() == sampleUserId
        })
        1 * slack.client().reactionsAdd(sampleSuccessReactionRequest)
    }

    def "handleDirectMessage() WHEN mentioning user is present and specified correct presence time SHOULD parse time from message correctly"() {
        given:
        slack.client() >> Mock(MethodsClient) {
            usersProfileGet(_ as UsersProfileGetRequest) >> Mock(UsersProfileGetResponse) {
                isOk() >> true
                getProfile() >> Mock(com.slack.api.model.User.Profile) {
                    getEmail() >> sampleUserMail
                }
            }
        }
        sampleMessageEvent.getText() >> testText
        sampleMessageEvent.getUser() >> sampleUserSlackId
        userService.get(sampleUserMail) >> sampleUser

        when:
        handler.handleDirectMessage(sampleMessagePayload, sampleContext)

        then:
        1 * presenceConfirmationService.confirmPresence(sampleUser, {
            it instanceof PresenceConfirmationInputOutput
            it.getDate() == LocalDate.now()
            it.getStartTime() == LocalTime.of(stHour, stMinute)
            it.getEndTime() == LocalTime.of(etHour, etMinute)
            it.getUserId() == sampleUserId
        })
        1 * slack.client().reactionsAdd(sampleSuccessReactionRequest)

        where:
        testText                  | stHour | stMinute | etHour | etMinute
        "<@FPA12L32> 9-9"         | 9      | 0        | 9      | 0
        "<@FPA12L32> 09-9"        | 9      | 0        | 9      | 0
        "<@FPA12L32> 9:30-9"      | 9      | 30       | 9      | 0
        "<@FPA12L32> 09:30-9"     | 9      | 30       | 9      | 0
        "<@FPA12L32> 9-09"        | 9      | 0        | 9      | 0
        "<@FPA12L32> 09-09"       | 9      | 0        | 9      | 0
        "<@FPA12L32> 9:30-09"     | 9      | 30       | 9      | 0
        "<@FPA12L32> 09:30-09"    | 9      | 30       | 9      | 0
        "<@FPA12L32> 9-9:30"      | 9      | 0        | 9      | 30
        "<@FPA12L32> 09-9:30"     | 9      | 0        | 9      | 30
        "<@FPA12L32> 9:30-9:30"   | 9      | 30       | 9      | 30
        "<@FPA12L32> 09:30-9:30"  | 9      | 30       | 9      | 30
        "<@FPA12L32> 9-09:30"     | 9      | 0        | 9      | 30
        "<@FPA12L32> 09-09:30"    | 9      | 0        | 9      | 30
        "<@FPA12L32> 9:30-09:30"  | 9      | 30       | 9      | 30
        "<@FPA12L32> 09:30-09:30" | 9      | 30       | 9      | 30
    }

    def "handleDirectMessage() WHEN mentioning user is present and specified invalid presence time SHOULD mark message with failure emoji"() {
        given:
        slack.client() >> Mock(MethodsClient) {
            usersProfileGet(_ as UsersProfileGetRequest) >> Mock(UsersProfileGetResponse) {
                isOk() >> true
                getProfile() >> Mock(com.slack.api.model.User.Profile) {
                    getEmail() >> sampleUserMail
                }
            }
        }
        sampleMessageEvent.getText() >> testText
        sampleMessageEvent.getUser() >> sampleUserSlackId
        userService.get(sampleUserMail) >> sampleUser

        when:
        handler.handleDirectMessage(sampleMessagePayload, sampleContext)

        then:
        0 * presenceConfirmationService.confirmPresence(sampleUser, _ as PresenceConfirmationInputOutput)
        1 * slack.client().reactionsAdd(sampleFailureReactionRequest)

        where:
        testText                 | _
        "<@FPA12L32> 9:3-9"      | _
        "<@FPA12L32> 09:3-9"     | _
        "<@FPA12L32> 9:3-09"     | _
        "<@FPA12L32> 09:3-09"    | _
        "<@FPA12L32> 9-9:3"      | _
        "<@FPA12L32> 09-9:3"     | _
        "<@FPA12L32> 9:3-9:3"    | _
        "<@FPA12L32> 9:30-9:3"   | _
        "<@FPA12L32> 09:3-9:3"   | _
        "<@FPA12L32> 09:30-9:3"  | _
        "<@FPA12L32> 9:3-9:30"   | _
        "<@FPA12L32> 09:3-9:30"  | _
        "<@FPA12L32> 9-09:3"     | _
        "<@FPA12L32> 09-09:3"    | _
        "<@FPA12L32> 9:3-09:3"   | _
        "<@FPA12L32> 9:30-09:3"  | _
        "<@FPA12L32> 09:3-09:3"  | _
        "<@FPA12L32> 09:30-09:3" | _
        "<@FPA12L32> 9:3-09:30"  | _
        "<@FPA12L32> 09:3-09:30" | _
    }

    def "handleDirectMessage() WHEN mentioning user is not found SHOULD mark message with failure emoji"() {
        given:
        slack.client() >> Mock(MethodsClient) {
            usersProfileGet(_ as UsersProfileGetRequest) >> Mock(UsersProfileGetResponse) {
                isOk() >> false
            }
        }
        sampleMessageEvent.getText() >> "<@FPA12L32>"
        sampleMessageEvent.getUser() >> sampleUserSlackId
        userService.get(_ as String) >> {throw NoSuchUserException.invalidEmail()}

        when:
        handler.handleDirectMessage(sampleMessagePayload, sampleContext)

        then:
        0 * presenceConfirmationService.confirmPresence(sampleUser, _ as PresenceConfirmationInputOutput)
        1 * slack.client().reactionsAdd(sampleFailureReactionRequest)
    }
}
