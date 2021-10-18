export const sortedTeams = (teams) => {
    const teamsCompare = (teamOne, teamTwo) => {
        return teamOne.label.localeCompare(teamTwo.label,"pl",{sensitivity: "base"})
    }

    return teams.sort(teamsCompare);
}