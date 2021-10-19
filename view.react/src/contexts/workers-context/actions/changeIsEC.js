import {CHANGE_IS_EC_ACTION_PREFIX} from "../constants";

export const changeIsEC = (isEC) => {
    return {
        type: `${CHANGE_IS_EC_ACTION_PREFIX}_change`,
        payload: {
            isEC,
        },
    }
}

export const changeIsECReducer = (state, action) => {
    switch (action.type) {
        case `${CHANGE_IS_EC_ACTION_PREFIX}_change`: {
            const {isEC} = action.payload;
            return {
                ...state,
                isEC,
            }
        }
        default: {
            throw new Error(`Unhandled exception type ${action.type}`)
        }
    }
}