import PropTypes from "prop-types";
import {useEffect, useState} from 'react';
import {GearFill as GearIcon, TrashFill as TrashIcon} from "react-bootstrap-icons";
import BootstrapTable from 'react-bootstrap-table-next';

import {saveHolidays} from "../../contexts/holidays-context/actions/saveHolidays";
import {useHolidays} from "../../contexts/holidays-context/holidaysContext";
import {tableClass} from "../../helpers/react-bootstrap-table2/tableClass";
import {ConfirmRemoveHolidayModal} from "./confirm-remove-holiday-modal/ConfirmRemoveHolidayModal";
import {HolidaySettingsModal} from "./holidays-settings-modal/HolidaySettingsModal";
import styles from './HolidaysConfigTab.module.scss';

export const HolidaysConfigTab = ({holidays, year}) => {

    const [, dispatchHolidays] = useHolidays();

    const [modalsShow, setModalsShow] = useState({});
    const [showConfirmRemoveHolidayModal, setShowConfirmRemoveHolidayModal] = useState(false)
    const [rowId, setRowId] = useState(0);


    const lastDay = new Date(year, 11, 31);
    const firstDay = new Date(year, 0, 1);

    useEffect(() => {
        const newModalsShow = {}
        holidays.forEach(holiday => newModalsShow[holiday.id] = false)
        setModalsShow(newModalsShow)
    }, [holidays])

    const handleRemovingHoliday = (id) => {
        const holidaysToSave = holidays
            .filter(holiday => holiday.id !== id)
            .map(holiday => ({name: holiday.name, date: holiday.date}));

        const newHolidays = {
            startDate: firstDay,
            endDate: lastDay,
            holidaysToSave: holidaysToSave
        }

        saveHolidays(dispatchHolidays, newHolidays);
        setModalsShow({...modalsShow, [id]: false});
    }

    const actionFormatter = (cell, row) => {

        const holiday = {...row};

        return (
            <>
                <div className={styles.actionButtonsContainer}>
                    <button onClick={() => {
                        setModalsShow({...modalsShow, [holiday.id]: true})
                    }
                    }>
                        <GearIcon className={styles.settingsButton} size={20}/>
                    </button>
                    <button onClick={() => {
                        setRowId(row.id);
                        setShowConfirmRemoveHolidayModal(true);
                    }}>
                        <TrashIcon className={styles.removeButton} size={20}/>
                    </button>
                </div>
            </>
        )
    }

    const modals = holidays.map(holiday => {
        return (
            <HolidaySettingsModal
                key={holiday.id}
                show={modalsShow[holiday.id]}
                onHide={() => setModalsShow({...modalsShow, [holiday.id]: false})}
                year = {year}
                holiday = {holiday}
            />
        )
    })

    const specificDayFormatter = (date) => {
        const days = ["Niedziela", "Poniedziałek", "Wtorek",
            "Środa", "Czwartek", "Piątek", "Sobota"];
        return days[new Date(date).getDay()];
    }

    const columns = [
        {
            dataField: 'id',
            hidden: true
        },
        {
            dataField: 'name',
            text: 'Święto',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle'}
        },
        {
            dataField: 'date',
            text: 'Data',
            headerAlign: 'center',
            align: 'center',
            sort: true,
            style: {verticalAlign: 'middle'}
        },
        {
            dataField: 'dayOfWeek',
            text: 'Dzień tygodnia',
            headerAlign: 'center',
            align: 'center',
            formatter: (cell, row) => specificDayFormatter(row.date),
            style: {verticalAlign: 'middle'}
        },
        {
            dataField: 'action',
            text: 'Akcja',
            headerAlign: 'center',
            align: 'center',
            formatter: actionFormatter,
            style: {verticalAlign: 'middle'}
        }
    ];

    return (
        <>
            {modals}
            <BootstrapTable
                bootstrap4
                keyField='id'
                data={holidays}
                wrapperClasses={tableClass}
                columns={columns}
                bordered={false}
                hover
            />
            <ConfirmRemoveHolidayModal show={showConfirmRemoveHolidayModal}
                                       onHide={() => setShowConfirmRemoveHolidayModal(false)}
                                       rowId={rowId}
                                       removeHoliday={handleRemovingHoliday}/>
        </>
    );

}

HolidaysConfigTab.propTypes = {
    holidays: PropTypes.array,
    year: PropTypes.number
}

HolidaysConfigTab.defaultProps = {
    holidays: [],
    year: (new Date()).getFullYear()
}