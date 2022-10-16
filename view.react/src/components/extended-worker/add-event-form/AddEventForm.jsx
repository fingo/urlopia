import PropTypes from "prop-types";
import {useEffect, useState} from "react";
import {Button, Form, Modal} from 'react-bootstrap';

import {useAbsenceHistory} from "../../../contexts/absence-history-context/absenceHistoryContext";
import {fetchRecentUserAbsenceHistory} from "../../../contexts/absence-history-context/actions/fetchUserAbsenceHistory";
import {fetchHolidays} from "../../../contexts/holidays-context/actions/fetchHolidays";
import {useHolidays} from "../../../contexts/holidays-context/holidaysContext";
import {btnClass} from '../../../global-styles/btn.module.scss';
import {formatDate} from "../../../helpers/DateFormatterHelper";
import {pushSuccessNotification} from "../../../helpers/notifications/Notifications";
import {sendPostRequest} from "../../../helpers/RequestHelper";
import {Calendar} from '../../create-absence-request-form/calendar/Calendar';
import styles from './AddEventForm.module.scss';

const KEY = 'selection';
const DEFAULT_REASON = 'USER_ACTIVATED';
const POST_EVENT_URL = '/api/v2/absence-history/details-change'

export const AddEventForm = ({show, onHide, userId, fullName}) => {
    const [, absenceHistoryDispatch] = useAbsenceHistory();

    const [, holidaysDispatch] = useHolidays();

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


    const handleSelectingOneDay = item => {
        const newState = item.selection;
        newState.endDate = newState.startDate;
        setSelectedRange([newState]);
        setIsReadyToSubmit(true);
    }

    const handleSubmit = () => {
        const startDate = formatDate(selectedRange[0].startDate);

        sendPostRequest(POST_EVENT_URL, {
            created: `${startDate} 00:02:00`,
            userId,
            event: selectedReason
        }).then(() => {
            fetchRecentUserAbsenceHistory(absenceHistoryDispatch, userId);
            pushSuccessNotification("Pomyślnie dodano wydarzenie wybranemu użytkownikowi")
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
                    Dodaj wydarzenie dla: <strong>{fullName}</strong>
                </Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form.Label><strong>Wybierz rodzaj: </strong></Form.Label>
                <Form.Select aria-label="Default select example"
                             onChange={e => setSelectedReason(e.target.value)}
                             defaultValue={DEFAULT_REASON}
                >
                    <option value='USER_ACTIVATED'>Użytkownik został aktywowany</option>
                    <option value='USER_DEACTIVATED'>Użytkownik został dezaktywowany</option>
                    <option value='USER_CHANGE_TO_EC'>Użytkownik zmienił rodzaj umowy na: pracownik</option>
                    <option value='USER_CHANGE_TO_B2B'>Użytkownik zmienił rodzaj umowy na: współpracownik</option>
                </Form.Select>

                <div className={styles.calendarWrapper}>
                    <Calendar isNormal={true}
                              selectedRange={selectedRange}
                              onChangeOption={handleSelectingOneDay}
                              withWeekends={true}
                    />
                </div>
            </Modal.Body>
            <Modal.Footer>
                <Button onClick={() => handleSubmit()}
                        className={btnClass}
                        disabled={!isReadyToSubmit}
                >
                    Zapisz wydarzenie
                </Button>
                <Button onClick={onHide} className={btnClass}>Anuluj</Button>
            </Modal.Footer>
        </Modal>
    );
}

AddEventForm.propTypes = {
    show: PropTypes.bool,
    onHide: PropTypes.func.isRequired,
    userId: PropTypes.number.isRequired,
    fullName: PropTypes.string,
}

AddEventForm.defaultProps = {
    show: false,
    fullName: 'unknown',
}