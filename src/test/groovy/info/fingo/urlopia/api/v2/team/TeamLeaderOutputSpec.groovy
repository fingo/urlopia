package info.fingo.urlopia.api.v2.team

import info.fingo.urlopia.api.v2.user.UserOutput
import info.fingo.urlopia.team.TeamExcerptProjection
import spock.lang.Specification

class TeamLeaderOutputSpec extends Specification{
    private TeamExcerptProjection teamExcerptProjection

    void setup(){
        teamExcerptProjection = Mock(TeamExcerptProjection)
    }
    def "fromTeamExcerptProjection() WHEN called with empty leaderId SHOULD return null"(){
        given:
        teamExcerptProjection.getLeaderId() >> ""

        when:
        def result = TeamLeaderOutput.fromTeamExcerptProjection(teamExcerptProjection)

        then:
        result == null

    }

    def "fromTeamExcerptProjection() WHEN called with not empty leaderId SHOULD map it correctly to TeamLeaderOutput object"(){
        given:
        teamExcerptProjection.getLeaderId() >> id
        teamExcerptProjection.getLeaderMailAddress() >> mail
        teamExcerptProjection.getLeaderFirstName() >> firstName
        teamExcerptProjection.getLeaderLastName() >> lastName
        teamExcerptProjection.getLeaderB2B() >> isB2b
        teamExcerptProjection.getLeaderEc() >> isEc

        and: "valid expected value"
        def correctTeamLeaderOutput = new TeamLeaderOutput(firstName + " " + lastName,
                                                            id,
                                                            mail,
                                                            isEc,
                                                            isB2b)

        when:
        def result = TeamLeaderOutput.fromTeamExcerptProjection(teamExcerptProjection)

        then:
            result == correctTeamLeaderOutput


        where:
        id | mail          | firstName  | lastName  | isB2b | isEc
        1  | "mail@mail"   | "John"     | "Smith"   | true  | false
        2  | "mail@mail2"  | "Jake"     | "Johnson" | false | false
        3  | "mail@mail3"  | "Mary"     | "Brown"   | false | true
        4  | "mail@mail4"  | "Jennifer" | "Lopez"   | true  | true

    }
}
