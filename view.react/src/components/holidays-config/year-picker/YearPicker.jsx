import PropTypes from "prop-types";
import {Dropdown, DropdownButton} from "react-bootstrap";

import styles from "../../../global-styles/AbsenceHistoryList.module.scss";

export const YearPicker = ({availableYears, selectedYear, handleYearChange}) => {

    return (
        <div>
            <DropdownButton id="dropdown-basic-button"
                            title={selectedYear}
                            size="sm"
                            bsPrefix={styles.datesDropdown}
                            onSelect={handleYearChange}>
                {availableYears.map((val, index) => (
                    <Dropdown.Item className={styles.dropItem}
                                   key={index}
                                   eventKey={val}
                    >
                        {val}
                    </Dropdown.Item>
                ))}
            </DropdownButton>
        </div>
    )
}

YearPicker.propTypes = {
    availableYears: PropTypes.array.isRequired,
    selectedYear: PropTypes.number.isRequired,
    handleYearChange: PropTypes.func.isRequired
}
