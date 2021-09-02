import {useEffect} from "react";
import {useHistory, useParams} from "react-router-dom";

import {AbsenceHistoryList} from "../../components/absence-history-list/AbsenceHistoryList";
import styles from './HistoryPage.module.scss';

export const URL = '/history';

export const HistoryPage = ({isAdmin}) => {
    const params = useParams();
    const history = useHistory();

    useEffect(() => {
        if (!isAdmin) {
            history.push('/history/me');
        }
    }, [isAdmin, history]);

    return (
        <div className={styles.container}>
            <AbsenceHistoryList forWhomToFetch={params.userId}/>
        </div>
    );
};