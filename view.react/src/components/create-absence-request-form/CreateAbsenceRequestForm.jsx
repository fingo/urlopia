import classNames from "classnames";
import {addDays, isWeekend} from 'date-fns';
import PropTypes from "prop-types";
import {useEffect, useRef, useState} from 'react';
import {Button, Col, Container, Form, Row} from "react-bootstrap";

import {getCurrentUser} from "../../api/services/session.service";
import {
    D1_FUNERAL,
    D1_WEDDING,
    D2_BIRTH,
    D2_FUNERAL,
    D2_WEDDING,
    NORMAL,
    OCCASIONAL,
    PLACEHOLDER
} from "../../constants/requestsTypes";
import {useVacationDays} from "../../contexts/vacation-days-context/vacationDaysContext";
import {countWorkingDays} from "../../helpers/CountWorkingDaysHelper";
import {formatDate} from "../../helpers/DateFormatterHelper";
import {isHoliday} from "../../helpers/IsHolidayHelper";
import {occasionalTypeInfoMapperHelper} from "../../helpers/OccasionalTypeInfoMapperHelper";
import {formatHoursToDays} from "../../helpers/RemainingDaysFormatterHelper";
import {updateVacationDays} from "../../helpers/updateVacationDays";
import {Calendar} from "./calendar/Calendar";
import styles from './CreateAbsenceRequestForm.module.scss';
import {InfoOverlay} from "./info-overlay/InfoOverlay";

const KEY = 'selection';
const ONE_DAY = 1;
const TWO_DAYS = 2;

