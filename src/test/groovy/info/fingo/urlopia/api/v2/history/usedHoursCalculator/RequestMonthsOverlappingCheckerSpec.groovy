package info.fingo.urlopia.api.v2.history.usedHoursCalculator

import info.fingo.urlopia.request.Request
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.YearMonth

class RequestMonthsOverlappingCheckerSpec extends Specification {

    def requestMonthsOverlappingChecker = new RequestMonthsOverlappingChecker()

    @Unroll
    def "requestNotOverlapNextMonth WHEN called with non overlapping request SHOULD return true"(){
        given:
        def givenYear = requestDTO.dateToCheck().getYear()
        def givenMonth = requestDTO.dateToCheck().getMonthValue()

        when:
        def result = requestMonthsOverlappingChecker.requestNotOverlapOtherMonth(givenYear, givenMonth, requestDTO.request())

        then:
        result

        where:
        requestDTO << getNonOverlappingRequests()
    }

    @Unroll
    def "requestNotOverlapNextMonth WHEN called with overlapping request by next months SHOULD return false"(){
        given:
        def givenYear = requestDTO.dateToCheck().getYear()
        def givenMonth = requestDTO.dateToCheck().getMonthValue()

        when:
        def result = requestMonthsOverlappingChecker.requestNotOverlapOtherMonth(givenYear, givenMonth, requestDTO.request())

        then:
        !result

        where:
        requestDTO << getNextMonthOverlappingRequests()
    }


    @Unroll
    def "requestNotOverlapNextMonth WHEN called with overlapping request by prev months SHOULD return false"(){
        given:
        def givenYear = requestDTO.dateToCheck().getYear()
        def givenMonth = requestDTO.dateToCheck().getMonthValue()

        when:
        def result = requestMonthsOverlappingChecker.requestNotOverlapOtherMonth(givenYear, givenMonth, requestDTO.request())

        then:
        !result

        where:
        requestDTO << getPrevMonthOverlappingRequests()
    }

    @Unroll
    def "requestNotOverlapNextMonth WHEN called with overlapping request by prev and next months SHOULD return false"(){
        given:
        def givenYear = requestDTO.dateToCheck().getYear()
        def givenMonth = requestDTO.dateToCheck().getMonthValue()

        when:
        def result = requestMonthsOverlappingChecker.requestNotOverlapOtherMonth(givenYear, givenMonth, requestDTO.request())

        then:
        !result

        where:
        requestDTO << getPrevAndNexMonthOverlappingRequests()
    }



    @Unroll
    def "requestOverlapOnlyPrevMonths WHEN called with request tha overlap by prev months SHOULD return true"(){
        given:
        def givenYear = requestDTO.dateToCheck().getYear()
        def givenMonth = requestDTO.dateToCheck().getMonthValue()

        when:
        def result = requestMonthsOverlappingChecker.requestOverlapOnlyPrevMonths(givenYear, givenMonth, requestDTO.request())

        then:
        result

        where:
        requestDTO << getPrevMonthOverlappingRequests()
    }

    @Unroll
    def "requestOverlapOnlyPrevMonths WHEN called with overlapping request by next months SHOULD return false"(){
        given:
        def givenYear = requestDTO.dateToCheck().getYear()
        def givenMonth = requestDTO.dateToCheck().getMonthValue()

        when:
        def result = requestMonthsOverlappingChecker.requestOverlapOnlyPrevMonths(givenYear, givenMonth, requestDTO.request())

        then:
        !result

        where:
        requestDTO << getNextMonthOverlappingRequests()
    }


    @Unroll
    def "requestOverlapOnlyPrevMonths WHEN called with non overlapping request SHOULD return false"(){
        given:
        def givenYear = requestDTO.dateToCheck().getYear()
        def givenMonth = requestDTO.dateToCheck().getMonthValue()

        when:
        def result = requestMonthsOverlappingChecker.requestOverlapOnlyPrevMonths(givenYear, givenMonth, requestDTO.request())

        then:
        !result

        where:
        requestDTO << getNonOverlappingRequests()
    }

