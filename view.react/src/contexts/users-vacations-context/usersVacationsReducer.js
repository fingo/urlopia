import {createForwardingReducer} from "../utils";
import {fetchUsersVacationsReducer} from "./actions/fetchUsersVacations";
import {FETCH_USERS_VACATIONS_ACTION_PREFIX} from "./constants";

const usersVacationsReducersMappings = {
    [FETCH_USERS_VACATIONS_ACTION_PREFIX]: fetchUsersVacationsReducer
}

export const usersVacationsReducer = createForwardingReducer(usersVacationsReducersMappings)