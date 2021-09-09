import "react-month-picker/css/month-picker.css";

import PropTypes from 'prop-types';
import { useState } from "react";
import {Button} from 'react-bootstrap';
import ReactMonthPicker from "react-month-picker";

import styles from './MonthPicker.module.scss';



export const MonthPicker = ({ range, chosenMonth, setChosenMonth}) => {

    const [isVisible, setVisibility] = useState(false);

    const showMonthPicker = event => {
        setVisibility(true);
        event.preventDefault();
    };

    const handleOnDismiss = () => {
        setVisibility(false);
    };

    const handleOnChange = (year, month) => {
        setChosenMonth({ year, month });
        setVisibility(false);
    };

    const availableMonths = ["STY", "LUT", "MAR", "KWI", "MAJ", "CZE", "LIP", "SIE", "WRZ", "PAŹ", "LIS", "GRU"];

    const getMonthValue = () => {
        const month = chosenMonth && chosenMonth.month ? chosenMonth.month : 0;
        const year = chosenMonth && chosenMonth.year ? chosenMonth.year : 0;

        return month && year ? `${availableMonths[month-1]}-${year}` : "Wybierz miesiąc...";
    };

    return (
        <div className="MonthYearPicker">
            <Button
                className={styles.btnClass}
                onClick={showMonthPicker}
                data-testid={"monthButton"}
            >
                {getMonthValue()}
            </Button>
            <ReactMonthPicker
                show={isVisible}
                lang={availableMonths}
                years={range}
                value={chosenMonth}
                onChange={handleOnChange}
                onDismiss={handleOnDismiss}
                data-testid={"monthPicker"}
            />
        </div>
    );
}

MonthPicker.propTypes = {
    range: PropTypes.object.isRequired,
    chosenMonth: PropTypes.object.isRequired,
    setChosenMonth: PropTypes.func.isRequired
}