package info.fingo.urlopia.api.v2.team;


import info.fingo.urlopia.team.TeamExcerptProjection;

public record TeamOutput(String teamName,
                         TeamLeaderOutput teamMembersOutput){

    public static TeamOutput fromTeamExcerptProjection(TeamExcerptProjection teamExcerptProjection){
        var teamLeaderOutput = TeamLeaderOutput.fromTeamExcerptProjection(teamExcerptProjection);
        var teamName = teamExcerptProjection.getName();
        return new TeamOutput(teamName,teamLeaderOutput);
    }
}
