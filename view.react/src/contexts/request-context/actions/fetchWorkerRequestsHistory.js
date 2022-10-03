import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_WORKER_REQUESTS_HISTORY_ACTION_PREFIX, FETCH_WORKER_REQUESTS_HISTORY_URL} from "../constants";

export const fetchWorkerRequestsHistory = (
    dispatch,
    userId,
    pageNumber,
    sortField = "startDate",
    sortOrder = "desc"
) => {
    const userFilter = `filter=requester.id:${userId}`
    const pagination = `page=${pageNumber}&sort=${sortField},${sortOrder}`

    dispatch({type: `${FETCH_WORKER_REQUESTS_HISTORY_ACTION_PREFIX}_request`});
    const URL = `${FETCH_WORKER_REQUESTS_HISTORY_URL}?${userFilter}&${pagination}`;
    sendGetRequest(URL)
        .then(data => dispatch({
                type: `${FETCH_WORKER_REQUESTS_HISTORY_ACTION_PREFIX}_success`,
                response: data
            })
        )
        .catch(errorMsg => dispatch({
            type: `${FETCH_WORKER_REQUESTS_HISTORY_ACTION_PREFIX}_failure`,
            error: errorMsg
        }))
}

export const fetchWorkerRequestsHistoryReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_WORKER_REQUESTS_HISTORY_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }
        case `${FETCH_WORKER_REQUESTS_HISTORY_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                requests: action.response.content,
                requestsPage: action.response
            }
        }
        case `${FETCH_WORKER_REQUESTS_HISTORY_ACTION_PREFIX}_failure`: {
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