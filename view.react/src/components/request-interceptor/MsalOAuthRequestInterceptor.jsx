import { useAccount,useMsal } from '@azure/msal-react';
import React from 'react';

import { axiosClient } from "../../api/client";
import {loginRequest} from "../../authConfig";

const MsalOAuthRequestInterceptor = ({ children }) => {
    const { instance, accounts } = useMsal();
    const account = useAccount(accounts[0]);

    axiosClient.interceptors.request.use(async (config) => {
        if (!account) {
            return config;
        }
        const response = await instance.acquireTokenSilent({
            ...loginRequest,
            account,
        });

        config.headers.Authorization = `Bearer ${response.accessToken}`;

        return config;
    });

    return (
        <>
            {children}
        </>
    );
};

export default MsalOAuthRequestInterceptor;