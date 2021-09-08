import {createForwardingReducer} from "../utils";
import {fetchAbsenceHistoryReducer} from "./actions/fetchAbsenceHistory";
import {fetchUserRecentAbsenceHistoryReducer} from "./actions/fetchUserAbsenceHistory";
import {FETCH_ABSENCE_HISTORY_ACTION_PREFIX, FETCH_USER_RECENT_ABSENCE_HISTORY_ACTION_PREFIX} from "./constants";

const absenceHistoryReducersMappings = {
    [`${FETCH_ABSENCE_HISTORY_ACTION_PREFIX}`]: fetchAbsenceHistoryReducer,
    [`${FETCH_USER_RECENT_ABSENCE_HISTORY_ACTION_PREFIX}`]: fetchUserRecentAbsenceHistoryReducer,
}

export const absenceHistoryReducer = createForwardingReducer(absenceHistoryReducersMappings)