    @Unroll
    def "requestOverlapOnlyPrevMonths WHEN called with overlapping request by prev and next months SHOULD return false"(){
        given:
        def givenYear = requestDTO.dateToCheck().getYear()
        def givenMonth = requestDTO.dateToCheck().getMonthValue()

        when:
        def result = requestMonthsOverlappingChecker.requestOverlapOnlyPrevMonths(givenYear, givenMonth, requestDTO.request())

        then:
        !result

        where:
        requestDTO << getPrevAndNexMonthOverlappingRequests()
    }


    @Unroll
    def "requestOverlapOnlyNextMonths WHEN called with request tha overlap by next months SHOULD return true"(){
        given:
        def givenYear = requestDTO.dateToCheck().getYear()
        def givenMonth = requestDTO.dateToCheck().getMonthValue()

        when:
        def result = requestMonthsOverlappingChecker.requestOverlapOnlyNextMonths(givenYear, givenMonth, requestDTO.request())

        then:
        result

        where:
        requestDTO << getNextMonthOverlappingRequests()
    }

    @Unroll
    def "requestOverlapOnlyNextMonths WHEN called with overlapping request by prev months SHOULD return false"(){
        given:
        def givenYear = requestDTO.dateToCheck().getYear()
        def givenMonth = requestDTO.dateToCheck().getMonthValue()

        when:
        def result = requestMonthsOverlappingChecker.requestOverlapOnlyNextMonths(givenYear, givenMonth, requestDTO.request())

        then:
        !result

        where:
        requestDTO << getPrevMonthOverlappingRequests()
    }


    @Unroll
    def "requestOverlapOnlyNextMonths WHEN called with non overlapping request SHOULD return false"(){
        given:
        def givenYear = requestDTO.dateToCheck().getYear()
        def givenMonth = requestDTO.dateToCheck().getMonthValue()

        when:
        def result = requestMonthsOverlappingChecker.requestOverlapOnlyNextMonths(givenYear, givenMonth, requestDTO.request())

        then:
        !result

        where:
        requestDTO << getNonOverlappingRequests()
    }

    @Unroll
    def "requestOverlapOnlyNextMonths WHEN called with overlapping request by prev and next months SHOULD return false"(){
        given:
        def givenYear = requestDTO.dateToCheck().getYear()
        def givenMonth = requestDTO.dateToCheck().getMonthValue()

        when:
        def result = requestMonthsOverlappingChecker.requestOverlapOnlyNextMonths(givenYear, givenMonth, requestDTO.request())

        then:
        !result

        where:
        requestDTO << getPrevAndNexMonthOverlappingRequests()
    }


    @Unroll
    def "requestOverlapNextAndPrevMonth WHEN called with request tha overlap by next and prevs months SHOULD return true"(){
        given:
        def givenYear = requestDTO.dateToCheck().getYear()
        def givenMonth = requestDTO.dateToCheck().getMonthValue()

        when:
        def result = requestMonthsOverlappingChecker.requestOverlapNextAndPrevMonth(givenYear, givenMonth, requestDTO.request())

        then:
        result

        where:
        requestDTO << getPrevAndNexMonthOverlappingRequests()
    }

    @Unroll
    def "requestOverlapNextAndPrevMonth WHEN called with overlapping request by prev months SHOULD return false"(){
        given:
        def givenYear = requestDTO.dateToCheck().getYear()
        def givenMonth = requestDTO.dateToCheck().getMonthValue()

        when:
        def result = requestMonthsOverlappingChecker.requestOverlapNextAndPrevMonth(givenYear, givenMonth, requestDTO.request())

        then:
        !result

        where:
        requestDTO << getPrevMonthOverlappingRequests()
    }


    @Unroll
    def "requestOverlapNextAndPrevMonth WHEN called with non overlapping request SHOULD return false"(){
        given:
        def givenYear = requestDTO.dateToCheck().getYear()
        def givenMonth = requestDTO.dateToCheck().getMonthValue()

        when:
        def result = requestMonthsOverlappingChecker.requestOverlapNextAndPrevMonth(givenYear, givenMonth, requestDTO.request())

        then:
        !result

        where:
        requestDTO << getNonOverlappingRequests()
    }

