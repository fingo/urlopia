import {getCurrentUser} from "../../../api/services/session.service";
import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_MY_PRESENCE_CONFIRMATIONS_ACTION_PREFIX, FETCH_MY_PRESENCE_CONFIRMATIONS_URL} from "../constants";

export const fetchMyPresenceConfirmations = dispatch => {
    dispatch({type: `${FETCH_MY_PRESENCE_CONFIRMATIONS_ACTION_PREFIX}_request`})

    const user = getCurrentUser()
    const userId = user.userId || -1
    const FILTER = `?filter=userId:${userId}`

    sendGetRequest(`${FETCH_MY_PRESENCE_CONFIRMATIONS_URL}${FILTER}`)
        .then(data => dispatch({
            type: `${FETCH_MY_PRESENCE_CONFIRMATIONS_ACTION_PREFIX}_success`,
            response: data
        }))
        .catch(errorMsg => dispatch({
            type: `${FETCH_MY_PRESENCE_CONFIRMATIONS_ACTION_PREFIX}_failure`,
            error: errorMsg
        }))
}

export const fetchMyPresenceConfirmationsReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_MY_PRESENCE_CONFIRMATIONS_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }
        case `${FETCH_MY_PRESENCE_CONFIRMATIONS_ACTION_PREFIX}_success`: {
            const newStateConfirmations = {}
            action.response.forEach(confirmation => {
                newStateConfirmations[confirmation.date] = confirmation
            })

            return {
                ...state,
                fetching: false,
                confirmations: newStateConfirmations
            }
        }
        case `${FETCH_MY_PRESENCE_CONFIRMATIONS_ACTION_PREFIX}_failure`: {
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
