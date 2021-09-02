import {createContext, useContext, useReducer} from 'react';

import {workersReducer} from "./workersReducer";

const WorkersContext = createContext();

const initialState = {
    fetching: false,
    error: null,
    workers: [],
    remainingDaysOfCurrentSelectedUser: {
        remainingDays: 0,
        remainingHours: 0,
    },
    selectedUser: {},
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
