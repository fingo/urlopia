import React from 'react';

import { axiosClient } from "../../api/client";
import {NO_AUTH_USER_KEY} from "../../constants/session.keystorage";

interface IProps {
    children: JSX.Element
}

const NoAuthRequestInterceptor = ({ children }: IProps) => {
    axiosClient.interceptors.request.use(async (config) => {
        config.headers.userId = localStorage.getItem(NO_AUTH_USER_KEY) || 1;
        return config;
    });

    return (
        <>
            {children}
        </>
    );
};

export default NoAuthRequestInterceptor;