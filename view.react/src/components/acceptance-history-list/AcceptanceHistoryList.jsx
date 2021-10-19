import {useCallback, useEffect, useState} from "react";
import {Dropdown, DropdownButton} from "react-bootstrap";

import {fetchAcceptancesHistory} from "../../contexts/request-context/actions/fetchAcceptancesHistory";
import {useRequests} from "../../contexts/request-context/requestContext";
import styles from "../../global-styles/AbsenceHistoryList.module.scss";
import {AcceptanceHistoryTab} from "./AcceptanceHistoryTab";

export const AcceptanceHistoryList = () => {
    const header = 'Historia akceptacji';
    const FIRST_AVAILABLE_YEAR = 2016;
    const currentYear = (new Date()).getFullYear()

    const [requestsState, requestsDispatch] = useRequests()
    const {history: acceptancesHistory} = requestsState.acceptances

    const [selectedYear, setSelectedYear] = useState(currentYear);
    const [availableYears, setAvailableYears] = useState([]);

    const getAvailableYears = useCallback(() => {
        const startYear = FIRST_AVAILABLE_YEAR;
        const years = [];
        for (let i = currentYear; i >= startYear; i--) {
            years.push(i);
        }
        setAvailableYears(years);
    }, [currentYear]);

    useEffect(() => {
        fetchAcceptancesHistory(requestsDispatch, selectedYear)
        getAvailableYears();
    }, [getAvailableYears, requestsDispatch, selectedYear]);

    const handleYearChange = (newYear) => {
        setSelectedYear(newYear);
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
            <AcceptanceHistoryTab acceptances={acceptancesHistory}/>
        </>
    );
}