package info.fingo.urlopia.config.mail.receive

import info.fingo.urlopia.config.mail.Mail
import spock.lang.Specification

class EmailBounceDetectorSpec extends Specification{

    private EmailBounceDetector emailBounceDetector
    private Mail mail

    def setup(){
        emailBounceDetector = new EmailBounceDetector();
        mail = Mock(Mail)
    }

    def "isBounce() WHEN get bounce mail SHOULD recognize it by sender address and return true"(){
        given:
        def subject = "Your vacation has been canceled"
        mail.getSenderAddress() >> mailAddress
        mail.getSubject() >> subject

        def expectedResult = true

        when:
        def result = emailBounceDetector.isBounce(mail);

        then:
        result == expectedResult

        where:
        _ | mailAddress
        _ | "noreply@some.domain"
        _ | "MAILER-DAEMON@some.domain"
        _ | "mailer-daemon@some.domain"
    }

    def "isBounce() WHEN get bounce mail address SHOULD recognize it by subject and return true"(){
        given:
        def mailAddress = "validMail@some.domain"
        mail.getSenderAddress() >> mailAddress
        mail.getSubject() >> subject

        def expectedResult = true

        when:
        def result = emailBounceDetector.isBounce(mail);

        then:
        result == expectedResult

        where:
        _ | subject
        _ | "Undeliverable Mail"
        _ | "Undeliverable "
        _ | "Undelivered Mail Returned to Sender "
    }

    def "isBounce() WHEN get normal mail SHOULD return false"(){
        given:
        mail.getSenderAddress() >> mailAddress
        mail.getSubject() >> subject

        def expectedResult = false

        when:
        def result = emailBounceDetector.isBounce(mail);

        then:
        result == expectedResult

        where:
        subject                                             | mailAddress
        "Undeliverable gift"                                | "someNormalMailAddress@some.domain"
        "Undeliverable vacation days"                       | "someNormalMailAddress@some.domain"
        "some normal Subject"                               | "fakeNoreply@some.domain"
        "some normal Subject"                               | "MAILER-DAEMON-FAKE@some.domain"
        "Your vacation has been canceled "                  | "Fake-MAILER-DAEMON@some.domain"
        "Your vacation has been canceled"                   | "validMail@some.domain"
    }

}
