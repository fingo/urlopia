package info.fingo.urlopia.config.mail.receive

import spock.lang.Specification

class MailParserSpec extends Specification{

    def "isAcceptedByMail WHEN decision start with #response SHOULD return #isAccepted"(){
        given:
        def decision = response + " some message"
        def mailParser = new MailParser()

        when:
        def result = mailParser.isAcceptedByMail(decision)

        then:
        result == isAccepted

        where:
        response | isAccepted
        "yes"    | true
        "tak"    | true
        "ok"     | true
        "YES"    | true
        "OK"     | true
        "Tak"    | true
        "nie"    | false
        "no"     | false
        "NO"     | false
        "NIE"    | false
        "Nie"    | false
    }

    def "isRejectedByMail WHEN decision start with #response SHOULD return isRejected"(){
        given:
        def decision = response + " some message";
        def mailParser = new MailParser();

        when:
        def result = mailParser.isRejectedByMail(decision)

        then:
        result == isRejected

        where:
        response | isRejected
        "yes"    | false
        "tak"    | false
        "ok"     | false
        "YES"    | false
        "OK"     | false
        "Tak"    | false
        "nie"    | true
        "no"     | true
        "NO"     | true
        "NIE"    | true
        "Nie"    | true
    }
}
