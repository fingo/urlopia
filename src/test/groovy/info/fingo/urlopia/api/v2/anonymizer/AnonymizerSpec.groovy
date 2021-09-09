package info.fingo.urlopia.api.v2.anonymizer

import spock.lang.Specification

class AnonymizerSpec extends Specification {
    def "anonymizeMail() SHOULD replace name of mail address to anonymized one"() {
        given:
        def mail1 = "a@gmail.com"
        def mail2 = "bc@outlook.com"
        def mail3 = "def@interia.pl"
        def mail4 = "ghij@onet.eu"
        def mail5 = "klmnop@gmail.com"
        def mail6 = "asgsjg.kds132@gmail.com"
        def mail7 = "@gmail.com"

        expect:
        Anonymizer.anonymizeMail(mail1) == ".@gmail.com"
        Anonymizer.anonymizeMail(mail2) == "..@outlook.com"
        Anonymizer.anonymizeMail(mail3) == "...@interia.pl"
        Anonymizer.anonymizeMail(mail4) == "g..j@onet.eu"
        Anonymizer.anonymizeMail(mail5) == "k....p@gmail.com"
        Anonymizer.anonymizeMail(mail6) == "a...........2@gmail.com"
        Anonymizer.anonymizeMail(mail7) == "@gmail.com"
    }

    def "anonymizeSubject() SHOULD replace subject to anonymized one"() {
        given:
        def subject1 = ""
        def subject2 = "123456789"
        def subject3 = "1234567890123456789"
        def subject4 = "12345678901234567890"
        def subject5 = "123456789012345678901"
        def subject6 = "1234567890123456789012"
        def subject7 = "1234567890123456789012345678"

        expect:
        Anonymizer.anonymizeSubject(subject1) == ""
        Anonymizer.anonymizeSubject(subject2) == "........."
        Anonymizer.anonymizeSubject(subject3) == "..................."
        Anonymizer.anonymizeSubject(subject4) == "...................."
        Anonymizer.anonymizeSubject(subject5) == "12345...........78901"
        Anonymizer.anonymizeSubject(subject6) == "12345............89012"
        Anonymizer.anonymizeSubject(subject7) == "12345..................45678"
    }

    def "anonymizeYearlyReportFileName() SHOULD replace yearly report filename to anonymized one"() {
        given:
        def fileName = "ewidencja_czasu_pracy_2000_AbcdeFghijk.xlsx"

        expect:
        Anonymizer.anonymizeYearlyReportFileName(fileName) == "ewidencja_czasu_pracy_2000_....xlsx"
    }
}
