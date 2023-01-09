import React from "react";

import {isNoAuthMode} from "../../helpers/authentication/LogoutHelper";
import {NoAuthApp} from "../noauth-app/NoAuthApp";
import {OAuthApp} from "../ouath-app/OAuthApp";


export const App = () => {

    const isNoAuth = isNoAuthMode();

    return (
        <>
            {
                isNoAuth ? <NoAuthApp /> : <OAuthApp />
            }
        </>
    )
}