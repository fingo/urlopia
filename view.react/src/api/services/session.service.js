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
    return JSON.parse(sessionStorage.getItem(USER_DATA_KEY));
}
