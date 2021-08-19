import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_COMPANY_REQUESTS_ACTION_PREFIX, FETCH_COMPANY_REQUESTS_URL,} from "../constants";

const FILTER = "?filter=status:PENDING"

export const fetchCompanyRequests = dispatch => {
    dispatch({type: `${FETCH_COMPANY_REQUESTS_ACTION_PREFIX}_request`})
    sendGetRequest(`${FETCH_COMPANY_REQUESTS_URL}${FILTER}`)
        .then(data => dispatch({
            type: `${FETCH_COMPANY_REQUESTS_ACTION_PREFIX}_success`,
            response: data
        }))
        .catch(errorMsg => dispatch({
            type: `${FETCH_COMPANY_REQUESTS_ACTION_PREFIX}_failure`,
            error: errorMsg
        }))
}

export const fetchCompanyRequestsReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_COMPANY_REQUESTS_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }
        case `${FETCH_COMPANY_REQUESTS_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                requests: action.response.content,
            }
        }
        case `${FETCH_COMPANY_REQUESTS_ACTION_PREFIX}_failure`: {
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