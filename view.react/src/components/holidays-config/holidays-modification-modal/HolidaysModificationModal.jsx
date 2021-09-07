import PropTypes from "prop-types";
import {forwardRef, useState} from 'react';
import {FormControl, FormLabel, Modal} from 'react-bootstrap';

import {DatePicker} from "../../date-picker/DatePicker";
import {DecisionButtonsPair} from "../decision-buttons-pair/DecisionButtonsPair";
import styles from "./HolidaysModificatonModal.module.scss";

export const HolidaysModificationModal = ({show, onHide, handleAccept, modalTitle, holidayName, displayDate, year}) => {

    const firstDay = new Date(year, 0, 1);
    const lastDay = new Date(year, 11, 31);

    const [name, setName] = useState(holidayName);
    const [chosenDate, setChosenDate] = useState(displayDate);

    const DateTimePickerInput = forwardRef(({ value, onClick }, ref) => {
        return (
            <button className={styles.inputStyling} onClick={onClick} ref={ref}>
                {value}
            </button>
        )
    });

    return (
        <Modal
            onHide={onHide}
            show={show}
            size="sm"
            centered
        >
            <Modal.Header closeButton>
                <Modal.Title>
                    {modalTitle}
                </Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div className={styles.inputContainer}>
                    <FormLabel>Data</FormLabel>
                    <DatePicker
                        withWeekend={false}
                        customInput={<DateTimePickerInput/>}
                        chosenDate={chosenDate}
                        onChange={date => setChosenDate(date)}
                        maxDate={lastDay}
                    />
                    <br/>
                    <FormLabel>Nazwa święta</FormLabel>
                    <FormControl
                        value={name}
                        onChange={event => setName(event.target.value)}
                        placeholder="Nazwa święta"
                        className={styles.inputStyling}
                    />
                </div>
            </Modal.Body>
            <Modal.Footer>
                <DecisionButtonsPair
                    onReject={onHide}
                    onAccept={() => handleAccept(name, chosenDate, firstDay, lastDay)}/>
            </Modal.Footer>
        </Modal>
    );
}

HolidaysModificationModal.propTypes = {
    show: PropTypes.func.isRequired,
    onHide: PropTypes.func.isRequired,
    handleAccept: PropTypes.func.isRequired,
    modalTitle: PropTypes.string,
    holidayName: PropTypes.string,
    displayDate: PropTypes.instanceOf(Date),
    year: PropTypes.number
}

HolidaysModificationModal.defaultProps = {
    year: (new Date()).getFullYear(),
    modalTitle: '',
    holidayName: 'Nowe święto',
    displayDate: new Date(),
}