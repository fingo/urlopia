import {createForwardingReducer} from "../utils";
import {fetchHolidaysReducer} from "./actions/fetchHolidays";
import {saveHolidaysReducer} from "./actions/saveHolidays";
import {FETCH_HOLIDAYS_ACTION_PREFIX, SAVE_HOLIDAYS_ACTION_PREFIX} from "./constants";

const holidaysReducersMappings = {
    [`${FETCH_HOLIDAYS_ACTION_PREFIX}`]: fetchHolidaysReducer,
    [`${SAVE_HOLIDAYS_ACTION_PREFIX}`]: saveHolidaysReducer,
};

export const holidaysReducer = createForwardingReducer(holidaysReducersMappings);
