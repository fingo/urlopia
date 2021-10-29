import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_ASSOCIATES_ACTION_PREFIX, WORKERS_ENDPOINT} from "../constants";

const FILTER = '?filter=b2b:TRUE,ec:FALSE,active:';

export const fetchAssociates = (dispatch,showActive)  => {
    dispatch({type: `${FETCH_ASSOCIATES_ACTION_PREFIX}_request`})
    sendGetRequest(`${WORKERS_ENDPOINT}${FILTER}${showActive}`)
        .then(data => {
            dispatch({
                type: `${FETCH_ASSOCIATES_ACTION_PREFIX}_success`,
                response: data,
            })
        })
        .catch(errorMsg => dispatch({
            type: `${FETCH_ASSOCIATES_ACTION_PREFIX}_failure`,
            error: errorMsg,
        }))
}

export const fetchAssociatesReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_ASSOCIATES_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }
        case `${FETCH_ASSOCIATES_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                associates: action.response,
            }
        }
        case `${FETCH_ASSOCIATES_ACTION_PREFIX}_failure`: {
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