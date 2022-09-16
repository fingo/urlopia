import { useAccount,useMsal } from '@azure/msal-react';
import axios from 'axios';
import React from 'react';

import {loginRequest} from "../../authConfig";



const RequestInterceptor = ({ children }) => {
    const { instance, accounts } = useMsal();
    const account = useAccount(accounts[0]);

    axios.interceptors.request.use(async (config) => {
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

export default RequestInterceptor;