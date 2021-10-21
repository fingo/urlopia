import {useEffect, useState} from "react";
import {useLocation, useParams} from "react-router-dom";

import {AbsenceHistoryList} from "../../components/absence-history-list/AbsenceHistoryList";
import {fetchAbsenceHistory} from "../../contexts/absence-history-context/actions/fetchAbsenceHistory";
import {fetchUserAbsenceHistory} from "../../contexts/absence-history-context/actions/fetchUserAbsenceHistory";
import styles from './HistoryPage.module.scss';

export const URL = '/history';

export const HistoryPage = ({isAdmin}) => {
    const params = useParams();
    const location = useLocation();

    const [pageNumber, setPageNumber] = useState(0)
    const [fetchHistoryAction, setFetchHistoryAction] = useState(() => () => {})

    useEffect(() => {
        setFetchHistoryAction(() => {
            if (isAdmin && location.pathname !== '/history/me') {
                return (dispatch) => fetchUserAbsenceHistory(dispatch, params.userId, pageNumber, false)
            }
            return (dispatch, {selectedYear}) => fetchAbsenceHistory(dispatch, selectedYear, pageNumber)
        })
    }, [isAdmin, params.userId, location.pathname, pageNumber])

    return (
        <div className={styles.container}>
            <AbsenceHistoryList
                fetchHistoryLogs={fetchHistoryAction}
                setPageNumber={setPageNumber}
            />
        </div>
    );
};