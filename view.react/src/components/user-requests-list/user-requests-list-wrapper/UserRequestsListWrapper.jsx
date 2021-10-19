import PropTypes from "prop-types";
import {useEffect} from "react";

import {cancelRequest} from "../../../contexts/request-context/actions/changeRequestStatus";
import {fetchMyRequests} from "../../../contexts/request-context/actions/fetchMyRequests";
import {useRequests} from "../../../contexts/request-context/requestContext";
import {UserRequestsList} from "../UserRequestsList";

export const UserRequestsListWrapper = ({shouldFetchUserRequests}) => {
    const [state, requestsDispatch] = useRequests()
    const {requests, fetching} = state.myRequests

    useEffect(() => {
        if (shouldFetchUserRequests) {
            fetchMyRequests(requestsDispatch)
        }
    }, [requestsDispatch, shouldFetchUserRequests])

    return (
        <UserRequestsList
            requests={requests}
            cancelRequest={(requestId) => cancelRequest(requestsDispatch, {requestId})}
            isFetching={fetching}
        />
    )
};

UserRequestsListWrapper.propTypes = {
    shouldFetchUserRequests: PropTypes.bool.isRequired,
}
