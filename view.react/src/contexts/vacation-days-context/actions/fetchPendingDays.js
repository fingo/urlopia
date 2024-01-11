import {sendGetRequest} from "../../../helpers/RequestHelper";
import {
    FETCH_MY_PENDING_DAYS_ACTION_PREFIX, FETCH_MY_PENDING_DAYS_URL,
} from "../constants";

export const fetchPendingDays = (dispatch) => {
    console.log('fetchPendingDays data')
    dispatch({type: `${FETCH_MY_PENDING_DAYS_ACTION_PREFIX}_request`})
    sendGetRequest(FETCH_MY_PENDING_DAYS_URL)
        .then(data => {
            dispatch({
                    type: `${FETCH_MY_PENDING_DAYS_ACTION_PREFIX}_success`,
                    response: data,
                })
            }
        )
        .catch(errorMsg => dispatch({
            type: `${FETCH_MY_PENDING_DAYS_ACTION_PREFIX}_failure`,
            error: errorMsg,
        }))
}

export const fetchPendingDaysReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_MY_PENDING_DAYS_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }
        case `${FETCH_MY_PENDING_DAYS_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                pendingDays: action.response,
            }
        }
        case `${FETCH_MY_PENDING_DAYS_ACTION_PREFIX}_failure`: {
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
