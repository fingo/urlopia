import {USER_DATA_KEY} from "../../constants/session.keystorage";
import {sendPostRequest} from "../../helpers/RequestHelper";

const URL = "/api/v2/session";

export const login = (body) => {
    return sendPostRequest(URL, body)
        .then(data => {
            if (data.token) {
                sessionStorage.setItem(USER_DATA_KEY, JSON.stringify(data));
            }
            return data;
        })
}

export const logout = () => {
    sessionStorage.removeItem(USER_DATA_KEY);
}

export const getCurrentUser = () => {
    const user = JSON.parse(sessionStorage.getItem(USER_DATA_KEY)) || {userRoles: []}
    return {
        ...user,
        isLeader: user.userRoles.includes("ROLES_LEADER"),
        isAdmin: user.userRoles.includes("ROLES_ADMIN")
    }
}

export const getFullUserName = () => {
    const user = getCurrentUser();
    return `${user.name} ${user.surname}`;
}

export const getUserTeams = () => {
    return getCurrentUser().teams;
}