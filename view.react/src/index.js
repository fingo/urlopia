import {InteractionType, PublicClientApplication} from "@azure/msal-browser";
import {MsalAuthenticationTemplate, MsalProvider} from "@azure/msal-react";
import React from 'react';
import ReactDOM from 'react-dom';
import { ReactNotifications } from 'react-notifications-component';
import {BrowserRouter as Router} from "react-router-dom";

import {loginRequest, msalConfig} from "./authConfig";
import {App} from './components/app/App';
import RequestInterceptor from "./components/request-interceptor/RequestInterceptor";
import {AppInfoProvider} from "./contexts/app-info-context/appInfoContext";
import reportWebVitals from './reportWebVitals';

const msalInstance = new PublicClientApplication(msalConfig);

const signInRequest = {
    prompt: 'select_account',
    ...loginRequest
}

ReactDOM.render(
    <React.StrictMode>
        <MsalProvider instance={msalInstance}>
            <Router>
                <ReactNotifications isMobile={true}/>
                <AppInfoProvider>
                    <MsalAuthenticationTemplate interactionType={InteractionType.Redirect} authenticationRequest={signInRequest}>
                        <RequestInterceptor>
                            <App/>
                        </RequestInterceptor>
                    </MsalAuthenticationTemplate>
                </AppInfoProvider>
            </Router>
        </MsalProvider>
    </React.StrictMode>,
    document.getElementById('root'),
);

reportWebVitals();
