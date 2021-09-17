import {sendPutRequest} from "../../../helpers/RequestHelper";
import {
    CHANGE_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX,
    CHANGE_USER_WORKING_HOURS_PREFERENCES_URL
} from "../constants";

export const changeWorkingHoursPreferences = (dispatch, newPreferences) => {
    dispatch({type: `${CHANGE_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_request`})
    sendPutRequest(`${CHANGE_USER_WORKING_HOURS_PREFERENCES_URL}`, {
        dayPreferences: newPreferences
    })
        .then(data => dispatch({
            type: `${CHANGE_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_success`,
            response: data
        }))
        .catch(errorMsg => dispatch({
            type: `${CHANGE_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_failure`,
            error: errorMsg
        }))
}

export const changeWorkingHoursPreferencesReducer = (state, action) => {
    switch (action.type) {
        case `${CHANGE_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_request`: {
            return {
                ...state
            }
        }
        case `${CHANGE_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_success`: {
            const addedPreference = action.response
            const {userId} = addedPreference

            return {
                ...state,
                preferences: {
                    ...state.preferences,
                    [userId]: addedPreference
                }
            }
        }
        case `${CHANGE_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_failure`: {
            return {
                ...state,
                error: action.error,
            }
        }
        default: {
            throw new Error(`Unhandled exception type ${action.type}`)
        }
    }
}