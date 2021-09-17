import {createContext, useContext, useReducer} from 'react';

import {userPreferenceReducer} from "./userPreferencesReducer";

const UserPreferencesContext = createContext();

const initialState = {
    workingHours: {
        fetching: false,
        error: null,
        preferences: {}
    }
}

export const UserPreferencesProvider = ({children}) => {
    const [state, dispatch] = useReducer(userPreferenceReducer, initialState);
    const value = [state, dispatch];
    return <UserPreferencesContext.Provider value={value}>{children}</UserPreferencesContext.Provider>;
}

export const useUserPreferences = () => {
    const context = useContext(UserPreferencesContext);
    if (context === undefined) {
        throw new Error("useUserPreferences() must be used within a UserPreferencesProvider")
    }
    return context;
}
