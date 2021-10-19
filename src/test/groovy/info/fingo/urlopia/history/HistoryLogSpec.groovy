package info.fingo.urlopia.history

import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestType
import info.fingo.urlopia.user.User
import spock.lang.Specification

class HistoryLogSpec extends Specification{
    def hours = 0
    def comment = ""
    def prevHistoryLog = Mock(HistoryLog)
    def firstDeciderName = "John Snow"
    def secondDeciderName = "Mary Smith"
    def user = Mock(User)
    def decider = Mock(User){
        getFullName() >> firstDeciderName
    }
    def decidersList = List.of(firstDeciderName,secondDeciderName)
    def onePersonList = List.of(firstDeciderName)



    def "getDeciderFullName WHEN called with requestType that is not null and  have different then SPECIAL SHOULD return request deciders"(){
        given: "valid requests"
        def firstRequest = Mock(Request){
            getType() >> RequestType.OCCASIONAL
            getDeciders() >> decidersList
        }

        def secondRequest = Mock(Request){
            getType() >> RequestType.OCCASIONAL
            getDeciders() >> decidersList
        }

        and: "valid historyLog"
        def firstHistoryLog = new HistoryLog(firstRequest,user,decider,hours,comment,prevHistoryLog)
        def secondHistoryLog = new HistoryLog(secondRequest,user,decider,hours,comment,prevHistoryLog)

        when:
        def firstDeciders = firstHistoryLog.getDeciderFullName();
        def secondDeciders = secondHistoryLog.getDeciderFullName();

        then:
        firstDeciders.containsAll(decidersList)
        secondDeciders.containsAll(decidersList)
    }

    def "getDeciderFullName WHEN called with request that have SPECIAL type SHOULD return historyLog deciders"(){
        given: "valid request"
        def firstRequest = Mock(Request){
            getType() >> RequestType.SPECIAL
            getDeciders() >> null
        }

        and: "valid historyLog"
        def firstHistoryLog = new HistoryLog(firstRequest,user,decider,hours,comment,prevHistoryLog)

        when:
        def firstDeciders = firstHistoryLog.getDeciderFullName();

        then:
        firstDeciders.containsAll(onePersonList)
    }

    def "getDeciderFull WHEN called with request that is null SHOULD return historyLog deciders"(){
        given: "valid request"
        def firstHistoryLog = new HistoryLog(null,user,decider,hours,comment,prevHistoryLog)

        when:
        def firstDeciders = firstHistoryLog.getDeciderFullName();

        then:
        firstDeciders.containsAll(onePersonList)
    }


}
