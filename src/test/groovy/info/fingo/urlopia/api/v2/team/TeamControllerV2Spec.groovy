package info.fingo.urlopia.api.v2.team

import info.fingo.urlopia.config.persistance.filter.Filter
import info.fingo.urlopia.team.TeamExcerptProjection
import info.fingo.urlopia.team.TeamService
import org.springframework.data.domain.Sort
import spock.lang.Specification

class TeamControllerV2Spec extends Specification{
    private TeamService teamService
    private TeamControllerV2 teamControllerV2
    private List<TeamExcerptProjection> teamsData
    private List<TeamOutput> teamOutputs

    void setup(){
        teamService = Mock(TeamService)
        teamControllerV2 = new TeamControllerV2(teamService)

        def firstTeamName = "team1"
        def firstLeaderId = ""
        def firstTeam = Mock(TeamExcerptProjection){
            getLeaderId() >> firstLeaderId
            getName() >> firstTeamName
        }

        def secondTeamName = "team2"
        def secondLeaderId = ""
        def secondTeam = Mock(TeamExcerptProjection){
            getLeaderId() >> secondLeaderId
            getName() >> secondTeamName
        }

        teamsData = List.of(firstTeam,secondTeam)
        teamOutputs = List.of(new TeamOutput(firstTeamName,null),
                                new TeamOutput(secondTeamName, null))


    }

    def "getAll() WHEN called with empty Filter SHOULD called service one time and map returned data"(){
        given:
        1 * teamService.getAll(_ as Filter, _ as Sort) >> teamsData


        when:
        def result = teamControllerV2.getAll(_ as String[],_ as Sort)

        then:
        result.containsAll(teamOutputs)
    }
}
