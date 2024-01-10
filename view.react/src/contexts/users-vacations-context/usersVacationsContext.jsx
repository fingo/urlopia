import {createContext, useContext, useReducer} from 'react';

import {usersVacationsReducer} from "./usersVacationsReducer";

const UsersVacationsContext = createContext();

const initialState = {
    fetching: false,
    error: null,
    usersVacations: {}
}

export const UsersVacationsProvider = ({children}) => {
    const [state, dispatch] = useReducer(usersVacationsReducer, initialState);
    const value = [state, dispatch];
    return <UsersVacationsContext.Provider value={value}>{children}</UsersVacationsContext.Provider>;
}

export const useUsersVacations = () => {
    const context = useContext(UsersVacationsContext);
    if (context === undefined) {
        throw new Error("useUsersVacations() must be used within a UsersVacationsProvider")
    }
    return context;
}
