import {createForwardingReducer} from "../utils";
import {fetchAppInfoReducer} from "./actions/fetchAppInfo";
import {FETCH_APP_INFO_ACTION_PREFIX} from "./contants";

const appInfoReducersMappings = {
    [FETCH_APP_INFO_ACTION_PREFIX]: fetchAppInfoReducer
}

export const appInfoReducer = createForwardingReducer(appInfoReducersMappings)