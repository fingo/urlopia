import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_USER_RECENT_ABSENCE_HISTORY_ACTION_PREFIX, FETCH_USER_RECENT_ABSENCE_HISTORY_URL} from "../constants";

export const fetchRecentUserAbsenceHistory = (dispatch, userId) => {
    const parameters = "page=0&size=5&sort=id,desc"
    return fetchUserAbsenceHistory(dispatch, userId, parameters, {recent: true})
}

export const fetchPagedUserAbsenceHistory = (
    dispatch,
    userId,
    year,
    pageNumber,
    sortField = "created",
    sortOrder = "desc"
) => {
    const parameters = `year=${year}&page=${pageNumber}&sort=${sortField},${sortOrder}`
    return fetchUserAbsenceHistory(dispatch, userId, parameters)
}

const fetchUserAbsenceHistory = (dispatch, userId, parameters, additionalPayload = {}) => {
    dispatch({type: `${FETCH_USER_RECENT_ABSENCE_HISTORY_ACTION_PREFIX}_absence-history`})
    sendGetRequest(`${FETCH_USER_RECENT_ABSENCE_HISTORY_URL}/${userId}?${parameters}`)
        .then(data =>
            dispatch({
                type: `${FETCH_USER_RECENT_ABSENCE_HISTORY_ACTION_PREFIX}_success`,
                response: data,
                payload: {
                    ...additionalPayload
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
            const response = action.response

            return {
                ...state,
                fetching: false,
                recentUserHistory: recent ? response.content : [...state.absenceHistory],
                absenceHistory: recent ? [...state.absenceHistory] : response.content,
                absenceHistoryPage: response
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