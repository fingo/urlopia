import moment from "moment";
import {forwardRef, useEffect, useState} from "react";
import {Button, Col, FormCheck, Modal, Row} from 'react-bootstrap';
import {ExclamationTriangleFill} from "react-bootstrap-icons";

import {getCurrentUser} from "../../api/services/session.service";
import {changeWorkingHoursPreferences} from "../../contexts/user-preferences-context/actions/changeWorkingHoursPreferences";
import {useUserPreferences} from "../../contexts/user-preferences-context/userPreferencesContext";
import {formattedTime} from "../../helpers/DateHelper";
import {TimePicker} from "../time-picker/TimePicker";
import styles from "./PreferencesModal.module.scss";

const getTime = (hours, minutes) => {
    const date = new Date()
    date.setHours(hours, minutes)
    return date
}

const startTimesValues = (preferences) => {
    const userPreferences = preferences.dayPreferences
    return {
        1: userPreferences ? moment(userPreferences[1].startTime, "HH:mm").toDate() : getTime(8, 0),
        2: userPreferences ? moment(userPreferences[2].startTime, "HH:mm").toDate() : getTime(8, 0),
        3: userPreferences ? moment(userPreferences[3].startTime, "HH:mm").toDate() : getTime(8, 0),
        4: userPreferences ? moment(userPreferences[4].startTime, "HH:mm").toDate() : getTime(8, 0),
        5: userPreferences ? moment(userPreferences[5].startTime, "HH:mm").toDate() : getTime(8, 0),
    }
}

const endTimesValues = (preferences) => {
    const userPreferences = preferences.dayPreferences
    return {
        1: userPreferences ? moment(userPreferences[1].endTime, "HH:mm").toDate() : getTime(16, 0),
        2: userPreferences ? moment(userPreferences[2].endTime, "HH:mm").toDate() : getTime(16, 0),
        3: userPreferences ? moment(userPreferences[3].endTime, "HH:mm").toDate() : getTime(16, 0),
        4: userPreferences ? moment(userPreferences[4].endTime, "HH:mm").toDate() : getTime(16, 0),
        5: userPreferences ? moment(userPreferences[5].endTime, "HH:mm").toDate() : getTime(16, 0),
    }
}

const nonWorkingValues = (preferences) => {
    const userPreferences = preferences.dayPreferences
    return {
        1: userPreferences ? userPreferences[1].nonWorking : false,
        2: userPreferences ? userPreferences[2].nonWorking : false,
        3: userPreferences ? userPreferences[3].nonWorking : false,
        4: userPreferences ? userPreferences[4].nonWorking : false,
        5: userPreferences ? userPreferences[5].nonWorking : false,
    }
}

const UNDEFINED_PREFERENCES = {
    dayPreferences: undefined
}

export const PreferencesModal = ({show, onHide, modalTitle, onClick}) => {
    const [preferencesState, preferencesDispatcher] = useUserPreferences()
    const {workingHours} = preferencesState

    const currentUser = getCurrentUser()
    const currentUserPreferences = workingHours.preferences[currentUser.userId] || UNDEFINED_PREFERENCES

    const [chosenStartTimes, setChosenStartTimes] = useState(() => startTimesValues(currentUserPreferences))
    const [chosenEndTimes, setChosenEndTimes] = useState(() => endTimesValues(currentUserPreferences))
    const [nonWorkingDays, setNonWorkingDays] = useState(() => nonWorkingValues(currentUserPreferences))

    useEffect(() => {
        setChosenStartTimes(() => startTimesValues(currentUserPreferences))
        setChosenEndTimes(() => endTimesValues(currentUserPreferences))
        setNonWorkingDays(() => nonWorkingValues(currentUserPreferences))
    }, [currentUserPreferences])

    const InputWrapper = ({children}) => {
        return <div className={styles.inputWrapper}>{children}</div>
    }

    const DateTimePickerInput = forwardRef(({value, onClick}, ref) => {
        return (
            <button className={styles.dateTimePickerInput} onClick={onClick} ref={ref}>
                {value}
            </button>
        )
    });

    const DayPreferences = ({day, dayOfWeek}) => {
        return (
            <Row>
                <Col>
                    <p>{day}: </p>
                </Col>
                <Col>
                    <FormCheck
                        id={dayOfWeek}
                        label={"Dzień niepracujący"}
                        checked={nonWorkingDays[dayOfWeek]}
                        onChange={e => setNonWorkingDays({
                            ...nonWorkingDays,
                            [dayOfWeek]: e.target.checked
                        })}
                    />
                </Col>
                <Col>
                    <div className={styles.timePickersContainer}>
                        <InputWrapper>
                            <TimePicker
                                customInput={<DateTimePickerInput/>}
                                chosenTime={chosenStartTimes[dayOfWeek]}
                                onChange={date => setChosenStartTimes({
                                    ...chosenStartTimes,
                                    [dayOfWeek]: date
                                })}
                            />
                        </InputWrapper>
                        <InputWrapper>
                            <div className={styles.dash}/>
                        </InputWrapper>
                        <InputWrapper>
                            <TimePicker
                                customInput={<DateTimePickerInput/>}
                                chosenTime={chosenEndTimes[dayOfWeek]}
                                onChange={date => setChosenEndTimes({
                                    ...chosenEndTimes,
                                    [dayOfWeek]: date
                                })}
                            />
                        </InputWrapper>
                    </div>
                </Col>
            </Row>
        )
    }

    return (
        <Modal
            onHide={onHide}
            show={show}
            size="lg"
            centered
        >
            <Modal.Header closeButton>
                <Modal.Title>
                    {modalTitle}
                </Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <DayPreferences day={"Poniedziałek"} dayOfWeek={1}/>
                <DayPreferences day={"Wtorek"} dayOfWeek={2}/>
                <DayPreferences day={"Środa"} dayOfWeek={3}/>
                <DayPreferences day={"Czwartek"} dayOfWeek={4}/>
                <DayPreferences day={"Piątek"} dayOfWeek={5}/>
                <div className={styles.warningLabelContainer}>
                    <ExclamationTriangleFill size={48} color={"orange"}/>
                    <div className={styles.warningLabelTextContainer}>
                        {"Wybrane godziny i dni nie mają żadnego wpływu na składane wnioski urlopowe " +
                        "oraz wyliczany na ich podstawie pozostały urlop."}
                    </div>
                </div>
            </Modal.Body>
            <Modal.Footer>
                <div className={styles.button}>
                    <Button
                        variant="primary"
                        type="submit"
                        onClick={() => {
                            let newPreferences = {}
                            for (let i = 1; i < 6; i++) {
                                newPreferences[i.toString()] = {
                                    startTime: formattedTime(chosenStartTimes[i]),
                                    endTime: formattedTime(chosenEndTimes[i]),
                                    nonWorking: nonWorkingDays[i],
                                }
                            }
                            changeWorkingHoursPreferences(preferencesDispatcher, newPreferences)
                            onClick()
                        }}
                    >
                        Zapisz zmiany
                    </Button>
                </div>
            </Modal.Footer>
        </Modal>
    );
}

PreferencesModal.propTypes = {}

PreferencesModal.defaultProps = {}