import {useEffect, useState} from "react";
import {Dropdown,DropdownButton} from "react-bootstrap";

import {useAbsenceHistory} from "../../contexts/absence-history-context/absenceHistoryContext";
import {fetchMyAbsenceHistory} from "../../contexts/absence-history-context/actions/fetchMyAbsenceHistory";
import styles from './AbsenceHistoryList.module.scss';
import {AbsenceHistoryTab} from "./AbsenceHistoryTab";


export const AbsenceHistoryList = () => {

    const [state, absenceHistoryDispatch] = useAbsenceHistory()
    const {absenceHistory} = state.myAbsenceHistory
    const isHidden = false;

    const FIRST_AVAILABLE_YEAR = 2016;
    const currentYear = (new Date()).getFullYear()
    const [selectedYear, setSelectedYear] = useState(currentYear);
    const [availableYears, setAvailableYears] = useState([]);

    useEffect(() => {
        getAvailableYears()
        fetchMyAbsenceHistory(absenceHistoryDispatch, selectedYear)
    }, [absenceHistoryDispatch]);

    const getAvailableYears = () => {
        const startYear = FIRST_AVAILABLE_YEAR;
        const years = [];
        for (let i = currentYear; i >= startYear; i--) {
            years.push(i);
        }
        setAvailableYears(years);
    };

    const handleYearChange = (e) => {
        fetchMyAbsenceHistory(absenceHistoryDispatch, e);
        setSelectedYear(e)
    }

    return (
        <>
            <div className={styles.panelFooter}>
                <h3>Historia nieobecności</h3>
                <div>
                    <DropdownButton id="dropdown-basic-button"
                                    title={selectedYear}
                                    size="sm"
                                    bsPrefix={styles.datesDropdown}
                                    onSelect={e => handleYearChange(e)}>
                        {availableYears.map((val, index) => (
                            <Dropdown.Item className={styles.dropItem}
                                           key={index}
                                           eventKey={val}
                            >
                                {val}
                            </Dropdown.Item>
                        ))}
                    </DropdownButton>
                </div>
            </div>
            <AbsenceHistoryTab logs={absenceHistory} isHidden={isHidden}/>
        </>
    );
}