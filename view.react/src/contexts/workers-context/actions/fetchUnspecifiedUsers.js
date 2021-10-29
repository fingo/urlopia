import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_UNSPECIFIED_USERS_ACTION_PREFIX, WORKERS_ENDPOINT} from "../constants";

const FILTER = '?filter=b2b:FALSE,ec:FALSE,active:';

export const fetchUnspecifiedUsers = (dispatch, showActive) => {
    dispatch({type: `${FETCH_UNSPECIFIED_USERS_ACTION_PREFIX}_request`})
    sendGetRequest(`${WORKERS_ENDPOINT}${FILTER}${showActive}`)
        .then(data => {
            dispatch({
                type: `${FETCH_UNSPECIFIED_USERS_ACTION_PREFIX}_success`,
                response: data,
            })
        })
        .catch(errorMsg => dispatch({
            type: `${FETCH_UNSPECIFIED_USERS_ACTION_PREFIX}_failure`,
            error: errorMsg,
        }))
}

export const fetchUnspecifiedUsersReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_UNSPECIFIED_USERS_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }
        case `${FETCH_UNSPECIFIED_USERS_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                unspecifiedUsers: action.response,
            }
        }
        case `${FETCH_UNSPECIFIED_USERS_ACTION_PREFIX}_failure`: {
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