import {AcceptanceHistoryList} from "../../components/acceptance-history-list/AcceptanceHistoryList";
import styles from "./AcceptanceHistoryPage.module.scss"

export const URL = "/acceptances/history"

export const AcceptanceHistoryPage = () => {
    return (
        <div className={styles.container}>
            <AcceptanceHistoryList />
        </div>
    )
}