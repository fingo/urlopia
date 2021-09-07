import styles from './ConfirmationLabel.module.scss'

export const ConfirmationLabel = ({fetching, confirmation, isOwnPresence}) => {
    const label = getLabel(confirmation, isOwnPresence)

    return (
        <div className={`${styles.label} ${confirmation ? styles.green : styles.red}`}>
            {!fetching && label}
        </div>
    )
}

const getLabel = (confirmation, isOwnPresence) => {
    if (confirmation) {
        const {startTime, endTime} = confirmation
        if (isOwnPresence) {
            return `W tym dniu zgłosiłeś swoją obecność w godzinach: ${startTime} - ${endTime}`
        }
        return `W tym dniu pracownik zgłosił swoją obecność w godzinach: ${startTime} - ${endTime}`
    }

    if (isOwnPresence) {
        return "W tym dniu nie zgłosiłeś jeszcze swojej obecności"
    }
    return "W tym dniu pracownik nie zgłosił swojej obecności"
}