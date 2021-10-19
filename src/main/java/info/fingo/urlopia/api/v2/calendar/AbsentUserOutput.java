package info.fingo.urlopia.api.v2.calendar;

import info.fingo.urlopia.team.Team;
import info.fingo.urlopia.user.User;

import java.util.List;

public record AbsentUserOutput(String userName, List<String> teams) {
    public static AbsentUserOutput of(User user) {
        var userTeamNames = user.getTeams().stream()
                .map(Team::getName)
                .toList();

        return new AbsentUserOutput(user.getFullName(), userTeamNames);
    }
}
