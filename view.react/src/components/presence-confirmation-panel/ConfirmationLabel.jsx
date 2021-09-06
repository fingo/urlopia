import styles from './ConfirmationLabel.module.scss'

export const ConfirmationLabel = ({fetching, confirmation}) => {
    let label = "W tym dniu nie zgłosiłeś jeszcze swojej obecności"
    if (confirmation) {
        const startTime = confirmation.startTime
        const endTime = confirmation.endTime
        label = `W tym dniu zgłosiłeś swoją obecność w godzinach: ${startTime} - ${endTime}`
    }

    return (
        <div className={`${styles.label} ${confirmation ? styles.green : styles.red}`}>
            {!fetching && label}
        </div>
    )
}