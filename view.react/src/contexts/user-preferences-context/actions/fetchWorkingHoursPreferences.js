import {sendGetRequest} from "../../../helpers/RequestHelper";
import {
    FETCH_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX,
    FETCH_USER_WORKING_HOURS_PREFERENCES_URL
} from "../constants";

export const fetchWorkingHoursPreferences = dispatch => {
    dispatch({type: `${FETCH_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_request`})
    sendGetRequest(`${FETCH_USER_WORKING_HOURS_PREFERENCES_URL}`)
        .then(data => dispatch({
            type: `${FETCH_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_success`,
            response: data
        }))
        .catch(errorMsg => dispatch({
            type: `${FETCH_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_failure`,
            error: errorMsg
        }))
}

export const fetchWorkingHoursPreferencesReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }
        case `${FETCH_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                preferences: action.response
            }
        }
        case `${FETCH_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_failure`: {
            return {
                ...state,
                fetching: false,
                error: action.error,
            }
        }
        default: {
            throw new Error(`Unhandled exception type ${action.type}`)
        }
    }
}
