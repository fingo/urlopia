import {CHANGE_REMAINING_DAYS_ACTION_PREFIX} from "../constants";

export const changeRemainingDays = (days, hours) => {
    return {
        type: `${CHANGE_REMAINING_DAYS_ACTION_PREFIX}_change`,
        payload: {
            days,
            hours,
        },
    }
}

export const changeRemainingDaysReducer = (state, action) => {
    switch (action.type) {
        case `${CHANGE_REMAINING_DAYS_ACTION_PREFIX}_change`: {
            const {days, hours} = action.payload;
            return {
                ...state,
                remainingDaysOfCurrentSelectedUser: {
                    remainingDays: days,
                    remainingHours: hours,
                },
            }
        }
        default: {
            throw new Error(`Unhandled exception type ${action.type}`)
        }
    }
}