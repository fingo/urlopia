import {sendPostRequest} from "../../../helpers/RequestHelper";
import {CREATE_REQUEST_ACTION_PREFIX, CREATE_REQUEST_URL} from "../constants";

export const createRequest = (dispatch, {startDate, endDate, type, occasionalType}, isAdmin) => {
    dispatch({type: `${CREATE_REQUEST_ACTION_PREFIX}_request`})
    sendPostRequest(CREATE_REQUEST_URL, {
        startDate,
        endDate,
        type,
        occasionalType,
    })
        .then(data => dispatch({
            type: `${CREATE_REQUEST_ACTION_PREFIX}_success`,
            payload: {
                isAdmin,
                occasionalType,
            },
            response: data,
        }))
        .catch(errorMsg => dispatch({
            type: `${CREATE_REQUEST_ACTION_PREFIX}_failure`,
            error: errorMsg,
        }))
}

export const createRequestReducer = (state, action) => {
    switch (action.type) {
        case `${CREATE_REQUEST_ACTION_PREFIX}_request`: {
            return {
                ...state,
                contextError: null
            }
        }
        case `${CREATE_REQUEST_ACTION_PREFIX}_success`: {
            const {isAdmin, occasionalType} = action.payload;
            let newCompanyRequests = state.companyRequests.requests
            if (!occasionalType && isAdmin) {
                newCompanyRequests = [action.response,...state.companyRequests.requests];
            }

            return {
                ...state,
                myRequests: {
                    ...state.myRequests,
                    fetching: false,
                    requests: [action.response, ...state.myRequests.requests],
                },
                companyRequests: {
                  ...state.companyRequests,
                  fetching: false,
                  requests: newCompanyRequests,
                },
            }
        }
        case `${CREATE_REQUEST_ACTION_PREFIX}_failure`: {
            return {
                ...state,
                contextError: action.error
            }
        }
        default: {
            throw new Error(`Unhandled exception type ${action.type}`)
        }
    }
}