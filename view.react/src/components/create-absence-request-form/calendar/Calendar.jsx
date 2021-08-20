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
}) => {
    const shouldBeDisabled = day => {
        return isWeekend(day) || isHoliday(day, holidays);
    };

    if (isNormal) {
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
            />
        );
    }
    else {
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
                focusedRange={[0, 0]}
            />);
    }
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
}

Calendar.defaultProps = {
    isNormal: true,
    calendarClass: '',
    onChangeOption: () => null,
    holidays: [],
}