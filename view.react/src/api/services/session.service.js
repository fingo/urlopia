import {USER_DATA_KEY} from "../../constants/session.keystorage";

export const getCurrentUser = () => {
    const user = JSON.parse(localStorage.getItem(USER_DATA_KEY)) || {roles: []}
    if (!user.roles){
        user.roles = [];
    }
    return {
        ...user,
        isLeader: user.roles.includes("ROLES_LEADER"),
        isAdmin: user.roles.includes("ROLES_ADMIN")
    };
}

export const getFullUserName = () => {
    const user = getCurrentUser();
    return !!user ? `${user.name} ${user.surname}` : '';
}

export const getUserTeams = () => {
    const user = getCurrentUser();
    return user.teams ?? [];
}