import {useEffect, useState} from 'react';
import {OverlayTrigger, Tooltip} from "react-bootstrap";
import {CalendarXFill as CalendarIcon, PlusCircleFill as PlusIcon} from 'react-bootstrap-icons';

import {fetchHolidays} from "../../contexts/holidays-context/actions/fetchHolidays";
import {useHolidays} from "../../contexts/holidays-context/holidaysContext";
import {DefaultHolidaysModal} from "./default-holidays-modal/DefaultHolidaysModal";
import {HolidayAdditionModal} from "./holidays-additional-modal/HolidaysAdditionModal";
import styles from './HolidaysConfig.module.scss'
import {HolidaysConfigTab} from './HolidaysConfigTab';
import {YearPicker} from './year-picker/YearPicker';

export const HolidaysConfig = () => {

    const [state, holidaysDispatch] = useHolidays()
    const {holidays} = state

    const currentYear = (new Date()).getFullYear();
    const availableYears = getAvailableYears(currentYear);

    const [selectedYear, setSelectedYear] = useState(currentYear);

    const [showModal, setShowModal] = useState(false);
    const [showDefaultHolidaysModal, setShowDefaultHolidaysModal] = useState(false);

    useEffect(() => {
        fetchHolidays(holidaysDispatch, selectedYear);
    }, [holidaysDispatch, selectedYear])

    const handleYearChange = (year) => {
        setSelectedYear(year);
    }

    return (
        <div>
            <div className={styles.panel}>
                <YearPicker availableYears={availableYears} selectedYear={selectedYear} handleYearChange={handleYearChange}/>
            </div>
            <HolidayAdditionModal
                show={showModal}
                onHide={()=>setShowModal(false)}
                year={selectedYear}
            />
            <DefaultHolidaysModal
                show={showDefaultHolidaysModal}
                onHide={() => setShowDefaultHolidaysModal(false)}
                year={selectedYear}
            />
            <HolidaysConfigTab holidays={holidays} year={selectedYear}/>
            <div className={styles.actionButtonsContainer}>
                <div>
                    <OverlayTrigger
                        placement='bottom'
                        overlay={
                            <Tooltip id={'tooltip-bottom'}>
                                Dodaj nowe święto
                            </Tooltip>}
                        >
                        <button onClick={() => setShowModal(true)}>
                            <PlusIcon className={styles.additionBtn} size={36}/>
                        </button>
                    </OverlayTrigger>
                    <OverlayTrigger
                        placement='bottom'
                        overlay={
                            <Tooltip id={'tooltip-bottom'}>
                                Przywróć domyślne święta
                            </Tooltip>}
                    >
                        <button onClick={() => setShowDefaultHolidaysModal(true)}>
                            <CalendarIcon className={styles.defaultBtn} size={36}/>
                        </button>
                    </OverlayTrigger>
                </div>
            </div>
        </div>
    )
}

const getAvailableYears = (currentYear) => {
    let result = [];
    for (let i = currentYear + 1; i >= 2016; i--) {
        result.push(i);
    }
    return result;
}