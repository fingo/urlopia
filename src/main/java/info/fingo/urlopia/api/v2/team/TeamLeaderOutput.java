package info.fingo.urlopia.api.v2.team;

import info.fingo.urlopia.team.TeamExcerptProjection;

public record TeamLeaderOutput(String fullName,
                               Long userId,
                               String mailAddress,
                               boolean isEmployee,
                               boolean isAssociates) {



    public static TeamLeaderOutput fromTeamExcerptProjection(TeamExcerptProjection teamExcerptProjection){
        var leaderId = teamExcerptProjection.getLeaderId();
        if (leaderId.isEmpty()){
            return null;
        }
        var stringBuilder = new StringBuilder();
        stringBuilder.append(teamExcerptProjection.getLeaderFirstName());
        stringBuilder.append(" ");
        stringBuilder.append(teamExcerptProjection.getLeaderLastName());
        var leaderFullName = stringBuilder.toString();
        var leaderMailAddress = teamExcerptProjection.getLeaderMailAddress();
        var leaderEc = teamExcerptProjection.getLeaderEc();
        var leaderB2B = teamExcerptProjection.getLeaderB2B();
        return new TeamLeaderOutput(leaderFullName ,
                                    Long.valueOf(leaderId),
                                    leaderMailAddress,
                                    leaderEc,
                                    leaderB2B);
    }

}



