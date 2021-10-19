import pl from 'date-fns/locale/pl'
import PropTypes from 'prop-types'
import {default as ReactDatePicker} from "react-datepicker";

export const TimePicker = ({customInput, chosenTime, onChange}) => {
    return (
        <ReactDatePicker
            showPopperArrow={false}
            popperModifiers={[
                {
                    name: "offset",
                    options: {
                        offset: [10, 10]
                    }
                }
            ]}
            customInput={customInput}
            selected={chosenTime}
            onChange={(date) => onChange(date)}
            showTimeSelect
            showTimeSelectOnly
            timeIntervals={10}
            timeCaption=""
            dateFormat="HH:mm"
            locale={pl}
        />
    );
}

TimePicker.propTypes = {
    customInput: PropTypes.object,
    chosenTime: PropTypes.object,
    onChange: PropTypes.func,
}

TimePicker.defaultProps = {
    customInput: null,
    chosenTime: new Date(),
    onChange: () => {},
}