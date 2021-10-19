import {acceptAcceptance, rejectAcceptance} from "../../../contexts/request-context/actions/changeAcceptanceStatus";
import {useRequests} from "../../../contexts/request-context/requestContext";
import {requestPeriodFormatter} from "../../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {TeamRequestsList} from "../TeamRequestsList";

export const TeamRequestsListWrapper = () => {
    const [state, requestsDispatch] = useRequests()
    const {pending: pendingAcceptances} = state.acceptances

    const formattedRequests = pendingAcceptances.map(req => {
        return {
            id: req.id,
            requester: req.requesterName,
            period: requestPeriodFormatter(req),
        }
    })

    return (
        <TeamRequestsList
            requests={formattedRequests}
            acceptRequest={(acceptanceId) => acceptAcceptance(requestsDispatch, {acceptanceId})}
            rejectRequest={(acceptanceId) => rejectAcceptance(requestsDispatch, {acceptanceId})}
        />
    )
};