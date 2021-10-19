import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_USER_RECENT_ABSENCE_HISTORY_ACTION_PREFIX, FETCH_USER_RECENT_ABSENCE_HISTORY_URL} from "../constants";

export const fetchUserAbsenceHistory = (dispatch, userId, recent = true) => {
    const PAGING = recent ? "page=0&size=5&sort=id,desc" : "sort=id,desc"
    dispatch({type: `${FETCH_USER_RECENT_ABSENCE_HISTORY_ACTION_PREFIX}_absence-history`})
    sendGetRequest(`${FETCH_USER_RECENT_ABSENCE_HISTORY_URL}/${userId}?${PAGING}`)
        .then(data =>
            dispatch({
                type: `${FETCH_USER_RECENT_ABSENCE_HISTORY_ACTION_PREFIX}_success`,
                response: data,
                payload: {
                    recent
                }
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
            const {recent} = action.payload
            return {
                ...state,
                fetching: false,
                recentUserHistory: recent ? action.response.content : [...state.absenceHistory],
                absenceHistory: recent ? [...state.absenceHistory] : action.response.content
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