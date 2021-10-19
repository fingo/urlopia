package info.fingo.urlopia.api.v2.user;


import info.fingo.urlopia.team.TeamExcerptProjection;
import info.fingo.urlopia.user.UserExcerptProjection;

import java.util.List;
import java.util.Set;

public record UserOutput(String fullName,
                         Long userId,
                         String mailAddress,
                         List<String> teams,
                         Float workingHours,
                         WorkTimeOutput workTime) {

   public static UserOutput fromUserExcerptProjection(UserExcerptProjection userProjection){
        return new UserOutput(userProjection.getName(),
                              userProjection.getId(),
                              userProjection.getMail(),
                              mapProjectionTeams(userProjection.getTeams()),
                              userProjection.getWorkTime(),
                              WorkTimeOutput.fromWorkTime(userProjection.getWorkTime()));
    }

    private static List<String> mapProjectionTeams(Set<TeamExcerptProjection> teamsProjections){
        return teamsProjections.stream()
                .map(TeamExcerptProjection::getName)
                .toList();
    }
}
