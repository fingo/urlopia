import {useEffect} from "react";

import {fetchHolidays} from "../../contexts/holidays-context/actions/fetchHolidays";
import {useHolidays} from '../../contexts/holidays-context/holidaysContext';
import {createRequest} from "../../contexts/request-context/actions/createRequest";
import {useRequests} from "../../contexts/request-context/requestContext";
import {CreateAbsenceRequestForm} from "./CreateAbsenceRequestForm";

export const CreateAbsenceRequestFormWrapper = () => {
    const [holidaysState, holidaysDispatch] = useHolidays();
    const [, requestsDispatch] = useRequests();
    const {holidays} = holidaysState;

    useEffect(() => {
        fetchHolidays(holidaysDispatch);
    }, [holidaysDispatch]);

    return (
        <CreateAbsenceRequestForm
            createRequest={body => createRequest(requestsDispatch, body)}
            holidays={holidays}
        />
    )
};
