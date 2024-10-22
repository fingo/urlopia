package info.fingo.urlopia.config.ad


import spock.lang.Specification

class ActiveDirectoryUtilsTest extends Specification {

    def "getParentDN WHEN parent exists SHOULD return it"() {
        when:
        def result = ActiveDirectoryUtils.getParentDN(actualDN)

        then:
        result == parentDN

        where:
        actualDN                                                         | parentDN
        "OU=Some name with\\, comma,OU=SomeTeam,OU=Teams,DC=fingo,DC=pl" | "OU=SomeTeam,OU=Teams,DC=fingo,DC=pl"
        "OU=SomeNestedTeam,OU=SomeTeam,OU=Teams,DC=fingo,DC=pl"          | "OU=SomeTeam,OU=Teams,DC=fingo,DC=pl"
        "OU=SomeTeam,OU=Teams,DC=fingo,DC=pl"                            | "OU=Teams,DC=fingo,DC=pl"
        "OU=Teams"                                                       | ""
        ""                                                               | ""
    }

    def "getRelativeDN WHEN base matches, ends with or differs SHOULD return correct relative DN"() {
        when:
        def result = ActiveDirectoryUtils.getRelativeDN(distinguishedName, base)

        then:
        result == expectedRelativeDN

        where:
        distinguishedName                                       | base                                  | expectedRelativeDN
        "OU=SomeNestedTeam,OU=SomeTeam,OU=Teams,DC=fingo,DC=pl" | "OU=SomeTeam,OU=Teams,DC=fingo,DC=pl" | "OU=SomeNestedTeam"
        "OU=SomeTeam,OU=Teams,DC=fingo,DC=pl"                   | "OU=Teams,DC=fingo,DC=pl"             | "OU=SomeTeam"
        "OU=Teams,DC=fingo,DC=pl"                               | "OU=Teams,DC=fingo,DC=pl"             | ""
        "OU=RandomTeam,OU=SomeTeam,OU=Teams,DC=fingo,DC=pl"     | "OU=SomeTeam,OU=Teams,DC=fingo,DC=pl" | "OU=RandomTeam"
        "OU=Teams,DC=fingo,DC=pl"                               | "OU=DifferentBase,DC=fingo,DC=pl"     | "OU=Teams,DC=fingo,DC=pl"
        ""                                                      | ""                                    | ""
        "OU=SomeTeam,OU=Teams,DC=fingo,DC=pl"                   | ""                                    | "OU=SomeTeam,OU=Teams,DC=fingo,DC=pl"
    }


}
