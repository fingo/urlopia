package info.fingo.urlopia.api.v2.request.normal

import info.fingo.urlopia.acceptance.AcceptanceService
import info.fingo.urlopia.history.HistoryLogService
import info.fingo.urlopia.holidays.WorkingDaysCalculator
import info.fingo.urlopia.request.NotEnoughDaysException
import info.fingo.urlopia.request.Request
import info.fingo.urlopia.request.RequestInput
import info.fingo.urlopia.request.RequestOverlappingException
import info.fingo.urlopia.request.RequestRepository
import info.fingo.urlopia.request.absence.InvalidDatesOrderException
import info.fingo.urlopia.request.normal.NormalRequestService
import info.fingo.urlopia.team.Team
import info.fingo.urlopia.user.User
import info.fingo.urlopia.user.UserRepository
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Specification

import java.time.LocalDate

class NormalRequestServiceSpec extends Specification{
    private  RequestRepository requestRepository;

    private  UserRepository userRepository;

    private  HistoryLogService historyLogService;

    private  WorkingDaysCalculator workingDaysCalculator;

    private  ApplicationEventPublisher publisher;

    private  AcceptanceService acceptanceService;

    private NormalRequestService normalRequestService

    void setup(){
        requestRepository = Mock(RequestRepository)
        userRepository = Mock(UserRepository)
        historyLogService = Mock(HistoryLogService)
        workingDaysCalculator = Mock(WorkingDaysCalculator)
        publisher = Mock(ApplicationEventPublisher)
        acceptanceService = Mock(AcceptanceService)
        normalRequestService = new NormalRequestService(requestRepository, userRepository,historyLogService,
                                                        workingDaysCalculator,publisher,acceptanceService)

    }


    def "create WHEN user don't have enough hours for request SHOULD throw NotEnoughDaysException"(){
        given: "user Mock with repo"
        def userWorkTime = 1.0f
        def userId = 5;
        def user = Mock(User){
            getId() >> userId
            getWorkTime() >> userWorkTime
        }
        userRepository.findById(userId) >> Optional.of(user)

        and: "request mock with repo"
        def workingDays = 100
        def request = Mock(Request){
            isPending() >> true
            isNormal() >> true
            getWorkingDays() >> workingDays
            getRequester() >> user
        }
        requestRepository.findByRequesterId(userId) >> [request]

        and: "historyLogService mock"
        historyLogService.countRemainingHours(_ as Long) >> 1

        and: "workingDaysCalculator mock that return more hours required then user have"
        workingDaysCalculator.calculate(_ as LocalDate, _ as LocalDate) >> 10000000

        when:

        normalRequestService.create(userId,Mock(RequestInput))

        then:
        thrown(NotEnoughDaysException)
    }

    def "create WHEN requestInput overlap existing user's request SHOULD throw RequestOverlappingException"(){
        given: "user Mock with repo"
        def userWorkTime = 1.0f
        def userId = 5;
        def user = Mock(User){
            getId() >> userId
            getWorkTime() >> userWorkTime
        }
        userRepository.findById(userId) >> Optional.of(user)

        and: "request mock with repo with endDate before startDate"
        def workingDays = 1
        def request = Mock(Request){
            getWorkingDays() >> workingDays
            getRequester() >> user
            isAffecting() >> true
            isOverlapping(_ as Request) >> true
        }
        requestRepository.findByRequesterId(userId) >> [request]

        and: "historyLogService mock"
        historyLogService.countRemainingHours(_ as Long) >> 1000

        and: "workingDaysCalculator mock"
        workingDaysCalculator.calculate(_ as LocalDate, _ as LocalDate) >> 1

        and: "requestInput mock that overlapping existing user's request "
        def requestInput = Mock(RequestInput){
            getStartDate() >> LocalDate.of(2021,1,1)
            getEndDate() >> LocalDate.of(2021,1,2)
        }

        when:
        normalRequestService.create(userId,requestInput)

        then:
        thrown(RequestOverlappingException)

    }

    def "create WHEN requestInput have endDate before startDate SHOULD throw InvalidRequestTimePeriodException"(){
        given: "user Mock with repo"
        def userId = 5;
        def user = Mock(User){
            getId() >> userId
        }
        userRepository.findById(userId) >> Optional.of(user)

        and: "request mock with repo"
        def request = Mock(Request){
            getRequester() >> user
        }
        requestRepository.findByRequesterId(userId) >> [request]

        and: "historyLogService mock"
        historyLogService.countRemainingHours(_ as Long) >> 1000

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
        normalRequestService.create(userId,requestInput)

        then:
        thrown(InvalidDatesOrderException)
    }

    def "create WHEN called with valid Data SHOULD not throw any exception"(){
        given: "team mock"
        def team = Mock(Team){
            getLeader() >> Mock(User)
        }
        and: "user Mock with repo"

        def userId = 5;
        def user = Mock(User){
            getId() >> userId
            getTeams() >> Set.of(team)
        }
        userRepository.findById(userId) >> Optional.of(user)

        and: "request mock with repo with endDate before startDate"
        def request = Mock(Request){
            getRequester() >> user
            getId() >> 2
        }
        requestRepository.findByRequesterId(userId) >> [request]
        requestRepository.save(_ as Request) >> request
        requestRepository.findById(_ as Long) >> Optional<Request>.of(request)

        and: "historyLogService mock"
        historyLogService.countRemainingHours(_ as Long) >> 1000

        and: "workingDaysCalculator mock"
        workingDaysCalculator.calculate(_ as LocalDate, _ as LocalDate) >> 1

        and: "valid requestInput"
        def startDate = LocalDate.of(2021,1,2)
        def endDate =  LocalDate.of(2021,1,2)
        def requestInput = Mock(RequestInput){
            getStartDate() >> startDate
            getEndDate() >> endDate
        }

        when:
        normalRequestService.create(userId,requestInput)

        then:
        noExceptionThrown()
    }
}
