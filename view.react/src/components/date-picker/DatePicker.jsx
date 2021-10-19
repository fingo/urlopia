import pl from 'date-fns/locale/pl'
import PropTypes from 'prop-types'
import {default as ReactDatePicker} from "react-datepicker";

import styles from './DateTimePicker.module.scss'

export const DatePicker = ({withWeekend, customInput, chosenDate, onChange, maxDate}) => {
    const isWorkingDay = date => {
        const day = date.getDay()
        return day !== 0 && day !== 6
    }

    const getDateClassName = date => {
        const shouldBeDisplayed = withWeekend || isWorkingDay(date)
        return shouldBeDisplayed ? undefined : styles.invisible
    }

    return (
        <ReactDatePicker
            dateFormat={"yyyy-MM-dd"}
            showPopperArrow={false}
            selected={chosenDate}
            onChange={(date) => onChange(date)}
            filterDate={isWorkingDay}
            locale={pl}
            maxDate={maxDate}
            customInput={customInput}
            popperModifiers={[
                {
                    name: "offset",
                    options: {
                        offset: [-40, 10]
                    }
                }
            ]}
            useWeekdaysShort={true}
            dayClassName={getDateClassName}
            weekDayClassName={getDateClassName}
            fixedHeight
        />
    )
}

DatePicker.propTypes = {
    withWeekend: PropTypes.bool,
    customInput: PropTypes.object,
    chosenDate: PropTypes.object,
    onChange: PropTypes.func,
    maxDate: PropTypes.object
}

DatePicker.defaultProps = {
    withWeekend: true,
    customInput: null,
    chosenDate: new Date(),
    onChange: () => {},
    maxDate: new Date(),
}