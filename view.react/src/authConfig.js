export const msalConfig = {
    auth: {
        clientId: process.env.REACT_APP_OAUTH_CLIENT_ID || '',
        authority: `https://login.microsoftonline.com/${process.env.REACT_APP_OAUTH_TENANT_ID}`,
        redirectUri: `${window.location.origin}/login_check`,
    },
    cache: {
        cacheLocation: "sessionStorage", // This configures where your cache will be stored
        storeAuthStateInCookie: false, // Set this to "true" if you are having issues on IE11 or Edge
    }
};

// Add scopes here for ID token to be used at Microsoft identity platform endpoints.
export const loginRequest = {
    scopes: [`${process.env.REACT_APP_OAUTH_CLIENT_ID}/.default`]
};
