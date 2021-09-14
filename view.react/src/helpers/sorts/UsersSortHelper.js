export const sortedUsers = (users) => {
    const usersCompare = (userOne, userTwo) => {
        const [userOneFirstName, userOneLastName] = userOne.label.split(" ")
        const [userTwoFirstName, userTwoLastName] = userTwo.label.split(" ")
        const compareByLastName = userOneLastName.localeCompare(userTwoLastName,"pl", {sensitivity:"base"})
        const compareByFirstName = userOneFirstName.localeCompare(userTwoFirstName,"pl",{sensitivity:"base"})
        return compareByLastName? compareByLastName : compareByFirstName
    }

    return users.sort(usersCompare);
}