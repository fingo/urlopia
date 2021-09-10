import {sendGetRequest} from "../../../helpers/RequestHelper";
import {NO_ACTION_WORKERS_ACTION_PREFIX, NO_ACTION_WORKERS_URL} from "../constants";

export const fetchNoActionWorkers = dispatch => {
    dispatch({type: `${NO_ACTION_WORKERS_ACTION_PREFIX}_request`});
    sendGetRequest(NO_ACTION_WORKERS_URL)
        .then(data => {
            dispatch({
                type: `${NO_ACTION_WORKERS_ACTION_PREFIX}_success`,
                response: data,
            })
        })
        .catch(errorMsg => dispatch({
            type: `${NO_ACTION_WORKERS_ACTION_PREFIX}_failure`,
            error: errorMsg,
        }))
}

export const fetchNoActionWorkersReducer = (state, action) => {
    switch (action.type) {
        case `${NO_ACTION_WORKERS_ACTION_PREFIX}_request`: {
            const {isEC} = state;
            if (isEC) {
                return {
                    ...state,
                    workers: {
                        ...state.workers,
                        fetching: true,
                        areUnspecifiedAbsencesFetched: false,
                        error: null,
                    }
                }
            }
            else {
                return {
                    ...state,
                    associates: {
                        ...state.associates,
                        fetching: true,
                        error: null,
                    }
                }
            }

        }
        case `${NO_ACTION_WORKERS_ACTION_PREFIX}_success`: {
            const {isEC} = state;
            if (isEC) {
                return {
                    ...state,
                    workers: {
                        ...state.workers,
                        fetching: false,
                        areUnspecifiedAbsencesFetched: true,
                        unspecifiedAbsences: action.response.users,
                    },
                }
            } else {
                return {
                    ...state,
                    associates: {
                        ...state.associates,
                        fetching: false,
                        unspecifiedAbsences: action.response.users,
                    },
                }
            }
        }
        case `${NO_ACTION_WORKERS_ACTION_PREFIX}_failure`: {
            return {
                ...state,
                fetching: false,
                areUnspecifiedAbsencesFetched: false,
                error: action.error,
            }
        }
        default: {
            throw new Error(`Unhandled exception type ${action.type}`)
        }
    }
}