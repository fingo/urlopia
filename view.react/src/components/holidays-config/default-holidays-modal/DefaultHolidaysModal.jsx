import PropTypes from "prop-types";
import {Modal} from 'react-bootstrap';

import {saveHolidays} from "../../../contexts/holidays-context/actions/saveHolidays";
import {GENERATE_DEFAULT_HOLIDAYS_URL} from "../../../contexts/holidays-context/constants";
import {useHolidays} from "../../../contexts/holidays-context/holidaysContext";
import {sendGetRequest} from "../../../helpers/RequestHelper";
import {DecisionButtonsPair} from "../decision-buttons-pair/DecisionButtonsPair";

export const DefaultHolidaysModal = ({show, onHide, year}) => {

    const [, dispatchHolidays] = useHolidays();

    const handleGeneratingDefault = () => {
        sendGetRequest(`${GENERATE_DEFAULT_HOLIDAYS_URL}${year}`)
            .then(data => {
                const newHolidays = {
                    startDate: new Date(year, 0, 1),
                    endDate: new Date(year, 11, 31),
                    holidaysToSave: data
                }
                saveHolidays(dispatchHolidays, newHolidays);
            })
            .catch(error => {
            })
        onHide();
    }

    return (
        <Modal
            show={show}
            onHide={onHide}
            size="sm"
            centered
        >
            <Modal.Body>
               <b>Czy na pewno chcesz przywrócić domyślne święta dla danego roku?</b>
            </Modal.Body>
            <Modal.Footer>
                <DecisionButtonsPair onReject={onHide} onAccept={handleGeneratingDefault}/>
            </Modal.Footer>
        </Modal>
    );
}

DefaultHolidaysModal.propTypes = {
    show: PropTypes.bool.isRequired,
    onHide: PropTypes.func.isRequired,
    year: PropTypes.number,
}

DefaultHolidaysModal.defaultProps = {
    year: (new Date()).getFullYear()
}