import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_ACCEPTANCES_ACTION_PREFIX, FETCH_ACCEPTANCES_URL} from "../constants";

const SORT_FILTER = `&sort=id,DESC`
const FILTER = "?filter=status:PENDING"

export const fetchAcceptances = dispatch => {
    dispatch({type: `${FETCH_ACCEPTANCES_ACTION_PREFIX}_request`})
    sendGetRequest(`${FETCH_ACCEPTANCES_URL}${FILTER}${SORT_FILTER}`)
        .then(data => dispatch({
                type: `${FETCH_ACCEPTANCES_ACTION_PREFIX}_success`,
                response: data
            })
        )
        .catch(errorMsg => dispatch({
            type: `${FETCH_ACCEPTANCES_ACTION_PREFIX}_failure`,
            error: errorMsg
        }))
}

export const fetchAcceptancesReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_ACCEPTANCES_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }
        case `${FETCH_ACCEPTANCES_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                requests: action.response.content,
            }
        }
        case `${FETCH_ACCEPTANCES_ACTION_PREFIX}_failure`: {
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