import {createContext, useContext, useReducer} from 'react';

import {workersReducer} from "./workersReducer";

const WorkersContext = createContext();

const initialState = {
    isEC: true,
    fetching: false,
    error: null,
    unspecifiedUsers: [],
    workers: {
        fetching: false,
        error: null,
        workers: [],
        remainingDaysOfCurrentSelectedWorker: {
            remainingDays: 0,
            remainingHours: 0,
        },
        selectedWorker: {},
    },
    associates: {
        fetching: false,
        error: null,
        associates: [],
        remainingDaysOfCurrentSelectedAssociate: {
            remainingDays: 0,
            remainingHours: 0,
        },
        selectedAssociate: {},
    }
}

export const WorkersProvider = ({children}) => {
    const [state, dispatch] = useReducer(workersReducer, initialState);
    const value = [state, dispatch];
    return <WorkersContext.Provider value={value}>{children}</WorkersContext.Provider>;
}

export const useWorkers = () => {
    const context = useContext(WorkersContext);
    if (context === undefined) {
        throw new Error("useWorkers() must be used within a WorkersProvider")
    }
    return context;
}
