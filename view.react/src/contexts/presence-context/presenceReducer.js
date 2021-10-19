import {createForwardingReducer} from "../utils";
import {addPresenceConfirmationReducer} from "./actions/addPresenceConfirmation";
import {fetchMyPresenceConfirmationsReducer} from "./actions/fetchMyPresenceConfirmations";
import {fetchUsersPresenceConfirmationsReducer} from "./actions/fetchUsersPresenceConfirmations";
import {
    ADD_PRESENCE_CONFIRMATION_ACTION_PREFIX,
    FETCH_MY_PRESENCE_CONFIRMATIONS_ACTION_PREFIX,
    FETCH_USERS_PRESENCE_CONFIRMATIONS_ACTION_PREFIX
} from "./constants";

const presenceReducersMappings = {
    [FETCH_MY_PRESENCE_CONFIRMATIONS_ACTION_PREFIX]: {
        slicePath: "myConfirmations",
        reducer: fetchMyPresenceConfirmationsReducer
    },
    [FETCH_USERS_PRESENCE_CONFIRMATIONS_ACTION_PREFIX]: {
        slicePath: "usersConfirmations",
        reducer: fetchUsersPresenceConfirmationsReducer
    },
    [ADD_PRESENCE_CONFIRMATION_ACTION_PREFIX]: addPresenceConfirmationReducer
}

export const presenceReducer = createForwardingReducer(presenceReducersMappings)