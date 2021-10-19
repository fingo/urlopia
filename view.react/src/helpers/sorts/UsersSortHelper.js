export const sortedUsers = (users, fieldToSortBy) => {
    const usersCompare = (userOne, userTwo) => {
        const [userOneFirstName, userOneLastName] = userOne[fieldToSortBy].split(" ")
        const [userTwoFirstName, userTwoLastName] = userTwo[fieldToSortBy].split(" ")
        const compareByLastName = userOneLastName.localeCompare(userTwoLastName,"pl", {sensitivity:"base"})
        const compareByFirstName = userOneFirstName.localeCompare(userTwoFirstName,"pl",{sensitivity:"base"})
        return compareByLastName? compareByLastName : compareByFirstName
    }

    return users.sort(usersCompare);
}