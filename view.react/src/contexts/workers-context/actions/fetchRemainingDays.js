import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_REMAINING_DAYS_ACTION_PREFIX, FETCH_REMAINING_DAYS_URL_POSTFIX, WORKERS_ENDPOINT} from "../constants";

export const fetchRemainingDays = (dispatch, userId) => {
    dispatch({type: `${FETCH_REMAINING_DAYS_ACTION_PREFIX}_request`})
    sendGetRequest(`${WORKERS_ENDPOINT}/${userId}${FETCH_REMAINING_DAYS_URL_POSTFIX}`)
        .then(data => dispatch({
                type: `${FETCH_REMAINING_DAYS_ACTION_PREFIX}_success`,
                response: data,
            })
        )
        .catch(errorMsg => dispatch({
            type: `${FETCH_REMAINING_DAYS_ACTION_PREFIX}_failure`,
            error: errorMsg,
        }))
}

export const fetchRemainingDaysReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_REMAINING_DAYS_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }
        case `${FETCH_REMAINING_DAYS_ACTION_PREFIX}_success`: {
            const {isEC} = state;
            if (isEC) {
                return {
                    ...state,
                    workers: {
                        ...state.workers,
                        fetching: false,
                        remainingDaysOfCurrentSelectedWorker: action.response,
                    }
                }
            } else {
                return {
                    ...state,
                    associates: {
                        ...state.associates,
                        fetching: false,
                        remainingDaysOfCurrentSelectedAssociate: action.response,
                    }
                }
            }
        }
        case `${FETCH_REMAINING_DAYS_ACTION_PREFIX}_failure`: {
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