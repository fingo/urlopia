import PropTypes from "prop-types";
import {forwardRef, useEffect, useState} from "react";

import {getCurrentUser} from "../../api/services/session.service";
import {addPresenceConfirmation} from "../../contexts/presence-context/actions/addPresenceConfirmation";
import {fetchMyPresenceConfirmations} from "../../contexts/presence-context/actions/fetchMyPresenceConfirmations";
import {usePresence} from "../../contexts/presence-context/presenceContext";
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

export const PresenceConfirmationPanel = ({userId, onConfirmation}) => {
    const ownPresence = userId === undefined
    const currentUser = getCurrentUser()

    const [presenceState, presenceDispatcher] = usePresence()
    const {confirmations: myConfirmations} = presenceState.myConfirmations
    const {confirmations: usersConfirmations} = presenceState.usersConfirmations
    const {fetching} = ownPresence ? presenceState.myConfirmations : presenceState.usersConfirmations

    const [usersVacationsState, usersVacationsDispatcher] = useUsersVacations()
    const {usersVacations} = usersVacationsState

    const TODAY = new Date();
    const [chosenDate, setChosenDate] = useState(TODAY)
    const [chosenStartTime, setChosenStartTime] = useState(getTime(8, 0))
    const [chosenEndTime, setChosenEndTime] = useState(getTime(16, 0))

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
        <div className={styles.mainContainer}>
            <div className={styles.flexContainer}>
                <InputWrapper>
                    Data
                    <DatePicker
                        withWeekend={false}
                        customInput={<DateTimePickerInput />}
                        chosenDate={chosenDate}
                        onChange={date => setChosenDate(date)}
                    />
                </InputWrapper>
                <div className={styles.timePickersContainer}>
                    <InputWrapper>
                        Godziny pracy
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
                        <LineBreak/>
                        <TimePicker
                            customInput={<DateTimePickerInput />}
                            chosenTime={chosenEndTime}
                            onChange={date => setChosenEndTime(date)}
                        />
                    </InputWrapper>
                </div>
                <button
                    className={styles.presenceConfirmationButton}
                    onClick={() => handlePresenceConfirmation()}
                >
                    {getConfirmation(chosenDate) ? "Zaktualizuj godziny obecności" : "Zgłoś obecność"}
                </button>
            </div>
            <ConfirmationLabel
                fetching={fetching}
                confirmation={getConfirmation(chosenDate)}
                isOwnPresence={ownPresence}
                isOnVacation={isUserOnVacation()}
            />
        </div>
    )
}

PresenceConfirmationPanel.propTypes = {
    userId: PropTypes.number,
    onConfirmation: PropTypes.func,
}

PresenceConfirmationPanel.defaultProps = {
    onConfirmation: () => {},
}
