import {sendPatchRequest} from "../../../helpers/RequestHelper";
import {CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX, CHANGE_ACCEPTANCE_STATUS_URL} from "../constants";

const changeAcceptanceStatus = (dispatch, {acceptanceId, newStatus}) => {
    dispatch({type: `${CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX}_request`})
    sendPatchRequest(`${CHANGE_ACCEPTANCE_STATUS_URL}/${acceptanceId}`, {
        status: newStatus
    })
        .then(data => dispatch({
            type: `${CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX}_success`,
            payload: {acceptanceId},
            response: data
        }))
        .catch(errorMsg => dispatch({
            type: `${CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX}_failure`,
            error: errorMsg
        }))
}

export const acceptAcceptance = (dispatch, {acceptanceId}) => changeAcceptanceStatus(dispatch, {acceptanceId, newStatus: "ACCEPTED"})
export const rejectAcceptance = (dispatch, {acceptanceId}) => changeAcceptanceStatus(dispatch, {acceptanceId, newStatus: "REJECTED"})

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
