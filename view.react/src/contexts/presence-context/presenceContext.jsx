import {createContext, useContext, useReducer} from 'react';

import {presenceReducer} from "./presenceReducer";

const PresenceContext = createContext();

const initialState = {
    myConfirmations: {
        fetching: false,
        error: null,
        confirmations: {}
    },
    usersConfirmations: {
        fetching: false,
        error: null,
        confirmations: {}
    },
    contextError: null
}

export const PresenceProvider = ({children}) => {
    const [state, dispatch] = useReducer(presenceReducer, initialState);
    const value = [state, dispatch];
    return <PresenceContext.Provider value={value}>{children}</PresenceContext.Provider>;
}

export const usePresence = () => {
    const context = useContext(PresenceContext);
    if (context === undefined) {
        throw new Error("usePresence() must be used within a PresenceProvider")
    }
    return context;
}
