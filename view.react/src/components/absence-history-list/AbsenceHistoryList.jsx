import PropTypes from "prop-types";
import {useEffect, useState} from "react";
import {Dropdown, DropdownButton} from "react-bootstrap";
import {useLocation} from "react-router-dom";

import {useAbsenceHistory} from "../../contexts/absence-history-context/absenceHistoryContext";
import {fetchAbsenceHistory} from "../../contexts/absence-history-context/actions/fetchAbsenceHistory";
import styles from '../../global-styles/AbsenceHistoryList.module.scss';
import {formatLogs} from "../../helpers/AbsenceHistoryFormatterHelper";
import {AbsenceHistoryTab} from "./AbsenceHistoryTab";

export const AbsenceHistoryList = ({forWhomToFetch}) => {
    const [state, absenceHistoryDispatch] = useAbsenceHistory()
    const {absenceHistory} = state;

    const location = useLocation();

    const FIRST_AVAILABLE_YEAR = 2016;
    const currentYear = (new Date()).getFullYear()
    const [selectedYear, setSelectedYear] = useState(currentYear);
    const [availableYears, setAvailableYears] = useState([]);

    useEffect(() => {
        getAvailableYears();
        fetchAbsenceHistory(absenceHistoryDispatch, forWhomToFetch, selectedYear);
    }, [absenceHistoryDispatch, selectedYear, forWhomToFetch]);

    const getAvailableYears = () => {
        const startYear = FIRST_AVAILABLE_YEAR;
        const years = [];
        for (let i = currentYear; i >= startYear; i--) {
            years.push(i);
        }
        setAvailableYears(years);
    };

    const handleYearChange = (newYear) => {
        setSelectedYear(newYear);
    }

    const formattedLog = formatLogs(absenceHistory);

    let header = 'Historia użytkownika';
    if (location.state?.fullName && location.pathname !== '/history/me') {
        header = header.concat(` - ${location.state.fullName}`);
    }
    return (
        <>
            <div className={styles.panelFooter}>
                <h3>{header}</h3>
                <div>
                    <DropdownButton id="dropdown-basic-button"
                                    title={selectedYear}
                                    size="sm"
                                    bsPrefix={styles.datesDropdown}
                                    onSelect={year => handleYearChange(year)}>
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
            <AbsenceHistoryTab logs={formattedLog} />
        </>
    );
}

AbsenceHistoryList.propTypes = {
    forWhomToFetch: PropTypes.number.isRequired,
}
