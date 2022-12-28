import React from "react";

import {NoAuthApp} from "../noauth-app/NoAuthApp";
import {OAuthApp} from "../ouath-app/OAuthApp";


export const App = () => {
    const authMode = process.env.REACT_APP_AUTH_MODE || '';

    return (
        <>
            {
                authMode === 'NO-AUTH' ?
                    <NoAuthApp /> :
                    <OAuthApp />
            }
        </>
    )
}