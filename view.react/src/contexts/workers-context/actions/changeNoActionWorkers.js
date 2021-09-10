import {CHANGE_NO_ACTION_WORKERS_ACTION_PREFIX} from "../constants";

export const changeNoActionWorkers = (userId, date) => {
    return {
        type: `${CHANGE_NO_ACTION_WORKERS_ACTION_PREFIX}_change`,
        payload: {
            userId,
            date,
        },
    }
}

export const changeNoActionWorkersReducer = (state, action) => {
    switch (action.type) {
        case `${CHANGE_NO_ACTION_WORKERS_ACTION_PREFIX}_change`: {
            const {userId, date} = action.payload;
            const userUnspecifiedAbsences = state.unspecifiedAbsences[userId];
            if (userUnspecifiedAbsences) {
                const changedUserUnspecifiedAbsences = userUnspecifiedAbsences.filter(absence => {
                    return absence !== date;
                });
                const newUnspecifiedAbsences = {...state.unspecifiedAbsences, [userId]: changedUserUnspecifiedAbsences}
                return {
                    ...state,
                    unspecifiedAbsences: newUnspecifiedAbsences,
                }
            }
            return {
                ...state,
            }
        }
        default: {
            throw new Error(`Unhandled exception type ${action.type}`)
        }
    }
}