import PropTypes from "prop-types";
import {useEffect} from "react";

import {acceptRequest, rejectRequest} from "../../../contexts/request-context/actions/changeRequestStatus";
import {fetchCompanyRequests} from "../../../contexts/request-context/actions/fetchCompanyRequests";
import {useRequests} from "../../../contexts/request-context/requestContext";
import {useVacationDays} from "../../../contexts/vacation-days-context/vacationDaysContext";
import {requestPeriodFormatter} from "../../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {updateVacationDays} from "../../../helpers/updateVacationDays";
import {CompanyRequestsList} from "../CompanyRequestsList";

export const CompanyRequestsListWrapper = ({shouldFetchCompanyRequests}) => {
    const [state, requestsDispatch] = useRequests()
    const {requests, fetching} = state.companyRequests

    const [, vacationDaysDispatch] = useVacationDays();

    useEffect(() => {
        if (shouldFetchCompanyRequests) {
            fetchCompanyRequests(requestsDispatch)
        }
    }, [requestsDispatch, shouldFetchCompanyRequests])

    const formattedRequests = requests.map(req => {
        return {
            id: req.id,
            applicant: req.requesterName,
            examiner: req.acceptances.map(acc => acc.leaderName),
            period: requestPeriodFormatter(req),
        }
    })

    return (
        <CompanyRequestsList
            requests={formattedRequests}
            rejectRequest={async (requestId) => {
                await rejectRequest(requestsDispatch, {requestId});
                updateVacationDays(vacationDaysDispatch);
            }}
            acceptRequest={async (requestId) => {
                await acceptRequest(requestsDispatch, {requestId});
                updateVacationDays(vacationDaysDispatch);
            }}
            isFetching={fetching}
        />
    )
};

CompanyRequestsListWrapper.propTypes = {
    shouldFetchCompanyRequests: PropTypes.bool.isRequired,
}
