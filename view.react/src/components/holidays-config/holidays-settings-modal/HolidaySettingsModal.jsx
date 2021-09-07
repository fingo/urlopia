
import PropTypes from "prop-types";

import {saveHolidays} from "../../../contexts/holidays-context/actions/saveHolidays";
import {useHolidays} from "../../../contexts/holidays-context/holidaysContext";
import {formattedDate} from "../../../helpers/DateHelper";
import {HolidaysModificationModal} from "../holidays-modification-modal/HolidaysModificationModal";

export const HolidaySettingsModal = ({show, onHide, year, holiday}) => {

    const [state, dispatchHolidays] = useHolidays();
    const {holidays} = state;

    const handleModifyingHoliday = (name, date, firstDay, lastDay) => {
        const modifiedHoliday = h => ({
            ...h,
            name,
            date: formattedDate(date),
        })
        
        const newHolidays = {
            startDate: firstDay,
            endDate: lastDay,
            holidaysToSave: holidays.map(h => h.id === holiday.id ? modifiedHoliday(h) : h),
        }

        saveHolidays(dispatchHolidays, newHolidays);
        onHide();
    }

    return (
      <HolidaysModificationModal
          show={show}
          onHide={onHide}
          handleAccept={handleModifyingHoliday}
          modalTitle={"Zmień święto"}
          holidayName={holiday.name}
          displayDate={new Date(holiday.date)}
          year={year}
      />
    );
}

HolidaySettingsModal.propTypes = {
    show: PropTypes.func.isRequired,
    onHide: PropTypes.func.isRequired,
    year: PropTypes.number,
    holiday: PropTypes.object.isRequired
}

HolidaySettingsModal.defaultProps = {
    year: (new Date()).getFullYear()
}