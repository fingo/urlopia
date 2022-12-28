import {InteractionType, PublicClientApplication} from "@azure/msal-browser";
import {MsalAuthenticationTemplate, MsalProvider} from "@azure/msal-react";
import {QueryClientProvider} from "@tanstack/react-query";
import ReactNotification from 'react-notifications-component';
import {HashRouter as Router} from "react-router-dom";

import queryClient from "../../api/queryHooks/queryClient";
import {loginRequest, msalConfig} from "../../authConfig";
import {AppInfoProvider} from "../../contexts/app-info-context/appInfoContext";
import {Main} from "../main/Main";
import MsalOAuthRequestInterceptor from "../request-interceptor/MsalOAuthRequestInterceptor";


export const OAuthApp = () => {

    const msalInstance = new PublicClientApplication(msalConfig);

    const signInRequest = {
        prompt: 'select_account',
        ...loginRequest
    }


    return (

        <MsalProvider instance={msalInstance}>
            <QueryClientProvider client={queryClient}>
                <Router>
                    <ReactNotification isMobile={true}/>
                    <AppInfoProvider>
                        <MsalAuthenticationTemplate interactionType={InteractionType.Redirect} authenticationRequest={signInRequest}>
                            <MsalOAuthRequestInterceptor>
                                <Main/>
                            </MsalOAuthRequestInterceptor>
                        </MsalAuthenticationTemplate>
                    </AppInfoProvider>
                </Router>
            </QueryClientProvider>
        </MsalProvider>

    )

}