import PropTypes from "prop-types";
import {useEffect, useState} from "react";
import {Button, Form, Modal} from 'react-bootstrap';

import {useAbsenceHistory} from "../../../contexts/absence-history-context/absenceHistoryContext";
import {fetchUserAbsenceHistory} from "../../../contexts/absence-history-context/actions/fetchUserAbsenceHistory";
import {fetchHolidays} from "../../../contexts/holidays-context/actions/fetchHolidays";
import {useHolidays} from "../../../contexts/holidays-context/holidaysContext";
import {btnClass} from '../../../global-styles/btn.module.scss';
import {formatDate} from "../../../helpers/DateFormatterHelper";
import {pushSuccessNotification} from "../../../helpers/notifications/Notifications";
import {sendPostRequest} from "../../../helpers/RequestHelper";
import {Calendar} from '../../create-absence-request-form/calendar/Calendar';
import styles from './AddAbsenceForm.module.scss';

const KEY = 'selection';
const DEFAULT_REASON = 'ADDITIONAL_CARE_ALLOWANCE_PANDEMIC';
const POST_ABSENCE_URL = '/api/v2/absence-requests/special-absence'

export const AddAbsenceForm = ({show, onHide, userId, fullName}) => {
    const [, absenceHistoryDispatch] = useAbsenceHistory();

    const [holidaysState, holidaysDispatch] = useHolidays();
    const {holidays} = holidaysState;

    const [selectedRange, setSelectedRange] = useState([{
            startDate: new Date(),
            endDate: new Date(),
            key: KEY,
            color: 'deepskyblue',
        }]
    );
    const [selectedReason, setSelectedReason] = useState(DEFAULT_REASON);
    const [isReadyToSubmit, setIsReadyToSubmit] = useState(false);

    useEffect(() => {
        fetchHolidays(holidaysDispatch);
    }, [holidaysDispatch]);


    const handleSelectingRange = item => {
        setSelectedRange([item.selection]);
        setIsReadyToSubmit(true);
    }

    const handleSubmit = () => {
        const startDate = formatDate(selectedRange[0].startDate);
        const endDate = formatDate(selectedRange[0].endDate);

        sendPostRequest(POST_ABSENCE_URL, {
            requesterId: userId,
            startDate,
            endDate,
            reason: selectedReason,
        }).then(() => {
            fetchUserAbsenceHistory(absenceHistoryDispatch, userId);
            pushSuccessNotification("Pomyślnie dodano nieobecność wybranemu użytkownikowi")
            onHide();
        }).catch(error => {
            console.log('err: ', error);
        })
    }

    return (
        <Modal
            show={show}
            onHide={onHide}
            size="lg"
            aria-labelledby="contained-modal-title-vcenter"
            centered
        >
            <Modal.Header closeButton>
                <Modal.Title id="contained-modal-title-vcenter">
                    Dodaj nieobecność dla: <strong>{fullName}</strong>
                </Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form.Label><strong>Wybierz rodzaj: </strong></Form.Label>
                <Form.Select aria-label="Default select example"
                             onChange={e => setSelectedReason(e.target.value)}
                             defaultValue={DEFAULT_REASON}
                >
                    <option value='ADDITIONAL_CARE_ALLOWANCE_PANDEMIC'>
                        Dodatkowy zasiłek opiekuńczy podczas pandemii
                    </option>
                    <option value='BLOOD_DONATION_PANDEMIC'>Oddawanie krwi podczas pandemii</option>
                    <option value='BLOOD_DONATION'>Oddawanie krwi</option>
                    <option value='DELEGATION'>Delegacja</option>
                    <option value='UNPAID_LEAVE'>Urlop bezpłatny</option>
                    <option value='PARENTAL_LEAVE'>Urlop rodzicielski</option>
                    <option value='MATERNITY_LEAVE'>Urlop macierzyński</option>
                    <option value='PATERNITY_LEAVE'>Urlop ojcowski</option>
                    <option value='SICK_LEAVE_EMPLOYEE'>Zwolnienie lekarskie na pracownika</option>
                    <option value='SICK_LEAVE_CHILD'>Zwolnienie lekarskie na dziecko</option>
                    <option value='SICK_LEAVE_FAMILY'>Zwolnienie lekarskie na członka rodziny</option>
                    <option value='UNEXCUSED'>Nieobecność nieusprawiedliwiona</option>
                    <option value='EXCUSED_UNPAID'>Nieobecność usprawiedliwiona niepłatna</option>
                    <option value='CHILDCARE'>Urlop wychowawczy</option>
                    <option value='CHILDCARE_FOR_14_YEARS_OLD'>Opieka nad dzieckiem do lat 14</option>
                    <option value='OTHER'>Inny powód nieobecności</option>
                </Form.Select>

                <div className={styles.calendarWrapper}>
                    <Calendar isNormal={true}
                              selectedRange={selectedRange}
                              onChangeOption={handleSelectingRange}
                              holidays={holidays}
                    />
                </div>
            </Modal.Body>
            <Modal.Footer>
                <Button onClick={() => handleSubmit()}
                        className={btnClass}
                        disabled={!isReadyToSubmit}
                >
                    Zapisz nieobecność
                </Button>
                <Button onClick={onHide} className={btnClass}>Anuluj</Button>
            </Modal.Footer>
        </Modal>
    );
}

AddAbsenceForm.propTypes = {
    show: PropTypes.bool,
    onHide: PropTypes.func.isRequired,
    userId: PropTypes.number.isRequired,
    fullName: PropTypes.string,
}

AddAbsenceForm.defaultProps = {
    show: false,
    fullName: 'unknown',
}