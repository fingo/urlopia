import {createForwardingReducer} from "../utils";
import {fetchMyAbsenceHistoryReducer} from "./actions/fetchMyAbsenceHistory";
import {fetchUserAbsenceHistoryReducer} from "./actions/fetchUserAbsenceHistory";
import {FETCH_MY_ABSENCE_HISTORY_ACTION_PREFIX, FETCH_USER_ABSENCE_HISTORY_PREFIX} from "./constants";

const absenceHistoryReducersMappings = {
    [`${FETCH_MY_ABSENCE_HISTORY_ACTION_PREFIX}`]: {
        slicePath: "myAbsenceHistory",
        reducer: fetchMyAbsenceHistoryReducer,
    },
    [`${FETCH_USER_ABSENCE_HISTORY_PREFIX}`]: {
        slicePath: "userAbsenceHistory",
        reducer: fetchUserAbsenceHistoryReducer,
    }
}

export const absenceHistoryReducer = createForwardingReducer(absenceHistoryReducersMappings)