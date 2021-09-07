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
            const {isEC} = state;
            const {userId} = action.payload;
            if (isEC) {
                const foundWorker = state.workers.workers.find(worker => worker.userId === userId);
                return {
                    ...state,
                    workers: {
                        ...state.workers,
                        fetching: false,
                        selectedWorker: foundWorker,
                    },
                }
            }
            else {
                const foundAssociate = state.associates.associates.find(worker => worker.userId === userId);
                return {
                    ...state,
                    associates: {
                        ...state.associates,
                        fetching: false,
                        selectedAssociate: foundAssociate,
                    },
                }
            }
        }
        default: {
            throw new Error(`Unhandled exception type ${action.type}`)
        }
    }
}