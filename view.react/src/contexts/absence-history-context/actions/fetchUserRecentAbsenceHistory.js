import {sendGetRequest} from "../../../helpers/RequestHelper";
import {
    FETCH_USER_RECENT_ABSENCE_HISTORY_ACTION_PREFIX,
    FETCH_USER_RECENT_ABSENCE_HISTORY_URL,
    FETCH_USER_RECENT_ABSENCE_HISTORY_URL_POSTFIX
} from "../constants";

export const fetchUserRecentAbsenceHistory = (dispatch, userId) => {
    dispatch({type: `${FETCH_USER_RECENT_ABSENCE_HISTORY_ACTION_PREFIX}_absence-history`})
    sendGetRequest(`${FETCH_USER_RECENT_ABSENCE_HISTORY_URL}/${userId}${FETCH_USER_RECENT_ABSENCE_HISTORY_URL_POSTFIX}`)
        .then(data =>
            dispatch({
                type: `${FETCH_USER_RECENT_ABSENCE_HISTORY_ACTION_PREFIX}_success`,
                response: data
            })
        )
        .catch(errorMessage => dispatch({
            type: `${FETCH_USER_RECENT_ABSENCE_HISTORY_ACTION_PREFIX}_failure`,
            errorMessage: errorMessage
        }))
}

export const fetchUserRecentAbsenceHistoryReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_USER_RECENT_ABSENCE_HISTORY_ACTION_PREFIX}_absence-history`: {
            return {
                ...state,
                fetching: true,
                error: null
            }
        }
        case `${FETCH_USER_RECENT_ABSENCE_HISTORY_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                recentUserHistory: action.response
            }
        }
        case `${FETCH_USER_RECENT_ABSENCE_HISTORY_ACTION_PREFIX}_failure`: {
            return {
                ...state,
                fetching: false,
                error: action.error
            }
        }
        default: {
            throw new Error(`Unhandled exception type ${action.type}`)
        }
    }
}