import {getCurrentUser} from "../../../api/services/session.service";
import {pushSuccessNotification} from "../../../helpers/notifications/Notifications";
import {sendPutRequest} from "../../../helpers/RequestHelper";
import {ADD_PRESENCE_CONFIRMATION_ACTION_PREFIX, ADD_PRESENCE_CONFIRMATION_URL} from "../constants";

export const addPresenceConfirmation = (dispatch, {date, startTime, endTime, userId}) => {
    dispatch({type: `${ADD_PRESENCE_CONFIRMATION_ACTION_PREFIX}_request`})
    const body = {
        date,
        startTime,
        endTime,
        userId
    }
    sendPutRequest(ADD_PRESENCE_CONFIRMATION_URL, body)
        .then(data => {
            const action = {
                type: `${ADD_PRESENCE_CONFIRMATION_ACTION_PREFIX}_success`,
                response: data
            }
            dispatch(action)
            pushNotificationOnSuccess(action)
        })
        .catch(error => {
            dispatch({
                type: `${ADD_PRESENCE_CONFIRMATION_ACTION_PREFIX}_failure`,
                error: error.message
            })
        })
}

const pushNotificationOnSuccess = action => {
    const {isAdmin, userId} = getCurrentUser()
    const confirmation = action.response
    const {userId: confirmationUserId, startTime, endTime} = confirmation
    const confirmingHisOwnPresence = userId === confirmationUserId

    if (confirmingHisOwnPresence) {
        pushSuccessNotification(`Pomyślnie zgłoszono obecność w godzinach ${startTime} - ${endTime}`)
    } else if (isAdmin) {
        pushSuccessNotification(`Pomyślnie zgłoszono obecność wybranego użytkownika w godzinach ${startTime} - ${endTime}`)
    }
}

export const addPresenceConfirmationReducer = (state, action) => {
    switch (action.type) {
        case `${ADD_PRESENCE_CONFIRMATION_ACTION_PREFIX}_request`: {
            return {
                ...state,
                contextError: null
            }
        }
        case `${ADD_PRESENCE_CONFIRMATION_ACTION_PREFIX}_success`: {
            return handleSuccess(state, action)
        }
        case `${ADD_PRESENCE_CONFIRMATION_ACTION_PREFIX}_failure`: {
            return {
                ...state,
                contextError: action.error,
            }
        }
        default: {
            throw new Error(`Unhandled exception type ${action.type}`)
        }
    }
}

const handleSuccess = (state, action) => {
    const {isAdmin, userId} = getCurrentUser()
    const confirmation = action.response
    const {userId: confirmationUserId, date} = confirmation
    const confirmingHisOwnPresence = userId === confirmationUserId

    let newState = {...state}

    if (confirmingHisOwnPresence) {
        newState = {
            ...newState,
            myConfirmations: {
                confirmations: {
                    ...state.myConfirmations.confirmations,
                    [`${date}`]: confirmation
                }
            }
        }
    }

    if (isAdmin) {
        newState = {
            ...newState,
            usersConfirmations: {
                confirmations: {
                    ...state.usersConfirmations.confirmations,
                    [confirmationUserId]: {
                        ...state.usersConfirmations.confirmations[confirmationUserId],
                        [date]: confirmation
                    }
                }
            }
        }
    }

    return newState
}
