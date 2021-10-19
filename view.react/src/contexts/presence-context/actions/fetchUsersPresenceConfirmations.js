import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_USERS_PRESENCE_CONFIRMATIONS_ACTION_PREFIX, FETCH_USERS_PRESENCE_CONFIRMATIONS_URL} from "../constants";

export const fetchUsersPresenceConfirmations = dispatch => {
    dispatch({type: `${FETCH_USERS_PRESENCE_CONFIRMATIONS_ACTION_PREFIX}_request`})
    sendGetRequest(`${FETCH_USERS_PRESENCE_CONFIRMATIONS_URL}`)
        .then(data => dispatch({
            type: `${FETCH_USERS_PRESENCE_CONFIRMATIONS_ACTION_PREFIX}_success`,
            response: data
        }))
        .catch(errorMsg => dispatch({
            type: `${FETCH_USERS_PRESENCE_CONFIRMATIONS_ACTION_PREFIX}_failure`,
            error: errorMsg
        }))
}

export const fetchUsersPresenceConfirmationsReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_USERS_PRESENCE_CONFIRMATIONS_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }
        case `${FETCH_USERS_PRESENCE_CONFIRMATIONS_ACTION_PREFIX}_success`: {
            return handleSuccess(state, action)
        }
        case `${FETCH_USERS_PRESENCE_CONFIRMATIONS_ACTION_PREFIX}_failure`: {
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

const handleSuccess = (state, action) => {
    const newStateConfirmations = {}
    const fetchedConfirmations = action.response

    const userIds = [...new Set(fetchedConfirmations.map(confirmation => confirmation.userId))]
    userIds.forEach(userId => newStateConfirmations[`${userId}`] = {})

    fetchedConfirmations.forEach(confirmation => {
        const {userId, date} = confirmation
        newStateConfirmations[userId][date] = confirmation
    })

    return {
        ...state,
        fetching: false,
        confirmations: newStateConfirmations
    }
}
