import {Reports} from "../../components/reports/Reports";
import styles from './ReportsPage.module.scss';

export const URL = '/reports';

export const ReportsPage = () => {
    return (
        <>
            <h1 className={styles.title}>Raporty</h1>
            <Reports/>
        </>
    );
};