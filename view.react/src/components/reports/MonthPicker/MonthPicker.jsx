import "react-month-picker/css/month-picker.css";

import PropTypes from 'prop-types';
import { useRef } from "react";
import {Button} from 'react-bootstrap';
import ReactMonthPicker from "react-month-picker";

import styles from './MonthPicker.module.scss';



export const MonthPicker = ({ range, chosenMonth, setChosenMonth}) => {
    const monthPickerRef = useRef();

    const showMonthPicker = event => {
        monthPickerRef.current.show()
        event.preventDefault();
    };

    const handleOnDismiss = () => {
    };

    const handleOnChange = (year, month) => {
        setChosenMonth({ year, month });
        monthPickerRef.current.dismiss()
    };

    const availableMonths = ["STY", "LUT", "MAR", "KWI", "MAJ", "CZE", "LIP", "SIE", "WRZ", "PAŹ", "LIS", "GRU"];

    const getMonthValue = () => {
        const month = chosenMonth && chosenMonth.month ? chosenMonth.month : 0;
        const year = chosenMonth && chosenMonth.year ? chosenMonth.year : 0;

        return month && year ? `${availableMonths[month-1]}-${year}` : "Wybierz miesiąc...";
    };

    return (
        <div className="MonthYearPicker">
            <ReactMonthPicker
                ref={monthPickerRef}
                lang={availableMonths}
                years={range}
                value={chosenMonth}
                onChange={(year, month) => handleOnChange(year, month)}
                onDismiss={() => handleOnDismiss()}
                data-testid={"monthPicker"}
            >
                <Button
                    className={styles.btnClass}
                    onClick={(e) => showMonthPicker(e)}
                    data-testid={"monthButton"}
                >
                    {getMonthValue()}
                </Button>
            </ReactMonthPicker>
        </div>
    );
}

MonthPicker.propTypes = {
    range: PropTypes.object.isRequired,
    chosenMonth: PropTypes.object.isRequired,
    setChosenMonth: PropTypes.func.isRequired
}