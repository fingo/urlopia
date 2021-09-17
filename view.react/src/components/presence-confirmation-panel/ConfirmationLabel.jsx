import styles from './ConfirmationLabel.module.scss'

export const ConfirmationLabel = ({fetching, confirmation, isOwnPresence, isOnVacation}) => {
    const label = getLabel(confirmation, isOwnPresence, isOnVacation)

    return (
        <div className={`${styles.label} ${confirmation || isOnVacation ? styles.green : styles.red}`}>
            {!fetching && label}
        </div>
    )
}

const getLabel = (confirmation, isOwnPresence, isOnVacation) => {
    if (isOnVacation) {
        if (isOwnPresence) {
            return "W tym dniu byłeś nieobecny lub na urlopie"
        }
        return "W tym dniu pracownik był nieobecny lub na urlopie"
    }

    if (confirmation) {
        const {startTime, endTime} = confirmation
        if (isOwnPresence) {
            return `W tym dniu zgłosiłeś swoją obecność w godzinach: ${startTime} - ${endTime}`
        }
        return `W tym dniu pracownik zgłosił swoją obecność w godzinach: ${startTime} - ${endTime}`
    }

    if (isOwnPresence) {
        return "W tym dniu nie zgłosiłeś swojej obecności"
    }
    return "W tym dniu pracownik nie zgłosił swojej obecności"
}