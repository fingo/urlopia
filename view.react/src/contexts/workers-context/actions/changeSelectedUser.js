import {CHANGE_SELECTED_USER_ACTION_PREFIX} from "../constants";

export const changeSelectedUser = (userId) => {
    return {
        type: `${CHANGE_SELECTED_USER_ACTION_PREFIX}_change`,
        payload: {
            userId,
        },
    }
}

export const changeSelectedUserReducer = (state, action) => {
    switch (action.type) {
        case `${CHANGE_SELECTED_USER_ACTION_PREFIX}_change`: {
            const {userId} = action.payload;
            const foundWorker = state.workers.find(worker => worker.userId === userId);
            return {
                ...state,
                fetching: false,
                selectedUser: foundWorker,
            }
        }
        default: {
            throw new Error(`Unhandled exception type ${action.type}`)
        }
    }
}