import React from 'react';

import { axiosClient } from "../../api/client";

interface IProps {
    children: JSX.Element
}

const NoAuthRequestInterceptor = ({ children }: IProps) => {
    axiosClient.interceptors.request.use(async (config) => {
        config.headers.userId = 1;
        return config;
    });

    return (
        <>
            {children}
        </>
    );
};

export default NoAuthRequestInterceptor;