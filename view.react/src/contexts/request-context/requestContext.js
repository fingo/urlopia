import {createContext, useContext, useReducer} from 'react';

import {requestReducer} from "./requestReducer";

const RequestContext = createContext();

const initialState = {
    userRequests: {
        fetching: false,
        error: null,
        requests: []
    },
    teamRequests: {
        fetching: false,
        error: null,
        requests: []
    },
    companyRequests: {
        fetching: false,
        error: null,
        requests: []
    }
}

export const RequestProvider = ({children}) => {
    const [state, dispatch] = useReducer(requestReducer, initialState);
    const value = [state, dispatch];
    return <RequestContext.Provider value={value}>{children}</RequestContext.Provider>;
}

export const useRequests = () => {
    const context = useContext(RequestContext);
    if (context === undefined) {
        throw new Error("useRequests() must be used within a RequestProvider")
    }
    return context;
}
