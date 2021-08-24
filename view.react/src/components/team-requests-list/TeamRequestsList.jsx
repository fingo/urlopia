import {acceptAcceptance, rejectAcceptance} from "../../contexts/request-context/actions/changeAcceptanceStatus";
import {useRequests} from "../../contexts/request-context/requestContext";
import {requestPeriodFormatter} from "../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {TeamRequestsTab} from "./TeamRequestsTab";

export const TeamRequestsList = () => {
    const [state, requestsDispatch] = useRequests()
    const {requests} = state.teamRequests

    const formattedRequests = requests.map(req => {
        return {
            id: req.id,
            requester: req.requesterName,
            period: requestPeriodFormatter(req),
        }
    })

    return (
        <TeamRequestsTab
            requests={formattedRequests}
            acceptRequest={(acceptanceId) => acceptAcceptance(requestsDispatch, {acceptanceId})}
            rejectRequest={(acceptanceId) => rejectAcceptance(requestsDispatch, {acceptanceId})}
        />
    )
};