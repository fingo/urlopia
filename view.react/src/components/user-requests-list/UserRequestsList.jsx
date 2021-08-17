import {useEffect} from "react";

import {cancelRequest} from "../../contexts/request-context/actions/changeRequestStatus";
import {fetchMyRequests} from "../../contexts/request-context/actions/fetchMyRequests";
import {useRequests} from "../../contexts/request-context/requestContext";
import {UserRequestsTab} from "./UserRequestsTab";

export const UserRequestsList = () => {
    const [state, requestsDispatch] = useRequests()
    const {requests} = state.myRequests

    useEffect(() => {
        fetchMyRequests(requestsDispatch)
    }, [requestsDispatch])

    return (
        <UserRequestsTab
            requests={requests}
            cancelRequest={(requestId) => cancelRequest(requestsDispatch, {requestId})}
        />
    )
};
