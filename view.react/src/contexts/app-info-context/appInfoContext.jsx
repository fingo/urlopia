import {createContext, useContext, useReducer} from 'react';

import {appInfoReducer} from "./appInfoReducer";

const AppInfoContext = createContext();

const initialState = {
    fetching: false,
    error: null,
    appInfo: {}
}

export const AppInfoProvider = ({children}) => {
    const [state, dispatch] = useReducer(appInfoReducer, initialState);
    const value = [state, dispatch];
    return <AppInfoContext.Provider value={value}>{children}</AppInfoContext.Provider>;
}

export const useAppInfo = () => {
    const context = useContext(AppInfoContext);
    if (context === undefined) {
        throw new Error("useAppInfo() must be used within a AppInfoProvider")
    }
    return context;
}
