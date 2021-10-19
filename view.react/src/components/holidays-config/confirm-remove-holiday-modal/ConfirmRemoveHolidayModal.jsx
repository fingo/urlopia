import PropTypes from "prop-types";
import {Modal} from "react-bootstrap";

import {DecisionButtonsPair} from "../decision-buttons-pair/DecisionButtonsPair";

export const ConfirmRemoveHolidayModal = ({show, onHide, rowId, removeHoliday}) => {

    const handleRemoveHoliday = () => {
        removeHoliday(rowId)
        onHide()
    }

    return(
        <Modal
            show={show}
            onHide={onHide}
            size="sm"
            centered
        >
            <Modal.Body>
                <b>Czy na pewno chcesz usunąć wybrane święto?</b>
            </Modal.Body>
            <Modal.Footer>
                <DecisionButtonsPair onReject={ onHide} onAccept={handleRemoveHoliday}/>
            </Modal.Footer>
        </Modal>
    )
}

ConfirmRemoveHolidayModal.propTypes = {
    show: PropTypes.bool.isRequired,
    onHide: PropTypes.func.isRequired,
    rowId: PropTypes.number.isRequired,
    removeHoliday: PropTypes.func.isRequired
}


