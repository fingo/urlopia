import {useCallback, useEffect, useState} from "react";
import {Dropdown, DropdownButton} from "react-bootstrap";

import {fetchAcceptancesHistory} from "../../contexts/request-context/actions/fetchAcceptancesHistory";
import {useRequests} from "../../contexts/request-context/requestContext";
import {getPaginationForPage} from "../../helpers/pagination/PaginationHelper";
import styles from "../absence-history-list/AbsenceHistoryList.module.scss";
import {AcceptanceHistoryTab} from "./AcceptanceHistoryTab";

export const AcceptanceHistoryList = () => {
    const header = 'Historia akceptacji';
    const FIRST_AVAILABLE_YEAR = 2016;
    const currentYear = (new Date()).getFullYear()

    const [pageNumber, setPageNumber] = useState(0)
    const [requestsState, requestsDispatch] = useRequests()
    const {history: acceptancesHistory, historyPage} = requestsState.acceptances

    const [selectedYear, setSelectedYear] = useState(currentYear);
    const [availableYears, setAvailableYears] = useState([]);

    const [currentSort, setCurrentSort] = useState({field: "request.startDate", order: "desc"})

    const getAvailableYears = useCallback(() => {
        const startYear = FIRST_AVAILABLE_YEAR;
        const years = [];
        for (let i = currentYear + 1; i >= startYear; i--) {
            years.push(i);
        }
        setAvailableYears(years);
    }, [currentYear]);

    useEffect(() => {
        fetchAcceptancesHistory(requestsDispatch, selectedYear, pageNumber, currentSort.field, currentSort.order)
        getAvailableYears();
    }, [getAvailableYears, requestsDispatch, selectedYear, pageNumber, currentSort]);

    const handleYearChange = (newYear) => {
        setSelectedYear(newYear);
    }

    const pagination = getPaginationForPage({
        page: historyPage,
        onClick: setPageNumber
    })

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
            <AcceptanceHistoryTab
                acceptances={acceptancesHistory}
                setSort={(sort) => setCurrentSort(sort)}
            />
            {pagination}
        </>
    );
}