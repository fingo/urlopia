import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_APP_INFO_ACTION_PREFIX, FETCH_APP_INFO_URL} from "../contants";

export const fetchAppInfo = (dispatch) => {
    dispatch({type: `${FETCH_APP_INFO_ACTION_PREFIX}_request`})
    sendGetRequest(`${FETCH_APP_INFO_URL}`)
        .then(data => dispatch({
            type: `${FETCH_APP_INFO_ACTION_PREFIX}_success`,
            response: data
        }))
        .catch(errorMsg => dispatch({
            type: `${FETCH_APP_INFO_ACTION_PREFIX}_failure`,
            error: errorMsg
        }))
}

export const fetchAppInfoReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_APP_INFO_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }
        case `${FETCH_APP_INFO_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                appInfo: action.response
            }
        }
        case `${FETCH_APP_INFO_ACTION_PREFIX}_failure`: {
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