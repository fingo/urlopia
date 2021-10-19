import {pushSuccessNotification} from "../../../helpers/notifications/Notifications";
import {sendPatchRequest} from "../../../helpers/RequestHelper";
import {CHANGE_REQUEST_STATUS_ACTION_PREFIX, CHANGE_REQUEST_STATUS_URL} from "../constants";

const changeRequestStatus = (dispatch, {requestId, newStatus}) => {
    dispatch({type: `${CHANGE_REQUEST_STATUS_ACTION_PREFIX}_request`})
    sendPatchRequest(`${CHANGE_REQUEST_STATUS_URL}/${requestId}`, {
        status: newStatus
    })
        .then(data => {
            const action = {
                type: `${CHANGE_REQUEST_STATUS_ACTION_PREFIX}_success`,
                payload: {requestId},
                response: data
            }
            dispatch(action)
            pushNotificationOnSuccess(action)
        })
        .catch(error => dispatch({
            type: `${CHANGE_REQUEST_STATUS_ACTION_PREFIX}_failure`,
            error: error.message
        }))
}

export const cancelRequest = (dispatch, {requestId}) => changeRequestStatus(dispatch, {requestId, newStatus: "CANCELED"})
export const acceptRequest = (dispatch, {requestId}) => changeRequestStatus(dispatch, {requestId, newStatus: "ACCEPTED"})
export const rejectRequest = (dispatch, {requestId}) => changeRequestStatus(dispatch, {requestId, newStatus: "REJECTED"})

const pushNotificationOnSuccess = action => {
    const {status} = action.response
    let message = "Pomyślnie anulowano wniosek o urlop"
    if (status === "ACCEPTED") {
        message = "Pomyślnie zaakceptowano wniosek o urlop"
    } else if (status === "REJECTED") {
        message = "Pomyślnie odrzucono wniosek o urlop"
    }

    pushSuccessNotification(message)
}

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
                    requests: handleMyRequestsStatusChange(state.myRequests.requests, changedStatusRequestId, newStatus)
                },
                acceptances: {
                    ...state.acceptances,
                    pending: handleTeamRequestsStatusChange(state.acceptances.pending, changedStatusRequestId, newStatus)
                },
                companyRequests: {
                    ...state.companyRequests,
                    requests: handleCompanyRequestsStatusChange(state.companyRequests.requests, changedStatusRequestId, newStatus)
                },
                workerRequestsHistory: {
                    ...state.workerRequestsHistory,
                    requests: handleWorkerRequestsHistoryStatusChange(state.workerRequestsHistory.requests, changedStatusRequestId, newStatus),
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

const handleMyRequestsStatusChange = (requests, requestId, newStatus) => {
    switch (newStatus) {
        case "CANCELED":
        case "ACCEPTED":
        case "REJECTED":
            const request = requests.find(req => req.id === requestId)
            if (request && isRequestEndDateInFuture(request)) {
                return updateRequestStatus(requests, requestId, newStatus)
            }
            return removeRequest(requests, requestId)
        default:
            return requests
    }
}

const isRequestEndDateInFuture = request => {
    let today = new Date()
    today.setDate(today.getDate() - 1)
    return today <= new Date(request.endDate)
}

const handleTeamRequestsStatusChange = (requests, requestId, newStatus) => {
    switch (newStatus) {
        case "CANCELED":
        case "ACCEPTED":
        case "REJECTED":
            return removeRequest(requests, requestId)
        default:
            return requests
    }
}

const handleCompanyRequestsStatusChange = (requests, requestId, newStatus) => {
    switch (newStatus) {
        case "CANCELED":
        case "ACCEPTED":
        case "REJECTED":
            return removeRequest(requests, requestId)
        default:
            return requests
    }
}

const handleWorkerRequestsHistoryStatusChange = (requests, requestId, newStatus) => {
    switch (newStatus) {
        case "CANCELED":
        case "ACCEPTED":
        case "REJECTED":
            return updateRequestStatus(requests, requestId, newStatus)
        default:
            return requests
    }
}

const updateRequestStatus = (requests, requestId, newStatus) => {
    return requests.map(req => req.id === requestId ? {...req, status: newStatus} : req)
}

const removeRequest = (requests, requestId) => {
    return requests.filter(req => req.id !== requestId)
}