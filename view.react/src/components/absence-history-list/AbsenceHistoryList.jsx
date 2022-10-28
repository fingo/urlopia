import PropTypes from "prop-types";
import {useCallback, useEffect, useState} from "react";
import {Button} from "react-bootstrap";
import {useLocation} from "react-router-dom";

import {getCurrentUser} from "../../api/services/session.service";
import {useAbsenceHistory} from "../../contexts/absence-history-context/absenceHistoryContext";
import btnClasses from "../../global-styles/btn.module.scss";
import {getPaginationForPage} from "../../helpers/pagination/PaginationHelper";
import { YearPicker } from "../holidays-config/year-picker/YearPicker";
import styles from "./AbsenceHistoryList.module.scss";
import {AbsenceHistoryTab} from "./AbsenceHistoryTab";

export const AbsenceHistoryList = ({fetchHistoryLogs, setPageNumber}) => {
    const [state, absenceHistoryDispatch] = useAbsenceHistory()
    const {absenceHistory, absenceHistoryPage} = state;
    const {ec: isUserEC} = getCurrentUser();
    const [refresh, setRefresh] = useState(false);

    const [isAdminView, setIsAdminView] = useState(false)
    const [showOnlyCountedInNextYear, setShowOnlyCountedInNextYear] = useState(false);


    const location = useLocation();

    const FIRST_AVAILABLE_YEAR = 2016;
    const currentYear = (new Date()).getFullYear()
    const availableYears = getAvailableYears(FIRST_AVAILABLE_YEAR, currentYear);

    const [selectedYear, setSelectedYear] = useState(currentYear);
    const [currentSort, setCurrentSort] = useState({field: "created", order: "desc"})

    const refreshLogs = useCallback(() => {
        setRefresh(prevState => ({
            refresh: !prevState.refresh
        }));
    },[setRefresh]);

    useEffect(() => {
        fetchHistoryLogs(absenceHistoryDispatch, {
            selectedYear,
            showOnlyCountedInNextYear,
            sortField: currentSort.field,
            sortOrder: currentSort.order
        })
    }, [absenceHistoryDispatch, selectedYear, fetchHistoryLogs, currentSort, showOnlyCountedInNextYear, refresh]);

    const handleYearChange = (newYear) => {
        setSelectedYear(newYear);
    }

    let vacationTypeLabel = isUserEC ? "Pozostały urlop" : "Pozostała przerwa"

    let header = 'Historia użytkownika';
    if (location.state?.fullName && location.pathname !== '/history/me') {
        header = header.concat(` - ${location.state.fullName}`);
        vacationTypeLabel = location.state.vacationTypeLabel;
        if (!isAdminView){
            setIsAdminView(true)
        }
    }

    const pagination = getPaginationForPage({
        page: absenceHistoryPage,
        onClick: pageNumber => setPageNumber(pageNumber)
    })

    const getButtonMessage = (countForNextYearShowed) => {
        return countForNextYearShowed? `Pokaż wszystkie wnioski`:
                               `Pokaż tylko wnioski które dotyczą innego roku niż rok w którym zostały złożone`;

    }

    const handleClick = active => {
        setShowOnlyCountedInNextYear(!active)
    }

    return (
        <>
            <div className={styles.panelFooter}>
                <h3>{header}</h3>
                <div>
                    <YearPicker availableYears={availableYears}
                                selectedYear={selectedYear}
                                handleYearChange={handleYearChange}
                                />
                </div>
                {isAdminView && <Button
                    className={btnClasses.btnClass}
                    onClick={() => handleClick(showOnlyCountedInNextYear)}
                >
                    {getButtonMessage(showOnlyCountedInNextYear)}
                </Button> }
            </div>
            <AbsenceHistoryTab
                logs={absenceHistory}
                vacationTypeLabel={vacationTypeLabel}
                setSort={(sort) => setCurrentSort(sort)}
                isAdminView={isAdminView}
                setRefresh={refreshLogs}
            />
            {pagination}
        </>
    );
}

const getAvailableYears = (startYear, currentYear) => {
    const years = [];
    for (let i = currentYear; i >= startYear; i--) {
        years.push(i);
    }
    return years;
}

AbsenceHistoryList.propTypes = {
    fetchHistoryLogs: PropTypes.func.isRequired,
}
