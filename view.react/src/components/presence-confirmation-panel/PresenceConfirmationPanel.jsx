import moment from "moment";
import PropTypes from "prop-types";
import {forwardRef, useEffect, useState} from "react";
import {Col, Row} from 'react-bootstrap';

import {getCurrentUser} from "../../api/services/session.service";
import {addPresenceConfirmation} from "../../contexts/presence-context/actions/addPresenceConfirmation";
import {fetchMyPresenceConfirmations} from "../../contexts/presence-context/actions/fetchMyPresenceConfirmations";
import {usePresence} from "../../contexts/presence-context/presenceContext";
import {useUserPreferences} from "../../contexts/user-preferences-context/userPreferencesContext";
import {fetchUsersVacations} from "../../contexts/users-vacations-context/actions/fetchUsersVacations";
import {useUsersVacations} from "../../contexts/users-vacations-context/usersVacationsContext";
import {formattedDate, formattedTime} from "../../helpers/DateHelper";
import {DatePicker} from "../date-picker/DatePicker";
import {TimePicker} from "../time-picker/TimePicker";
import {ConfirmationLabel} from "./ConfirmationLabel";
import styles from './PresenceConfirmationPanel.module.scss'

const getTime = (hours, minutes) => {
    const date = new Date()
    date.setHours(hours, minutes)
    return date
}

const getPreference = (dayPreferences, chosenDate, produceValue, produceDefaultValue) => {
    const dayOfWeek = chosenDate.getDay()
    return dayPreferences && dayPreferences[dayOfWeek] ? produceValue(dayOfWeek) : produceDefaultValue()
}

const getStartTimeValue = (preferences, chosenDate) => {
    const dayPreferences = preferences.dayPreferences
    const produceValue = dayOfWeek => moment(dayPreferences[dayOfWeek].startTime, "HH:mm").toDate()
    const produceDefaultValue = () => getTime(8, 0)

    return getPreference(dayPreferences, chosenDate, produceValue, produceDefaultValue)
}

const getEndTimeValue = (preferences, chosenDate) => {
    const dayPreferences = preferences.dayPreferences
    const produceValue = dayOfWeek => moment(dayPreferences[dayOfWeek].endTime, "HH:mm").toDate()
    const produceDefaultValue = () => getTime(16, 0)

    return getPreference(dayPreferences, chosenDate, produceValue, produceDefaultValue)
}

const isNotWorking = (preferences, chosenDate) => {
    const dayPreferences = preferences.dayPreferences
    const produceValue = dayOfWeek => dayPreferences[dayOfWeek].nonWorking
    const produceDefaultValue = () => false

    const isNonWorking = getPreference(dayPreferences, chosenDate, produceValue, produceDefaultValue)
    const preferenceChangeDate = moment(preferences.changeDate, "YYYY-MM-DD").toDate()
    return isNonWorking && preferenceChangeDate < chosenDate
}

const UNDEFINED_PREFERENCES = {
    dayPreferences: undefined
}

