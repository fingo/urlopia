import {createForwardingReducer} from "../utils";
import {changeWorkingHoursPreferencesReducer} from "./actions/changeWorkingHoursPreferences";
import {fetchWorkingHoursPreferencesReducer} from "./actions/fetchWorkingHoursPreferences";
import {
    CHANGE_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX,
    FETCH_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX
} from "./constants";

const presenceReducersMappings = {
    [FETCH_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX]: {
        slicePath: "workingHours",
        reducer: fetchWorkingHoursPreferencesReducer
    },
    [CHANGE_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX]: {
        slicePath: "workingHours",
        reducer: changeWorkingHoursPreferencesReducer
    }
}

export const userPreferenceReducer = createForwardingReducer(presenceReducersMappings)