package info.fingo.urlopia.api.v2.user

import info.fingo.urlopia.team.TeamExcerptProjection
import info.fingo.urlopia.user.UserExcerptProjection
import spock.lang.Specification

class UserOutputSpec extends Specification{
    private UserExcerptProjection userExcerptProjection
    private TeamExcerptProjection teamExcerptProjection
    private TeamExcerptProjection secondTeamExcerptProjection

    void setup(){
        userExcerptProjection = Mock(UserExcerptProjection)
        teamExcerptProjection = Mock(TeamExcerptProjection)
        secondTeamExcerptProjection = Mock(TeamExcerptProjection)
    }

    def "fromUserExcerptProjection() WHEN called with userProjection SHOULD map it correctly to UserOutput object"(){
        given: "List of team names  to which the user belongs"
        def team1Name = "team1"
        def team2Name = "team2"
        def listName = List.of(team1Name,team2Name)

        teamExcerptProjection.getName() >> team1Name
        secondTeamExcerptProjection.getName() >> team2Name

        and: "set of teams to which the user belongs"
        Set<TeamExcerptProjection> teams = new HashSet<TeamExcerptProjection>()
        teams.add(teamExcerptProjection)
        teams.add(secondTeamExcerptProjection)

        userExcerptProjection.getName() >> fullName
        userExcerptProjection.getId() >> id
        userExcerptProjection.getMail() >> mail
        userExcerptProjection.getTeams() >> teams
        userExcerptProjection.getWorkTime() >> workTime

        and: "valid expected value"
        def correctUserOutput = new UserOutput(fullName,
                                                id,
                                                mail,
                                                List.of(team1Name,team2Name),
                                                workTime)

        when:
        def result = UserOutput.fromUserExcerptProjection(userExcerptProjection)

        then:
        result.fullName() == correctUserOutput.fullName()
        result.userId() == correctUserOutput.userId()
        result.mailAddress() == correctUserOutput.mailAddress()
        result.workingHours() == correctUserOutput.workingHours()
        correctUserOutput.teams().containsAll(listName)

        where:
        fullName         | id | mail          | workTime
        "John Smith"     | 1  | "mail@mail"   | 7.0
        "Jake Johnson"   | 2  | "mail@mail2"  | 9.0
        "Mary Brown"     | 3  | "mail@mail3"  | 3.0
        "Jennifer Lopez" | 4  | "mail@mail4"  | 5.0
    }
}
