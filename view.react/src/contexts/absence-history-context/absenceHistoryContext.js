import {createContext, useContext, useReducer} from 'react';

import {absenceHistoryReducer} from "./absenceHistoryReducer";

const AbsenceHistoryContext = createContext();

const initialState = {
    myAbsenceHistory: {
        fetching: false,
        error: null,
        absenceHistory: []
    },
    userAbsenceHistory: {
        fetching: false,
        error: null,
        absenceHistory: []
    },
    contextError: null
}

export const AbsenceHistoryProvider = ({children}) => {
    const [state, dispatch] = useReducer(absenceHistoryReducer, initialState);
    const value = [state, dispatch];
    return <AbsenceHistoryContext.Provider value={value}>
                {children}
            </AbsenceHistoryContext.Provider>
}

export const useAbsenceHistory = () => {
    const context = useContext(AbsenceHistoryContext);
    if (context === undefined) {
        throw new Error("useAbsenceHistory() must be used within an AbsenceHistoryProvider");
    }
    return context;
}
