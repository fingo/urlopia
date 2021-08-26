import {sendGetRequest} from "../../../helpers/RequestHelper";
import {
    FETCH_USER_ABSENCE_HISTORY_PREFIX,
    FETCH_USER_ABSENCE_HISTORY_URL
} from "../constants";

export const fetchUserAbsenceHistory = (dispatch, userId, year) => {
    dispatch({type: `${FETCH_USER_ABSENCE_HISTORY_PREFIX}_absence-history`})
    sendGetRequest(`${FETCH_USER_ABSENCE_HISTORY_URL}/${userId}/?year=${year}`)
        .then(data => {
            dispatch({
                type: `${FETCH_USER_ABSENCE_HISTORY_PREFIX}_success`,
                response: data
            })
        })
        .catch(errorMessage => dispatch({
            type: `${FETCH_USER_ABSENCE_HISTORY_PREFIX}_failure`,
            errorMessage: errorMessage
        }))
}

export const fetchUserAbsenceHistoryReducer = (state, action) => {
    switch(action.type) {
        case `${FETCH_USER_ABSENCE_HISTORY_PREFIX}_absence-history`: {
            return {
                ...state,
                fetching: true,
                error: null
            }
        }
        case `${FETCH_USER_ABSENCE_HISTORY_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                absenceHistory: action.response
            }
        }
        case `${FETCH_USER_ABSENCE_HISTORY_PREFIX}_failure`: {
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