export const CreateAbsenceRequestForm = ({
    createRequest,
    holidays,
}) => {
    const [selectedRange, setSelectedRange] = useState([{
            startDate: new Date(),
            endDate: new Date(),
            key: KEY,
            color: 'deepskyblue',
        }]
    );
    const {ec: isUserEC} = getCurrentUser();
    const [workingDaysCounter, setWorkingDaysCounter] = useState(0);
    const [onChangeOption, setOnChangeOption] = useState(null);
    const [isNormal, setIsNormal] = useState(true);
    const [isSelected, setIsSelected] = useState(false);
    const [type, setType] = useState(null);
    const [occasionalType, setOccasionalType] = useState(null);
    const formRef = useRef(null);
    const [isReadyToSubmit, setIsReadyToSubmit] = useState(false);

    const [vacationDays, setVacationDays] = useState(0);
    const [vacationHours, setVacationHours] = useState(0);
    const [pendingDays, setPendingDays] = useState(0);
    const [pendingHours, setPendingHours] = useState(0);
    const [workTime, setWorkTime] = useState(8);

    const [vacationDaysState, vacationDaysDispatch] = useVacationDays();

    useEffect(() => {
        const {pendingDays, pendingHours} = vacationDaysState.pendingDays;
        setPendingDays(pendingDays);
        setPendingHours(pendingHours);
    }, [vacationDaysState.pendingDays]);

    useEffect(() => {
        const {remainingDays, remainingHours, workTime} = vacationDaysState.vacationDays;
        setVacationDays(remainingDays);
        setVacationHours(remainingHours);
        setWorkTime(workTime);
    }, [vacationDaysState.vacationDays]);

    const handleRequestsTypeChange = e => {
        setInitialSelectedRange();
        const {value} = e.target;
        setType(value);
        if (value === NORMAL) {
            formRef.current.reset();
            setOccasionalType(null);
            setOnChangeOption(() => handleSelectingRange);
            setIsNormal(true);
            setIsSelected(true);
        } else {
            setIsSelected(false);
        }
    }

    const handleOccasionalTypeChange = e => {
        setInitialSelectedRange();
        const {value} = e.target;
        setOccasionalType(value);
        setIsNormal(false);
        let substr = value.substring(0, value.indexOf('_'));
        switch (substr) {
            case 'D1':
                setOnChangeOption(() => handleSelectingOneDay);
                break;
            case 'D2':
                setOnChangeOption(() => handleSelectingTwoDays);
                break;
            default:
                throw new Error(`Unsupported request type: ${value}`);
        }

        setIsSelected(true);
    }

    const handleSelectingRange = item => {
        setSelectedRange([item.selection]);
        const {startDate, endDate} = item.selection;
        setWorkingDaysCounter(countWorkingDays(startDate, endDate, holidays));
        setIsReadyToSubmit(true);
    }

    const handleSelectingTwoDays = item => {
        const newState = item.selection;
        let foundEndDate = false;
        let currentDate = addDays(newState.startDate, ONE_DAY);
        while (!foundEndDate) {
            if (isHoliday(currentDate, holidays) || isWeekend(currentDate)) {
                currentDate = addDays(currentDate, ONE_DAY);
            } else {
                newState.endDate = currentDate;
                foundEndDate = true;
            }
        }
        setSelectedRange([newState]);
        setWorkingDaysCounter(TWO_DAYS);
        setIsReadyToSubmit(true);
    }

    const handleSelectingOneDay = item => {
        const newState = item.selection;
        newState.endDate = newState.startDate;
        setSelectedRange([newState]);
        setWorkingDaysCounter(ONE_DAY);
        setIsReadyToSubmit(true);
    }

    const handleSendRequest = async e => {
        const startDate = formatDate(selectedRange[0].startDate);
        const endDate = formatDate(selectedRange[0].endDate);
        const body = {
            startDate,
            endDate,
            type,
            occasionalType,
        }

        const isAdmin = getCurrentUser().isAdmin;
        await createRequest(body, isAdmin);
        updateVacationDays(vacationDaysDispatch);
    }

    const setInitialSelectedRange = () => {
        const newState = selectedRange[0];
        newState.startDate = new Date();
        newState.endDate = new Date();
        setSelectedRange([newState]);
        setIsReadyToSubmit(false);
    }
    const vacationTypeLabel = isUserEC ? "Pozostały urlop: " : "Pozostała przerwa: "

    const remainingDays = vacationDays-pendingDays
    const remainingHours = vacationHours - pendingHours;
    const remainingHoursAsDays = formatHoursToDays(remainingHours/workTime)
    const pendingHoursAsDays  = formatHoursToDays(pendingHours/workTime)

    const calendarClass = classNames(styles.calendar, {[styles.blur]: !isSelected});
    return (
        <Container fluid className={styles.container}>
            <Row>
                <Col xs={12} xl={4}>
                    <Form>
                        <div className={styles.labelAndInfo}>
                            <Form.Label>
                                Typ wniosku:
                            </Form.Label>
                        </div>
                        <div className='d-flex'>
                            <Form.Select defaultValue={PLACEHOLDER}
                                         className={styles.formSelect}
                                         onChange={e => handleRequestsTypeChange(e)}
                                         data-testid='selector'
                            >
                                <option value={PLACEHOLDER} hidden>Wybierz typ wniosku...</option>
                                <option value={NORMAL} data-testid='select-option'>Wypoczynkowy</option>
                                <option value={OCCASIONAL} data-testid='select-option'>Okolicznościowy</option>
                            </Form.Select>
                            <div className='invisible'>
                                <InfoOverlay/>
                            </div>
                        </div>
                    </Form>
                    <Form ref={formRef} className={type === OCCASIONAL ? "d-flex" : "d-none"}>
                        <Form.Select defaultValue={PLACEHOLDER}
                                     className={styles.formSelect}
                                     onChange={e => handleOccasionalTypeChange(e)}
                                     data-testid='selector'
                        >
                            <option value={PLACEHOLDER} hidden>Wybierz okoliczność...</option>
                            <optgroup label={"2-dniowe"}>
                                <option value={D2_BIRTH}>Narodziny dziecka</option>
                                <option value={D2_FUNERAL}>Pogrzeb</option>
                                <option value={D2_WEDDING}>Ślub własny</option>
                            </optgroup>
                            <optgroup label={"1-dniowe"}>
                                <option value={D1_FUNERAL}>Pogrzeb</option>
                                <option value={D1_WEDDING}>Ślub dziecka</option>
                            </optgroup>
                        </Form.Select>
                        <div>
                            <InfoOverlay/>
                        </div>
                    </Form>

                    <p className={styles.additionalInfo}>{occasionalTypeInfoMapperHelper(occasionalType)}</p>

                    <h5>Liczba dni roboczych: <strong>{workingDaysCounter}</strong></h5>
                    {
                        workTime === 8 ?
                            <>
                                <h5>{vacationTypeLabel}<strong>{remainingDays} dni</strong> {remainingHours} godzin</h5>
                                <h5>Złożone wnioski: <strong>{pendingDays} dni</strong> {pendingHours} godzin</h5>
                            </>
                        :
                            <>
                                <h5>{vacationTypeLabel}<strong>{remainingHours} godzin ({remainingHoursAsDays} d) </strong></h5>
                                <h5>Złożone wnioski: <strong>{pendingHours} godzin ({pendingHoursAsDays} d)</strong></h5>
                            </>
                    }
                </Col>

                <Col xs={12} xl={8} className={styles.calendarColumn}>
                    {
                        !isSelected &&
                        <div className={styles.calendarOverlay} data-testid='overlay'>
                            <h1 data-testid='infoHeader'>Najpierw wybierz typ wniosku!</h1>
                        </div>
                    }

                    <div className={styles.calendarWrapper}>
                        <Calendar isNormal={isNormal}
                                  calendarClass={calendarClass}
                                  onChangeOption={onChangeOption}
                                  selectedRange={selectedRange}
                                  holidays={holidays}
                        />
                    </div>

                    <Button className={styles.btn}
                            onClick={e => handleSendRequest(e)}
                            disabled={!isReadyToSubmit}
                    >
                        Złóż wniosek
                    </Button>
                </Col>
            </Row>
        </Container>
    );
};

CreateAbsenceRequestForm.propTypes = {
    createRequest: PropTypes.func.isRequired,
    holidays: PropTypes.arrayOf(
        PropTypes.shape({
            id: PropTypes.number,
            name: PropTypes.string,
            date: PropTypes.string,
        })),
}

CreateAbsenceRequestForm.defaultProps = {
    holidays: [],
}