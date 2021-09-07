
import PropTypes from "prop-types";

import {saveHolidays} from "../../../contexts/holidays-context/actions/saveHolidays";
import {useHolidays} from "../../../contexts/holidays-context/holidaysContext";
import {formattedDate} from "../../../helpers/DateHelper";
import {HolidaysModificationModal} from "../holidays-modification-modal/HolidaysModificationModal";

export const HolidayAdditionModal = ({show, onHide, year}) => {

    const [state, holidaysDispatch] = useHolidays();
    const {holidays} = state

    const handleAddingNewHoliday = (name, date, firstDay, lastDay) => {

        const holidaysToSave = holidays.map(
            holiday => ({name: holiday.name, date: holiday.date})
        );

        holidaysToSave.push({name: name, date: formattedDate(date)});

        const newHolidays = {
            startDate: firstDay,
            endDate: lastDay,
            holidaysToSave: holidaysToSave
        };

        saveHolidays(holidaysDispatch, newHolidays);
        onHide();
    }

    return (
        <HolidaysModificationModal
            show={show}
            onHide={onHide}
            handleAccept={handleAddingNewHoliday}
            modalTitle={"Dodaj święto"}
            holidayName={''}
            displayDate={new Date(year, 0, 1)}
            year={year}
        />
    );
}

HolidayAdditionModal.propTypes = {
    show: PropTypes.func.isRequired,
    onHide: PropTypes.func.isRequired,
    year: PropTypes.number
}

HolidayAdditionModal.defaultProps = {
    year: (new Date()).getFullYear()
}