export const PresenceConfirmationPanel = ({userId, onConfirmation}) => {
    const ownPresence = userId === undefined
    const currentUser = getCurrentUser()

    const [presenceState, presenceDispatcher] = usePresence()
    const {confirmations: myConfirmations} = presenceState.myConfirmations
    const {confirmations: usersConfirmations} = presenceState.usersConfirmations
    const {fetching} = ownPresence ? presenceState.myConfirmations : presenceState.usersConfirmations

    const [usersVacationsState, usersVacationsDispatcher] = useUsersVacations()
    const {usersVacations} = usersVacationsState

    const [preferencesState,] = useUserPreferences()
    const {workingHours} = preferencesState
    const userPreferences = workingHours.preferences[ownPresence ? currentUser.userId : userId] || UNDEFINED_PREFERENCES

    const TODAY = new Date();
    const [chosenDate, setChosenDate] = useState(TODAY)
    const [chosenStartTime, setChosenStartTime] = useState(() => getStartTimeValue(userPreferences, chosenDate))
    const [chosenEndTime, setChosenEndTime] = useState(() => getEndTimeValue(userPreferences, chosenDate))
    
    useEffect(() => {
        setChosenStartTime(() => getStartTimeValue(userPreferences, chosenDate))
        setChosenEndTime(() => getEndTimeValue(userPreferences, chosenDate))
    }, [chosenDate, userPreferences])

    useEffect(() => {
        fetchMyPresenceConfirmations(presenceDispatcher)
        fetchUsersVacations(usersVacationsDispatcher, {
            userId: ownPresence ? currentUser.userId : userId
        })
    }, [presenceDispatcher, usersVacationsDispatcher, ownPresence, currentUser.userId, userId])

    const handlePresenceConfirmation = () => {
        addPresenceConfirmation(presenceDispatcher, {
            date: formattedDate(chosenDate),
            startTime: formattedTime(chosenStartTime),
            endTime: formattedTime(chosenEndTime),
            userId: ownPresence ? currentUser.userId : userId
        })
        onConfirmation(formattedDate(chosenDate));
    }

    const DateTimePickerInput = forwardRef(({ value, onClick }, ref) => {
        return (
            <button className={styles.dateTimePickerInput} onClick={onClick} ref={ref}>
                {value}
            </button>
        )
    });

    const InputWrapper = ({children}) => {
        return <div className={styles.inputWrapper}>{children}</div>
    }

    const LineBreak = () => <div className={styles.lineBreak}/>

    const getConfirmation = (date) => {
        if (ownPresence) {
            return myConfirmations[formattedDate(date)]
        }
        return usersConfirmations[userId] && usersConfirmations[userId][formattedDate(date)]
    }

    const isUserOnVacation = () => {
        const formatted = formattedDate(chosenDate)
        const id = ownPresence ? currentUser.userId : userId
        return usersVacations[formatted] && usersVacations[formatted].includes(id) !== undefined
    }

    return (
        <Row>
            <Col xs={{ span: 7, offset: 5 }}>
                <div className={styles.mainContainer}>
                    <Col xs={10} lg={5} className={styles.mainContentContainer}>
                        <div className={styles.header}>Rejestracja godzin obecności</div>
                        <div className={styles.flexContainer}>
                            <InputWrapper>
                                <DatePicker
                                    withWeekend={false}
                                    customInput={<DateTimePickerInput />}
                                    chosenDate={chosenDate}
                                    onChange={date => setChosenDate(date)}
                                />
                            </InputWrapper>
                            <div className={styles.timePickersContainer}>
                                <InputWrapper>
                                    <TimePicker
                                        customInput={<DateTimePickerInput />}
                                        chosenTime={chosenStartTime}
                                        onChange={date => setChosenStartTime(date)}
                                    />
                                </InputWrapper>
                                <InputWrapper>
                                    <LineBreak/>
                                    <div className={styles.dash} />
                                </InputWrapper>
                                <InputWrapper>
                                    <TimePicker
                                        customInput={<DateTimePickerInput />}
                                        chosenTime={chosenEndTime}
                                        onChange={date => setChosenEndTime(date)}
                                    />
                                </InputWrapper>
                            </div>
                        </div>
                        <ConfirmationLabel
                            fetching={fetching}
                            confirmation={getConfirmation(chosenDate)}
                            isOwnPresence={ownPresence}
                            isOnVacation={isUserOnVacation()}
                            isNotWorking={isNotWorking(userPreferences, chosenDate)}
                        />
                        <button
                                className={styles.presenceConfirmationButton}
                                onClick={() => handlePresenceConfirmation()}
                            >
                                {getConfirmation(chosenDate) ? "zaktualizuj godziny obecności" : "zgłoś obecność"}
                            </button>
                    </Col>
                </div>
            </Col>
        </Row>
    )
}

PresenceConfirmationPanel.propTypes = {
    userId: PropTypes.number,
    onConfirmation: PropTypes.func,
}

PresenceConfirmationPanel.defaultProps = {
    onConfirmation: () => {},
}
