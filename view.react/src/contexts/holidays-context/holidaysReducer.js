import {createForwardingReducer} from "../utils";
import {fetchHolidaysReducer} from "./actions/fetchHolidays";
import {FETCH_HOLIDAYS_ACTION_PREFIX} from "./constants";

const holidaysReducersMappings = {
    [`${FETCH_HOLIDAYS_ACTION_PREFIX}`]: fetchHolidaysReducer
};

export const holidaysReducer = createForwardingReducer(holidaysReducersMappings);
