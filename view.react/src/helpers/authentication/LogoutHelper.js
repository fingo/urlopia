import { PublicClientApplication } from "@azure/msal-browser";

import { msalConfig } from "../../authConfig";
import { USER_DATA_KEY } from "../../constants/session.keystorage";

export const logout = () => {
    oauthLogout();
}

const oauthLogout = () => {
    const msalInstance = new PublicClientApplication(msalConfig);
    localStorage.removeItem(USER_DATA_KEY);
    msalInstance.logoutRedirect().catch(e => {
            console.error(e);
    });
}