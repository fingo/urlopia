import {isWeekend} from "date-fns";
import pl from "date-fns/locale/pl";
import PropTypes from "prop-types";
import DateRange from "react-date-range/dist/components/DateRange";

import {isHoliday} from "../../../helpers/IsHolidayHelper";

export const Calendar = ({
    isNormal,
    calendarClass,
    onChangeOption,
    selectedRange,
    holidays,
    withWeekends
}) => {
    const shouldBeDisabled = day => {
        return (!withWeekends && isWeekend(day)) || isHoliday(day, holidays);
    };

    return (
        <DateRange
            className={calendarClass}
            locale={pl}
            onChange={onChangeOption}
            showSelectionPreview={true}
            showMonthAndYearPickers={false}
            months={2}
            ranges={selectedRange}
            direction='horizontal'
            disabledDay={day => shouldBeDisabled(day)}
            focusedRange={isNormal ? undefined : [0, 0]}
        />);
}

Calendar.propTypes = {
    isNormal: PropTypes.bool,
    calendarClass: PropTypes.string,
    onChangeOption: PropTypes.func,
    selectedRange: PropTypes.arrayOf(
        PropTypes.shape({
            startDate: PropTypes.object,
            endDate: PropTypes.object,
            key: PropTypes.string,
            color: PropTypes.string,
        })
    ).isRequired,
    holidays: PropTypes.arrayOf(
        PropTypes.shape({
            id: PropTypes.number,
            name: PropTypes.string,
            date: PropTypes.string,
        })),
    withWeekends: PropTypes.bool,
}

Calendar.defaultProps = {
    isNormal: true,
    calendarClass: '',
    onChangeOption: () => null,
    holidays: [],
    withWeekends: false,
}