    @Unroll
    def "requestOverlapNextAndPrevMonth WHEN called with overlapping request by next months SHOULD return false"(){
        given:
        def givenYear = requestDTO.dateToCheck().getYear()
        def givenMonth = requestDTO.dateToCheck().getMonthValue()

        when:
        def result = requestMonthsOverlappingChecker.requestOverlapNextAndPrevMonth(givenYear, givenMonth, requestDTO.request())

        then:
        !result

        where:
        requestDTO << getNextMonthOverlappingRequests()
    }



    private List<RequestCheckDTO> getNonOverlappingRequests(){
        def notOverlappingRequest = Mock(Request){
            getStartDate() >> LocalDate.of(2021, 1,1)
            getEndDate() >> LocalDate.of(2021, 1,1)
        }
        def dateToCheck = YearMonth.of(2021,1)
        def requestCheckDTO = new RequestCheckDTO(notOverlappingRequest, dateToCheck)

        return [requestCheckDTO]
    }

    private List<RequestCheckDTO> getNextMonthOverlappingRequests(){

        def dateToCheck = YearMonth.of(2021,2)

        def overlapNextMonthInSameYear = Mock(Request){
            getStartDate() >> LocalDate.of(2021, 2,1)
            getEndDate() >> LocalDate.of(2021, 3,1)
        }
        def requestCheckDTO = new RequestCheckDTO(overlapNextMonthInSameYear, dateToCheck)


        def overlapNextMonthInNextYear = Mock(Request){
            getStartDate() >> LocalDate.of(2021, 2,1)
            getEndDate() >> LocalDate.of(2022, 1,1)
        }
        def requestCheckDTO2 = new RequestCheckDTO(overlapNextMonthInNextYear, dateToCheck)


        return [requestCheckDTO, requestCheckDTO2]
    }

    private List<RequestCheckDTO> getPrevMonthOverlappingRequests(){

        def dateToCheck = YearMonth.of(2021,3)

        def overlapPrevMonthInSameYear = Mock(Request){
            getStartDate() >> LocalDate.of(2021, 2,1)
            getEndDate() >> LocalDate.of(2021, 3,1)
        }
        def requestCheckDTO = new RequestCheckDTO(overlapPrevMonthInSameYear, dateToCheck)

        def overlapPrevMonthInNextYear = Mock(Request){
            getStartDate() >> LocalDate.of(2020, 4,1)
            getEndDate() >> LocalDate.of(2021, 3,1)
        }
        def requestCheckDTO2 = new RequestCheckDTO(overlapPrevMonthInNextYear, dateToCheck)

        return [requestCheckDTO, requestCheckDTO2]
    }

    private List<RequestCheckDTO> getPrevAndNexMonthOverlappingRequests(){

        def dateToCheck = YearMonth.of(2021,3)

        def overlapNextAndPrevMonthInSameYear = Mock(Request){
            getStartDate() >> LocalDate.of(2021, 2,1)
            getEndDate() >> LocalDate.of(2021, 4,1)
        }
        def requestCheckDTO = new RequestCheckDTO(overlapNextAndPrevMonthInSameYear, dateToCheck)

        def overlapNextByYearAndPrevMonth = Mock(Request){
            getStartDate() >> LocalDate.of(2021, 2,1)
            getEndDate() >> LocalDate.of(2022, 1,1)
        }
        def requestCheckDTO2 = new RequestCheckDTO(overlapNextByYearAndPrevMonth, dateToCheck)

        def overlapNextAndPrevByYearMonth = Mock(Request){
            getStartDate() >> LocalDate.of(2020, 2,1)
            getEndDate() >> LocalDate.of(2021, 4,1)
        }
        def requestCheckDTO3 = new RequestCheckDTO(overlapNextAndPrevByYearMonth, dateToCheck)

        return [requestCheckDTO, requestCheckDTO2, requestCheckDTO3]

    }
}
