import {pushSuccessNotification} from "../../../helpers/notifications/Notifications";
import {sendPutRequest} from "../../../helpers/RequestHelper";
import {HOLIDAYS_ENDPOINT_URL,SAVE_HOLIDAYS_ACTION_PREFIX} from "../constants";

export const saveHolidays = (dispatch, holidays) => {
    dispatch({type: `${SAVE_HOLIDAYS_ACTION_PREFIX}_request`})
    sendPutRequest(HOLIDAYS_ENDPOINT_URL, holidays)
        .then(data => {
            dispatch({
              type: `${SAVE_HOLIDAYS_ACTION_PREFIX}_success`,
              response: data,
            })
            pushSuccessNotification("Pomyślnie zaktualizowano święta");
        })
        .catch(errorMessage => dispatch({
            type: `${SAVE_HOLIDAYS_ACTION_PREFIX}_failure`,
            error: errorMessage,
        }))
}

export const saveHolidaysReducer = (state, action) => {
    switch (action.type) {
        case `${SAVE_HOLIDAYS_ACTION_PREFIX}_request`: {
            return {
                ...state,
                fetching: true,
                error: null,
            }
        }

        case `${SAVE_HOLIDAYS_ACTION_PREFIX}_success`: {
            return {
                ...state,
                fetching: false,
                holidays: action.response,
            }
        }

        case `${SAVE_HOLIDAYS_ACTION_PREFIX}_failure`: {
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