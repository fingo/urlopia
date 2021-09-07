import {sendGetRequest} from "../../../helpers/RequestHelper";
import {FETCH_HOLIDAYS_ACTION_PREFIX,HOLIDAYS_ENDPOINT_URL} from "../constants";

export const fetchHolidays = (dispatch, year) => {
    const yearURL = year ? ("?year=" + year) : "";

    const URL_WITH_YEAR = `${HOLIDAYS_ENDPOINT_URL}${yearURL}`;
    dispatch({type: `${FETCH_HOLIDAYS_ACTION_PREFIX}_request`})
    sendGetRequest(URL_WITH_YEAR)
        .then(data => {
            dispatch({
                type: `${FETCH_HOLIDAYS_ACTION_PREFIX}_success`,
                response: data,
            })
        })
        .catch(errorMsg => dispatch({
            type: `${FETCH_HOLIDAYS_ACTION_PREFIX}_failure`,
            error: errorMsg,
        }))
}

export const fetchHolidaysReducer = (state, action) => {
    switch (action.type) {
        case `${FETCH_HOLIDAYS_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }
        case `${FETCH_HOLIDAYS_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                holidays: action.response,
            }
        }
        case `${FETCH_HOLIDAYS_ACTION_PREFIX}_failure`: {
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