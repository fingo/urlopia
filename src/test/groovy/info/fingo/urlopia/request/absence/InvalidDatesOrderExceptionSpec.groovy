package info.fingo.urlopia.request.absence

import org.springframework.http.HttpStatus
import spock.lang.Specification

class InvalidDatesOrderExceptionSpec extends Specification {
    private static final String EXPECTED_MESSAGE = "End date is before start date"
    private static final HttpStatus EXPECTED_HTTPS_STATUS = HttpStatus.CONFLICT

    def "invalidDatesOrder WHEN called SHOULD return InvalidDatesOrderException with expected message and status"() {

        when:
        def result = InvalidDatesOrderException.invalidDatesOrder()

        then:
        result.getMessage() == EXPECTED_MESSAGE
        result.getHttpStatus() == EXPECTED_HTTPS_STATUS
    }

}