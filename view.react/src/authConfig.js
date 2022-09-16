export const msalConfig = {
    auth: {
        clientId: "136f665b-8ad9-4662-92b6-3aad6f959e0f",
        authority: 'https://login.microsoftonline.com/09a1625a-533f-428a-b137-b8d1f46d688a',
        redirectUri: `${window.location.origin}/login_check`,
    },
    cache: {
        cacheLocation: "sessionStorage", // This configures where your cache will be stored
        storeAuthStateInCookie: false, // Set this to "true" if you are having issues on IE11 or Edge
    }
};

// Add scopes here for ID token to be used at Microsoft identity platform endpoints.
export const loginRequest = {
    scopes: ["136f665b-8ad9-4662-92b6-3aad6f959e0f/.default"]
};
