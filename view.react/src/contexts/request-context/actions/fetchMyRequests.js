import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_MY_REQUESTS_ACTION_PREFIX, FETCH_MY_REQUESTS_URL} from "../constants";

const TODAY = new Date().toJSON().slice(0, 10)
const PARAMS = new URLSearchParams()
PARAMS.append("filter", `endDate>:${TODAY}|status:PENDING`)
PARAMS.append("sort", "id,DESC")
PARAMS.append("size", "999")

export const fetchMyRequests = dispatch => {
    dispatch({type: `${FETCH_MY_REQUESTS_ACTION_PREFIX}_request`})
    sendGetRequest(`${FETCH_MY_REQUESTS_URL}`, PARAMS)
        .then(data => dispatch({
                type: `${FETCH_MY_REQUESTS_ACTION_PREFIX}_success`,
                response: data
            })
        )
        .catch(errorMsg => dispatch({
            type: `${FETCH_MY_REQUESTS_ACTION_PREFIX}_failure`,
            error: errorMsg
        }))
}

export const fetchMyRequestsReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_MY_REQUESTS_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }
        case `${FETCH_MY_REQUESTS_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                requests: action.response.content,
            }
        }
        case `${FETCH_MY_REQUESTS_ACTION_PREFIX}_failure`: {
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