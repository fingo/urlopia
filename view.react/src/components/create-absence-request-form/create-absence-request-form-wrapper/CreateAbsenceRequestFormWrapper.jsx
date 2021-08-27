import PropTypes from 'prop-types'
import {useEffect} from "react";

import {fetchHolidays} from "../../../contexts/holidays-context/actions/fetchHolidays";
import {useHolidays} from '../../../contexts/holidays-context/holidaysContext';
import {createRequest} from "../../../contexts/request-context/actions/createRequest";
import {useRequests} from "../../../contexts/request-context/requestContext";
import {CreateAbsenceRequestForm} from "../CreateAbsenceRequestForm";

export const CreateAbsenceRequestFormWrapper = ({shouldFetchHolidays}) => {
    const [holidaysState, holidaysDispatch] = useHolidays();
    const [, requestsDispatch] = useRequests();
    const {holidays} = holidaysState;

    useEffect(() => {
        if (shouldFetchHolidays) {
            fetchHolidays(holidaysDispatch);
        }
    }, [holidaysDispatch, shouldFetchHolidays]);

    return (
        <CreateAbsenceRequestForm
            createRequest={(body, isAdmin) => createRequest(requestsDispatch, body, isAdmin)}
            holidays={holidays}
        />
    )
};

CreateAbsenceRequestFormWrapper.propTypes = {
    shouldFetchHolidays: PropTypes.bool.isRequired,
}
