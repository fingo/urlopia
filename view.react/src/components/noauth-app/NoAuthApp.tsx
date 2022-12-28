import {QueryClientProvider} from "@tanstack/react-query";
import ReactNotification from 'react-notifications-component';
import {HashRouter as Router} from "react-router-dom";

import queryClient from "../../api/queryHooks/queryClient";
import {AppInfoProvider} from "../../contexts/app-info-context/appInfoContext";
import {Main} from "../main/Main";
import NoAuthRequestInterceptor from "../noauth-request-interceptor/NoAuthRequestInterceptor";


export const NoAuthApp = () => {

    return (
        <QueryClientProvider client={queryClient}>
            <Router>
                <ReactNotification isMobile={true}/>
                <AppInfoProvider>
                        <NoAuthRequestInterceptor>
                            <Main/>
                        </NoAuthRequestInterceptor>
                </AppInfoProvider>
            </Router>
        </QueryClientProvider>
    )
}