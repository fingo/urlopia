import {createForwardingReducer} from "../utils";
import {changeIsECReducer} from "./actions/changeIsEC";
import {changeRemainingDaysReducer} from "./actions/changeRemainingDays";
import {changeSelectedUserReducer} from "./actions/changeSelectedUser";
import {changeWorkTimeReducer} from "./actions/changeWorkTime";
import {fetchAssociatesReducer} from "./actions/fetchAssociates";
import {fetchRemainingDaysReducer} from "./actions/fetchRemainingDays";
import {fetchWorkersReducer} from "./actions/fetchWorkers";
import {
    CHANGE_IS_EC_ACTION_PREFIX,
    CHANGE_REMAINING_DAYS_ACTION_PREFIX,
    CHANGE_SELECTED_USER_ACTION_PREFIX,
    CHANGE_WORK_TIME_ACTION_PREFIX,
    FETCH_ASSOCIATES_ACTION_PREFIX,
    FETCH_REMAINING_DAYS_ACTION_PREFIX,
    FETCH_WORKERS_ACTION_PREFIX,
} from "./constants";

const workersReducersMappings = {
    [`${FETCH_WORKERS_ACTION_PREFIX}`]: {
        slicePath: 'workers',
        reducer: fetchWorkersReducer,
    },
    [`${FETCH_ASSOCIATES_ACTION_PREFIX}`]: {
        slicePath: 'associates',
        reducer: fetchAssociatesReducer,
    },
    [`${FETCH_REMAINING_DAYS_ACTION_PREFIX}`]: fetchRemainingDaysReducer,
    [`${CHANGE_WORK_TIME_ACTION_PREFIX}`]: changeWorkTimeReducer,
    [`${CHANGE_SELECTED_USER_ACTION_PREFIX}`]: changeSelectedUserReducer,
    [`${CHANGE_REMAINING_DAYS_ACTION_PREFIX}`]: changeRemainingDaysReducer,
    [`${CHANGE_IS_EC_ACTION_PREFIX}`]: changeIsECReducer,
};

export const workersReducer = createForwardingReducer(workersReducersMappings);
