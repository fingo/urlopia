import {createContext, useContext, useReducer} from 'react';

import {holidaysReducer} from "./holidaysReducer";

const HolidaysContext = createContext();

const initialState = {
    fetching: false,
    error: null,
    holidays: [],
}

export const HolidaysProvider = ({children}) => {
    const [state, dispatch] = useReducer(holidaysReducer, initialState);
    const value = [state, dispatch];
    return <HolidaysContext.Provider value={value}>{children}</HolidaysContext.Provider>;
}

export const useHolidays = () => {
    const context = useContext(HolidaysContext);
    if (context === undefined) {
        throw new Error("useHolidays() must be used within a HolidaysProvider")
    }
    return context;
}
