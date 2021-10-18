import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_WORKERS_ACTION_PREFIX, WORKERS_ENDPOINT} from "../constants";

const FILTER = '?filter=b2b:FALSE,ec:TRUE,active:true';

export const fetchWorkers = dispatch => {
    dispatch({type: `${FETCH_WORKERS_ACTION_PREFIX}_request`})
    sendGetRequest(`${WORKERS_ENDPOINT}${FILTER}`)
        .then(data => {
            dispatch({
                type: `${FETCH_WORKERS_ACTION_PREFIX}_success`,
                response: data,
            })
        })
        .catch(errorMsg => dispatch({
            type: `${FETCH_WORKERS_ACTION_PREFIX}_failure`,
            error: errorMsg,
        }))
}

export const fetchWorkersReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_WORKERS_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }
        case `${FETCH_WORKERS_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                workers: action.response,
            }
        }
        case `${FETCH_WORKERS_ACTION_PREFIX}_failure`: {
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