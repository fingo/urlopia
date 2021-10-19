import {sendGetRequest} from "../../../helpers/RequestHelper";
import {
    FETCH_USERS_VACATIONS_ACTION_PREFIX,
    FETCH_USERS_VACATIONS_ENDPOINT_POSTFIX,
    FETCH_USERS_VACATIONS_ENDPOINT_PREFIX
} from "../constants";

export const fetchUsersVacations = (dispatch, {userId}) => {
    dispatch({type: `${FETCH_USERS_VACATIONS_ACTION_PREFIX}_request`})
    sendGetRequest(`${FETCH_USERS_VACATIONS_ENDPOINT_PREFIX}${userId}${FETCH_USERS_VACATIONS_ENDPOINT_POSTFIX}`)
        .then(data => dispatch({
            type: `${FETCH_USERS_VACATIONS_ACTION_PREFIX}_success`,
            response: data
        }))
        .catch(errorMsg => dispatch({
            type: `${FETCH_USERS_VACATIONS_ACTION_PREFIX}_failure`,
            error: errorMsg
        }))
}

export const fetchUsersVacationsReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_USERS_VACATIONS_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }
        case `${FETCH_USERS_VACATIONS_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                usersVacations: action.response.usersVacations
            }
        }
        case `${FETCH_USERS_VACATIONS_ACTION_PREFIX}_failure`: {
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
