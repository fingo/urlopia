import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_ABSENCE_HISTORY_ACTION_PREFIX, FETCH_ABSENCE_HISTORY_URL} from "../constants";

export const fetchAbsenceHistory = (
    dispatch,
    year,
    pageNumber,
    sortField = "created",
    sortOrder = "desc"
) => {
    const paging = `sort=${sortField},${sortOrder}&page=${pageNumber}`

    dispatch({type: `${FETCH_ABSENCE_HISTORY_ACTION_PREFIX}_absence-history`})
    sendGetRequest(`${FETCH_ABSENCE_HISTORY_URL}?year=${year}&${paging}`)
        .then(data => dispatch({
                type: `${FETCH_ABSENCE_HISTORY_ACTION_PREFIX}_success`,
                response: data
            })
        ).catch(errorMessage => dispatch({
            type: `${FETCH_ABSENCE_HISTORY_ACTION_PREFIX}_failure`,
            errorMessage: errorMessage
        }))
}

export const fetchAbsenceHistoryReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_ABSENCE_HISTORY_ACTION_PREFIX}_absence-history`: {
            return {
                ...state,
                fetching: true,
                error: null
            }
        }
        case `${FETCH_ABSENCE_HISTORY_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                absenceHistory: action.response.content,
                absenceHistoryPage: action.response,
            }
        }
        case `${FETCH_ABSENCE_HISTORY_ACTION_PREFIX}_failure`: {
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