package info.fingo.urlopia.api.v2.request.occasional

import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService
import info.fingo.urlopia.history.HistoryLogService
import info.fingo.urlopia.holidays.WorkingDaysCalculator
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestInput
import info.fingo.urlopia.request.RequestRepository
import info.fingo.urlopia.request.absence.InvalidDatesOrderException
import info.fingo.urlopia.request.occasional.OccasionalRequestService
import info.fingo.urlopia.user.User
import info.fingo.urlopia.user.UserRepository
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Specification

import java.time.LocalDate

class OccasionalRequestServiceSpec extends Specification{
    private  RequestRepository requestRepository;

    private  UserRepository userRepository;

    private  HistoryLogService historyLogService;

    private  WorkingDaysCalculator workingDaysCalculator;

    private  ApplicationEventPublisher publisher;

    private  OccasionalRequestService occasionalRequestService

    private PresenceConfirmationService presenceConfirmationService;

    void setup(){
        requestRepository = Mock(RequestRepository)
        userRepository = Mock(UserRepository)
        historyLogService = Mock(HistoryLogService)
        workingDaysCalculator = Mock(WorkingDaysCalculator)
        publisher = Mock(ApplicationEventPublisher)
        presenceConfirmationService = Mock(PresenceConfirmationService)
        occasionalRequestService = new OccasionalRequestService(requestRepository,userRepository,
                                                                historyLogService,workingDaysCalculator,publisher,
                                                                presenceConfirmationService)
    }

    def "create WHEN requestInput have endDate before startDate SHOULD throw InvalidRequestTimePeriodException"(){
        given: "user Mock with repo"
        def userId = 5;
        def user = Mock(User){
            getId() >> userId
        }
        userRepository.findById(userId) >> Optional.of(user)

        and: "workingDaysCalculator mock"
        workingDaysCalculator.calculate(_ as LocalDate, _ as LocalDate) >> 1

        and: "requestInput mock that have endDate before startDate"
        def startDate = LocalDate.of(2021,1,2)
        def endDate =  LocalDate.of(2021,1,1)
        def requestInput = Mock(RequestInput){
            getStartDate() >> startDate
            getEndDate() >> endDate
        }

        when:
        occasionalRequestService.create(userId,requestInput)

        then:
        thrown(InvalidDatesOrderException)
    }

    def "create WHEN requestInput with valid data SHOULD not throw any exceptions"(){
        given: "user Mock with repo"
        def userId = 5;
        def user = Mock(User){
            getId() >> userId
        }
        userRepository.findById(userId) >> Optional.of(user)

        and: "workingDaysCalculator mock"
        workingDaysCalculator.calculate(_ as LocalDate, _ as LocalDate) >> 1

        and: "request mock with repo"
        def typeInfo = Mock(Request.TypeInfo){
            getInfo() >> ""
        }
        def request = Mock(Request){
            getTerm() >> ""
            getTypeInfo() >> typeInfo

        }
        requestRepository.save(_ as Request) >> request

        and: "requestInput mock that have endDate before startDate"
        def startDate = LocalDate.of(2021,1,2)
        def endDate =  LocalDate.of(2021,1,3)
        def requestInput = Mock(RequestInput){
            getStartDate() >> startDate
            getEndDate() >> endDate
        }

        when:
        occasionalRequestService.create(userId,requestInput)

        then:
        noExceptionThrown()
    }

}
