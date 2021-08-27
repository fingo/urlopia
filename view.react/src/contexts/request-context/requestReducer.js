import {createForwardingReducer} from "../utils";
import {changeAcceptanceStatusReducer} from "./actions/changeAcceptanceStatus";
import {changeRequestStatusReducer} from "./actions/changeRequestStatus";
import {createRequestReducer} from "./actions/createRequest";
import {fetchAcceptancesReducer} from "./actions/fetchAcceptances";
import {fetchCompanyRequestsReducer} from "./actions/fetchCompanyRequests";
import {fetchMyRequestsReducer} from "./actions/fetchMyRequests";
import {
    CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX,
    CHANGE_REQUEST_STATUS_ACTION_PREFIX,
    CREATE_REQUEST_ACTION_PREFIX,
    FETCH_ACCEPTANCES_ACTION_PREFIX,
    FETCH_COMPANY_REQUESTS_ACTION_PREFIX,
    FETCH_MY_REQUESTS_ACTION_PREFIX,
} from "./constants";

const requestReducersMappings = {
    [`${FETCH_MY_REQUESTS_ACTION_PREFIX}`]: {
        slicePath: "myRequests",
        reducer: fetchMyRequestsReducer,
    },
    [`${FETCH_COMPANY_REQUESTS_ACTION_PREFIX}`]: {
        slicePath: "companyRequests",
        reducer: fetchCompanyRequestsReducer,
    },
    [`${FETCH_ACCEPTANCES_ACTION_PREFIX}`]: {
        slicePath: "teamRequests",
        reducer: fetchAcceptancesReducer,
    },
    [`${CHANGE_REQUEST_STATUS_ACTION_PREFIX}`]: changeRequestStatusReducer,
    [`${CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX}`]: changeAcceptanceStatusReducer,
    [`${CREATE_REQUEST_ACTION_PREFIX}`]: createRequestReducer,
}

export const requestReducer = createForwardingReducer(requestReducersMappings)
