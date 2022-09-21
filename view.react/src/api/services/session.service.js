import {USER_DATA_KEY} from "../../constants/session.keystorage";


export const logout = (instance) => {
    localStorage.removeItem(USER_DATA_KEY);
    instance.logoutRedirect().catch(e => {
        console.error(e);
    });
}

export const getCurrentUser = () => {
    const user = JSON.parse(localStorage.getItem(USER_DATA_KEY)) || {roles: []}
    return {
        ...user,
        isLeader: user?.roles.includes("ROLES_LEADER"),
        isAdmin: user?.roles.includes("ROLES_ADMIN")
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