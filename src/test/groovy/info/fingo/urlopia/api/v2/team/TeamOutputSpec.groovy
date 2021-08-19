package info.fingo.urlopia.api.v2.team

import info.fingo.urlopia.team.TeamExcerptProjection
import spock.lang.Specification

class TeamOutputSpec extends Specification{
    private TeamExcerptProjection teamExcerptProjection

    void setup(){
        teamExcerptProjection = Mock(TeamExcerptProjection)
    }

    def "fromTeamExcerptProjection WHEN called with teamProjection SHOULD map it correctly to TeamOutput object"(){
        given:
        teamExcerptProjection.getLeaderId() >> ""
        teamExcerptProjection.getName() >> teamName

        and: "valid expected value"
        def correctTeamOutput = new TeamOutput(teamName,null)

        when:
        def result = TeamOutput.fromTeamExcerptProjection(teamExcerptProjection)

        then:
        result == correctTeamOutput

        where:
        _ | teamName
        _ | "team1"
        _ | "team2"
        _ | "team3"
        _ | ""
    }
}
