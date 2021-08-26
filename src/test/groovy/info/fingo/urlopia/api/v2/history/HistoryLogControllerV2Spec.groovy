package info.fingo.urlopia.api.v2.history

import info.fingo.urlopia.config.authentication.WebTokenService
import info.fingo.urlopia.config.persistance.filter.Filter
import info.fingo.urlopia.history.HistoryLogExcerptProjection
import info.fingo.urlopia.history.HistoryLogService
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import java.time.LocalDateTime

class HistoryLogControllerV2Spec extends Specification{

    def userId = 1L

    def projection = Mock(HistoryLogExcerptProjection) {
        getId() >> 1L
        getCreated() >> LocalDateTime.now()
        getComment() >> "comment"
        getDeciderFullName() >> "Sample decider"
        getWorkTimeDenominator() >> 1
        getWorkTimeNumerator() >> 1
        getUserWorkTime() >> 8.0
        getHours() >> 1.0
        getHoursRemaining() >> 10.0
    }

    def historyLogService = Mock(HistoryLogService) {
        get(_ as Long, null, _ as Filter) >> List.of(projection)
    }
    def webTokenService = Mock(WebTokenService)

    def historyLogControllerV2 = new HistoryLogControllerV2(historyLogService)

    def "getHistoryLogs() SHOULD return List of caller's instances of HistoryLogOutput" () {
        given:
        def authenticatedUserId = 2L
        def request = Mock(HttpServletRequest) {
            getAttribute(_ as String) >> authenticatedUserId
        }
        projection.getId() >> authenticatedUserId


        when:
        def result = historyLogControllerV2.getHistoryLogs(request, null, [] as String)

        then:
        result instanceof List<HistoryLogOutput>
    }

    def "getSpecificHistoryLogs() WHEN called by admin SHOULD return List of HistoryLogOutput"() {
        given:
        webTokenService.isCurrentUserAnAdmin() >> true

        when:
        def result = historyLogControllerV2.getSpecificHistoryLogs(userId, null, [] as String)

        then:
        result instanceof List<HistoryLogOutput>
    }

    def "getSpecificHistoryLogs() WHEN called by admin SHOULD NOT throw" () {
        given:
        webTokenService.isCurrentUserAnAdmin() >> true

        when:
        historyLogControllerV2.getSpecificHistoryLogs(userId, null, [] as String)

        then:
        notThrown(Exception)
    }
}
