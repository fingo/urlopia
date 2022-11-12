import PropTypes from "prop-types";
import {Modal} from "react-bootstrap";

import {DecisionButtonsPair} from "../../holidays-config/decision-buttons-pair/DecisionButtonsPair";

export const ConfirmRemoveHistoryLogModal = ({show, onHide, historyLogId, removeHistoryLog}) => {

    const handleRemoveHistoryLog = () => {
        removeHistoryLog(historyLogId)
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
                <b>Czy na pewno chcesz usunąć wybrany wpis w historii?</b>
            </Modal.Body>
            <Modal.Footer>
                <DecisionButtonsPair onReject={onHide} onAccept={handleRemoveHistoryLog}/>
            </Modal.Footer>
        </Modal>
    )
}

ConfirmRemoveHistoryLogModal.propTypes = {
    show: PropTypes.bool.isRequired,
    onHide: PropTypes.func.isRequired,
    historyLogId: PropTypes.number.isRequired,
    removeHistoryLog: PropTypes.func.isRequired
}