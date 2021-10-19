import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_WORKER_REQUESTS_HISTORY_ACTION_PREFIX, FETCH_WORKER_REQUESTS_HISTORY_URL} from "../constants";

const FIRST_NAME_FILTER = 'filter=requester.firstName:';
const LAST_NAME_FILTER = 'filter=requester.lastName:';
const SORT_FILTER = `&sort=id,DESC`

export const fetchWorkerRequestsHistory = (dispatch, firstName, lastName) => {
    dispatch({type: `${FETCH_WORKER_REQUESTS_HISTORY_ACTION_PREFIX}_request`});
    const URL = `${FETCH_WORKER_REQUESTS_HISTORY_URL}?${FIRST_NAME_FILTER}${firstName}&${LAST_NAME_FILTER}${lastName}${SORT_FILTER}`;
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