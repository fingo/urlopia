import PropTypes from "prop-types";
import {useEffect} from "react";

import {cancelRequest} from "../../contexts/request-context/actions/changeRequestStatus";
import {fetchMyRequests} from "../../contexts/request-context/actions/fetchMyRequests";
import {useRequests} from "../../contexts/request-context/requestContext";
import {UserRequestsTab} from "./UserRequestsTab";

export const UserRequestsList = ({shouldFetchUserRequests}) => {
    const [state, requestsDispatch] = useRequests()
    const {requests} = state.myRequests

    useEffect(() => {
        if (shouldFetchUserRequests) {
            fetchMyRequests(requestsDispatch)
        }
    }, [requestsDispatch, shouldFetchUserRequests])

    return (
        <UserRequestsTab
            requests={requests}
            cancelRequest={(requestId) => cancelRequest(requestsDispatch, {requestId})}
        />
    )
};

UserRequestsList.propTypes = {
    shouldFetchUserRequests: PropTypes.bool.isRequired,
}
