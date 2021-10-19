export const filterAbsentUsers = (absentUsers, selectedUsers, selectedTeams) => {
    return absentUsers.filter(user => {
        const isInSelectedUsers = selectedUsers.some(selectedUser => {
            return user.userName === selectedUser.value;
        });

        const isInSelectedTeams = selectedTeams.some(team => {
            return user.teams.includes(team.value) || (!user.teams.length && team.value === 'noTeams');
        });

        return isInSelectedUsers || isInSelectedTeams;
    });
}