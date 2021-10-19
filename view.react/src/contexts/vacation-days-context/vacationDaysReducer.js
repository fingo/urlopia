import {createForwardingReducer} from "../utils";
import {fetchPendingDaysReducer} from "./actions/fetchPendingDays";
import {fetchVacationDaysReducer} from "./actions/fetchVacationDays";
import {FETCH_MY_PENDING_DAYS_ACTION_PREFIX, FETCH_MY_VACATION_DAYS_ACTION_PREFIX} from "./constants";

const vacationDaysReducersMappings = {
    [`${FETCH_MY_PENDING_DAYS_ACTION_PREFIX}`]: fetchPendingDaysReducer,
    [`${FETCH_MY_VACATION_DAYS_ACTION_PREFIX}`]: fetchVacationDaysReducer,
}

export const vacationDaysReducer = createForwardingReducer(vacationDaysReducersMappings)
