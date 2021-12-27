import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_ACCEPTANCES_HISTORY_ACTION_PREFIX, FETCH_ACCEPTANCES_HISTORY_URL} from "../constants";

export const fetchAcceptancesHistory = (
    dispatch,
    year,
    pageNumber,
    sortField = "request.startDate",
    sortOrder = "desc"
) => {
    const yearStart = `${year}-01-01`
    const yearEnd = `${year}-12-31`

    const filter = `?filter=request.endDate>:${yearStart}&filter=request.startDate<:${yearEnd}`
    const pagination = `page=${pageNumber}&sort=${sortField},${sortOrder}`

    dispatch({type: `${FETCH_ACCEPTANCES_HISTORY_ACTION_PREFIX}_request`})
    sendGetRequest(`${FETCH_ACCEPTANCES_HISTORY_URL}${filter}&${pagination}`)
        .then(data => dispatch({
                type: `${FETCH_ACCEPTANCES_HISTORY_ACTION_PREFIX}_success`,
                response: data
            })
        )
        .catch(errorMsg => dispatch({
            type: `${FETCH_ACCEPTANCES_HISTORY_ACTION_PREFIX}_failure`,
            error: errorMsg
        }))
}

export const fetchAcceptancesHistoryReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_ACCEPTANCES_HISTORY_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }
        case `${FETCH_ACCEPTANCES_HISTORY_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                history: action.response.content,
                historyPage: action.response,
            }
        }
        case `${FETCH_ACCEPTANCES_HISTORY_ACTION_PREFIX}_failure`: {
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