import {createContext, useContext, useReducer} from 'react';

import {vacationDaysReducer} from "./vacationDaysReducer";

const VacationDaysContext = createContext();

const initialState = {
    fetching: false,
    error: null,
    vacationDays: {},
    pendingDays: {}
}

export const VacationDaysProvider = ({children}) => {
    const [state, dispatch] = useReducer(vacationDaysReducer, initialState);
    const value = [state, dispatch];
    return <VacationDaysContext.Provider value={value}>{children}</VacationDaysContext.Provider>;
}

export const useVacationDays = () => {
    const context = useContext(VacationDaysContext);
    if (context === undefined) {
        throw new Error("useVacationDays() must be used within a VacationDaysProvider")
    }
    return context;
}