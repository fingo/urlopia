import { PublicClientApplication } from "@azure/msal-browser";

import { msalConfig } from "../../authConfig";
import { NO_AUTH_USER_KEY, USER_DATA_KEY } from "../../constants/session.keystorage";

export const logout = () => {
    isNoAuthMode() ? noAuthLogout() : oauthLogout();
}

export const isNoAuthMode = () =>{
    const currentAuthMode = import.meta.env.VITE_AUTH_MODE || '';
    return 'NO-AUTH' === currentAuthMode;
}

const oauthLogout = () => {
    const msalInstance = new PublicClientApplication(msalConfig);
    localStorage.removeItem(USER_DATA_KEY);
    msalInstance.logoutRedirect().catch(e => {
            console.error(e);
    });
}

const noAuthLogout = () => {
    localStorage.removeItem(NO_AUTH_USER_KEY)
    localStorage.removeItem(USER_DATA_KEY)
    window.location.href = window.location.origin
}
