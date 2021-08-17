import {sendPatchRequest} from "../../../helpers/RequestHelper";
import {CHANGE_REQUEST_STATUS_ACTION_PREFIX, CHANGE_REQUEST_STATUS_URL} from "../constants";

const changeRequestStatus = (dispatch, {requestId, newStatus}) => {
    dispatch({type: `${CHANGE_REQUEST_STATUS_ACTION_PREFIX}_request`})
    sendPatchRequest(`${CHANGE_REQUEST_STATUS_URL}/${requestId}`, {
        status: newStatus
    })
        .then(data => dispatch({
            type: `${CHANGE_REQUEST_STATUS_ACTION_PREFIX}_success`,
            payload: {requestId},
            response: data
        }))
        .catch(errorMsg => dispatch({
            type: `${CHANGE_REQUEST_STATUS_ACTION_PREFIX}_failure`,
            error: errorMsg
        }))
}

export const cancelRequest = (dispatch, {requestId}) => changeRequestStatus(dispatch, {requestId, newStatus: "CANCELED"})

export const changeRequestStatusReducer = (state, action) => {
    switch (action.type) {
        case `${CHANGE_REQUEST_STATUS_ACTION_PREFIX}_request`: {
            return {
                ...state,
                contextError: null
            }
        }
        case `${CHANGE_REQUEST_STATUS_ACTION_PREFIX}_success`: {
            const changedStatusRequestId = action.payload.requestId
            const newStatus = action.response.status
            return {
                ...state,
                myRequests: {
                    ...state.myRequests,
                    requests: mapRequestsStatus(state.myRequests.requests, changedStatusRequestId, newStatus)
                },
                teamRequests: {
                    ...state.teamRequests,
                    requests: mapRequestsStatus(state.teamRequests.requests, changedStatusRequestId, newStatus)
                },
                companyRequests: {
                    ...state.companyRequests,
                    requests: mapRequestsStatus(state.companyRequests.requests, changedStatusRequestId, newStatus)
                }
            }
        }
        case `${CHANGE_REQUEST_STATUS_ACTION_PREFIX}_failure`: {
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

const mapRequestsStatus = (requests, requestId, newStatus) => {
    return requests.map(req => req.id === requestId ? {...req, status: newStatus} : req)
}