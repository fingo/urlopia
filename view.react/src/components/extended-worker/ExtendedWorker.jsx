import PropTypes from "prop-types";
import {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";

import {useAbsenceHistory} from "../../contexts/absence-history-context/absenceHistoryContext";
import {fetchUserRecentAbsenceHistory} from "../../contexts/absence-history-context/actions/fetchUserRecentAbsenceHistory";
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

export const ExtendedWorker = ({workTime, userId}) => {
    const [remainingDays, setRemainingDays] = useState('');
    const [remainingHours, setRemainingHours] = useState('');

    const [availableYears, setAvailableYears] = useState([]);

    const [workersState, workersDispatch] = useWorkers();
    const {isEC} = workersState;

    const [, absenceHistoryDispatch] = useAbsenceHistory();

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
    }, [workersDispatch, userId]);

    useEffect(() => {
        const {remainingDays, remainingHours} = isEC
            ? workersState.workers.remainingDaysOfCurrentSelectedWorker
            : workersState.associates.remainingDaysOfCurrentSelectedAssociate;

        setRemainingDays(remainingDays);
        setRemainingHours(remainingHours);
    }, [workersState, isEC]);

    useEffect(() => {
        fetchUserRecentAbsenceHistory(absenceHistoryDispatch, userId);
    }, [absenceHistoryDispatch, userId]);

    return (
        <Container fluid>
            <Row>
                <Col lg={5}>
                    <div className={styles.holidayDaysInfo}>
                        <h3>Pozostały urlop:</h3>
                        <h3><strong>{remainingDays} dni</strong> {remainingHours} godzin</h3>
                    </div>

                    <ChangeDaysPoolAndWorkTimeSection workTime={workTime} />

                    <ButtonsSection/>
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
}