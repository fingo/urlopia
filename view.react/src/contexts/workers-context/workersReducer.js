import {createForwardingReducer} from "../utils";
import {changeIsECReducer} from "./actions/changeIsEC";
import {changeNoActionWorkersReducer} from "./actions/changeNoActionWorkers";
import {changeRemainingDaysReducer} from "./actions/changeRemainingDays";
import {changeSelectedUserReducer} from "./actions/changeSelectedUser";
import {changeWorkTimeReducer} from "./actions/changeWorkTime";
import {fetchAssociatesReducer} from "./actions/fetchAssociates";
import {fetchNoActionWorkersReducer} from "./actions/fetchNoActionWorkers";
import {fetchRemainingDaysReducer} from "./actions/fetchRemainingDays";
import {fetchUnspecifiedUsersReducer} from "./actions/fetchUnspecifiedUsers";
import {fetchWorkersReducer} from "./actions/fetchWorkers";
import {
    CHANGE_IS_EC_ACTION_PREFIX, CHANGE_NO_ACTION_WORKERS_ACTION_PREFIX,
    CHANGE_REMAINING_DAYS_ACTION_PREFIX,
    CHANGE_SELECTED_USER_ACTION_PREFIX,
    CHANGE_WORK_TIME_ACTION_PREFIX,
    FETCH_ASSOCIATES_ACTION_PREFIX,
    FETCH_REMAINING_DAYS_ACTION_PREFIX,
    FETCH_UNSPECIFIED_USERS_ACTION_PREFIX,
    FETCH_WORKERS_ACTION_PREFIX,
    NO_ACTION_WORKERS_ACTION_PREFIX,
} from "./constants";

const workersReducersMappings = {
    [`${NO_ACTION_WORKERS_ACTION_PREFIX}`]: fetchNoActionWorkersReducer,
    [`${FETCH_WORKERS_ACTION_PREFIX}`]: {
        slicePath: 'workers',
        reducer: fetchWorkersReducer,
    },
    [`${FETCH_ASSOCIATES_ACTION_PREFIX}`]: {
        slicePath: 'associates',
        reducer: fetchAssociatesReducer,
    },
    [`${FETCH_REMAINING_DAYS_ACTION_PREFIX}`]: fetchRemainingDaysReducer,
    [`${FETCH_UNSPECIFIED_USERS_ACTION_PREFIX}`]: fetchUnspecifiedUsersReducer,
    [`${CHANGE_WORK_TIME_ACTION_PREFIX}`]: changeWorkTimeReducer,
    [`${CHANGE_SELECTED_USER_ACTION_PREFIX}`]: changeSelectedUserReducer,
    [`${CHANGE_REMAINING_DAYS_ACTION_PREFIX}`]: changeRemainingDaysReducer,
    [`${CHANGE_IS_EC_ACTION_PREFIX}`]: changeIsECReducer,
    [`${CHANGE_NO_ACTION_WORKERS_ACTION_PREFIX}`]: {
        slicePath: 'workers',
        reducer: changeNoActionWorkersReducer,
    },
};

export const workersReducer = createForwardingReducer(workersReducersMappings);
