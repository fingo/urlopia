import {HolidaysConfig} from '../../components/holidays-config/HolidaysConfig'
import {HolidaysProvider} from "../../contexts/holidays-context/holidaysContext";
import styles from "./HolidaysPage.module.scss";

export const URL = '/holidays';

export const HolidaysPage = () => {
    return (
        <>
            <div className={styles.container}>
                <h3 className={styles.title}>Dni Świąteczne</h3>
                <HolidaysProvider>
                    <HolidaysConfig/>
                </HolidaysProvider>
            </div>
        </>
    );
};