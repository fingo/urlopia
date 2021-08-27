import {forwardRef, useEffect, useState} from "react";

import {getCurrentUser} from "../../api/services/session.service";
import {addPresenceConfirmation} from "../../contexts/presence-context/actions/addPresenceConfirmation";
import {fetchMyPresenceConfirmations} from "../../contexts/presence-context/actions/fetchMyPresenceConfirmations";
import {usePresence} from "../../contexts/presence-context/presenceContext";
import {formattedDate, formattedTime} from "../../helpers/DateHelper";
import {DatePicker} from "../date-picker/DatePicker";
import {TimePicker} from "../time-picker/TimePicker";
import styles from './PresenceConfirmationPanel.module.scss'

export const PresenceConfirmationPanel = () => {
    const [state, presenceDispatcher] = usePresence()
    const {confirmations} = state.myConfirmations

    const TODAY = new Date()
    const [chosenDate, setChosenDate] = useState(TODAY)
    const [chosenStartTime, setChosenStartTime] = useState(TODAY)
    const [chosenEndTime, setChosenEndTime] = useState(TODAY)

    useEffect(() => {
        fetchMyPresenceConfirmations(presenceDispatcher)
    }, [presenceDispatcher])

    const handlePresenceConfirmation = () => {
        const currentUser = getCurrentUser()
        addPresenceConfirmation(presenceDispatcher, {
            date: formattedDate(chosenDate),
            startTime: formattedTime(chosenStartTime),
            endTime: formattedTime(chosenEndTime),
            userId: currentUser.userId
        })
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

    return (
        <div className={styles.container}>
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
                {confirmations[formattedDate(chosenDate)] ? "Nadpisz obecność" : "Zgłoś obecność"}
            </button>
        </div>
    )
}