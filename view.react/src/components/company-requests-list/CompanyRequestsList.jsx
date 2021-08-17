import {useRequests} from "../../contexts/request-context/requestContext";
import {UserRequestsTab} from "../user-requests-list/UserRequestsTab";

export const CompanyRequestsList = () => {
    const [state] = useRequests()
    const {requests} = state.companyRequests

    return (
        <UserRequestsTab
            requests={requests}
        />
    )
};


