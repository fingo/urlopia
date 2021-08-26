import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_MY_ABSENCE_HISTORY_ACTION_PREFIX, FETCH_MY_ABSENCE_HISTORY_URL} from "../constants";

export const fetchMyAbsenceHistory = (dispatch, year) => {
    dispatch({type: `${FETCH_MY_ABSENCE_HISTORY_ACTION_PREFIX}_absence-history`})
    sendGetRequest(`${FETCH_MY_ABSENCE_HISTORY_URL}${year}`)
        .then(data => {
            dispatch({
                type: `${FETCH_MY_ABSENCE_HISTORY_ACTION_PREFIX}_success`,
                response: data
            })
        })
        .catch(errorMessage => dispatch({
            type: `${FETCH_MY_ABSENCE_HISTORY_ACTION_PREFIX}_failure`,
            error: errorMessage
        }))
}


export const fetchMyAbsenceHistoryReducer = (state, action) => {
    switch(action.type) {
        case `${FETCH_MY_ABSENCE_HISTORY_ACTION_PREFIX}_absence-history`: {
            return {
                ...state,
                fetching: true,
                error: null
            }
        }
        case `${FETCH_MY_ABSENCE_HISTORY_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                absenceHistory: action.response
            }
        }
        case `${FETCH_MY_ABSENCE_HISTORY_ACTION_PREFIX}_failure`: {
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