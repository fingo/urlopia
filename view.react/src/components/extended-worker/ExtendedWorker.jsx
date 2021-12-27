import PropTypes from "prop-types";
import {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";

import {useAbsenceHistory} from "../../contexts/absence-history-context/absenceHistoryContext";
import {fetchRecentUserAbsenceHistory} from "../../contexts/absence-history-context/actions/fetchUserAbsenceHistory";
import {changeSelectedUser} from "../../contexts/workers-context/actions/changeSelectedUser";
import {fetchRemainingDays} from "../../contexts/workers-context/actions/fetchRemainingDays";
import {useWorkers} from "../../contexts/workers-context/workersContext";
import {sendGetRequest} from "../../helpers/RequestHelper";
import {AbsenceHistorySection} from "./absence-history-section/AbsenceHistorySection";
import {ButtonsSection} from "./buttons-section/ButtonsSection";
import {ChangeDaysPoolAndWorkTimeSection} from "./change-days-pool-and-work-time-section/ChangeDaysPoolAndWorkTimeSection";
import styles from './ExtendedWorker.module.scss';
import {ReportsSection} from "./reports-section/ReportsSection";

const CURRENT_YEAR = new Date().getFullYear();
const GET_AVAILABLE_YEARS_URL_PREFIX = '/api/users/';
const GET_AVAILABLE_YEARS_URL_POSTFIX = '/days/employment-year';

export const ExtendedWorker = ({workTime, userId, isUnspecifiedAbsences}) => {
    const [remainingDays, setRemainingDays] = useState('');
    const [remainingHours, setRemainingHours] = useState('');

    const [availableYears, setAvailableYears] = useState([]);

    const [workersState, workersDispatch] = useWorkers();
    const {isEC} = workersState;

    const [, absenceHistoryDispatch] = useAbsenceHistory();
    const vacationTypeLabel = isEC ? "Pozostały urlop:" : "Pozostała przerwa:"

    useEffect(() => {
        sendGetRequest(`${GET_AVAILABLE_YEARS_URL_PREFIX}${userId}${GET_AVAILABLE_YEARS_URL_POSTFIX}`)
            .then(year => {
                let years = [];
                for (let i = CURRENT_YEAR; i >= year; i--) {
                    years.push(i);
                }
                setAvailableYears(years);
            }).catch(error => console.log('err: ', error));
    }, [userId]);

    useEffect(() => {
        workersDispatch(changeSelectedUser(userId));
        fetchRemainingDays(workersDispatch, userId);
    }, [workersDispatch, userId, workTime]);

    useEffect(() => {
        const {remainingDays, remainingHours} = isEC
            ? workersState.workers.remainingDaysOfCurrentSelectedWorker
            : workersState.associates.remainingDaysOfCurrentSelectedAssociate;

        setRemainingDays(remainingDays);
        setRemainingHours(remainingHours);
    }, [workersState, isEC]);

    useEffect(() => {
        fetchRecentUserAbsenceHistory(absenceHistoryDispatch, userId);
    }, [absenceHistoryDispatch, userId]);

    return (
        <Container fluid>
            <Row>
                <Col lg={5}>
                    <div className={styles.holidayDaysInfo}>
                        {
                            workTime === "1/1" ?
                                <>
                                    <h3>{vacationTypeLabel}</h3>
                                    <h3><strong>{remainingDays} dni</strong> {remainingHours} godzin</h3>
                                </>
                                :
                                <>
                                    <h3>{vacationTypeLabel}</h3>
                                    <h3><strong>{remainingHours} godzin</strong></h3>
                                </>
                        }
                    </div>

                    <ChangeDaysPoolAndWorkTimeSection workTime={workTime} />

                    <ButtonsSection isUnspecifiedAbsences={isUnspecifiedAbsences}/>
                </Col>

                <Col lg={7} className='text-center'>
                    <AbsenceHistorySection/>

                    <ReportsSection availableYears={availableYears}/>
                </Col>
            </Row>
        </Container>
    );
};

ExtendedWorker.propTypes = {
    workTime: PropTypes.string.isRequired,
    userId: PropTypes.number.isRequired,
    isUnspecifiedAbsences: PropTypes.bool,
}

ExtendedWorker.defaultProps = {
    isUnspecifiedAbsences: false,
}
