import {createForwardingReducer} from "../utils";
import {changeRequestStatusReducer} from "./actions/changeRequestStatus";
import {fetchCompanyRequestsReducer} from "./actions/fetchCompanyRequests";
import {fetchMyRequestsReducer} from "./actions/fetchMyRequests";
import {
    CHANGE_REQUEST_STATUS_ACTION_PREFIX,
    FETCH_COMPANY_REQUESTS_ACTION_PREFIX,
    FETCH_MY_REQUESTS_ACTION_PREFIX,
} from "./constants";

const requestReducersMappings = {
    [`${FETCH_MY_REQUESTS_ACTION_PREFIX}`]: {
        slicePath: "myRequests",
        reducer: fetchMyRequestsReducer
    },
    [`${FETCH_COMPANY_REQUESTS_ACTION_PREFIX}`]: {
        slicePath: "companyRequests",
        reducer: fetchCompanyRequestsReducer
    },
    [`${CHANGE_REQUEST_STATUS_ACTION_PREFIX}`]: changeRequestStatusReducer
}

export const requestReducer = createForwardingReducer(requestReducersMappings)
