import {AbsenceHistoryList} from "../../components/absence-history-list/AbsenceHistoryList";
import {AbsenceHistoryProvider} from "../../contexts/absence-history-context/absenceHistoryContext";
import styles from './HistoryPage.module.scss';

export const URL = '/history';

export const HistoryPage = () => {
    return (
        <AbsenceHistoryProvider>
            <div className={styles.container}>
                <AbsenceHistoryList/>
            </div>
        </AbsenceHistoryProvider>
    );
};