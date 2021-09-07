import {pushSuccessNotification} from "../../../helpers/notifications/Notifications";
import {sendPatchRequest} from "../../../helpers/RequestHelper";
import {CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX, CHANGE_ACCEPTANCE_STATUS_URL} from "../constants";

const changeAcceptanceStatus = (dispatch, {acceptanceId, newStatus}) => {
    dispatch({type: `${CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX}_request`})
    sendPatchRequest(`${CHANGE_ACCEPTANCE_STATUS_URL}/${acceptanceId}`, {
        status: newStatus
    })
        .then(data => {
            const action = {
                type: `${CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX}_success`,
                payload: {acceptanceId},
                response: data
            }
            dispatch(action)
            pushNotificationOnSuccess(action)
        })
        .catch(errorMsg => dispatch({
            type: `${CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX}_failure`,
            error: errorMsg
        }))
}

export const acceptAcceptance = (dispatch, {acceptanceId}) => changeAcceptanceStatus(dispatch, {acceptanceId, newStatus: "ACCEPTED"})
export const rejectAcceptance = (dispatch, {acceptanceId}) => changeAcceptanceStatus(dispatch, {acceptanceId, newStatus: "REJECTED"})

const pushNotificationOnSuccess = action => {
    const {status} = action.response

    let message = "Pomyślnie zaakceptowano wniosek o urlop"
    if (status === 'REJECTED') {
        message = "Pomyślnie odrzucono wniosek o urlop"
    }

    pushSuccessNotification(message)
}

export const changeAcceptanceStatusReducer = (state, action) => {
    switch (action.type) {
        case `${CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX}_request`: {
            return {
                ...state,
                contextError: null
            }
        }
        case `${CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX}_success`: {
            const changedStatusAcceptanceId = action.payload.acceptanceId
            return {
                ...state,
                teamRequests: {
                    ...state.teamRequests,
                    requests: state.teamRequests.requests.filter(req => req.id !== changedStatusAcceptanceId)
                }
            }
        }
        case `${CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX}_failure`: {
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
