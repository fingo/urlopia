import PropTypes from "prop-types";
import {Modal} from "react-bootstrap";

import {DecisionButtonsPair} from "../../../components/holidays-config/decision-buttons-pair/DecisionButtonsPair";

export const ConfirmRejectModal = ({show, onHide, rowId, rejectRequest}) => {

    const handleRejectRequest = () => {
        rejectRequest(rowId)
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
                <b>Czy na pewno chcesz odrzucić wniosek?</b>
            </Modal.Body>
            <Modal.Footer>
                <DecisionButtonsPair onReject={ onHide} onAccept={handleRejectRequest}/>
            </Modal.Footer>
        </Modal>
    )
}


ConfirmRejectModal.propTypes = {
    show: PropTypes.bool.isRequired,
    onHide: PropTypes.func.isRequired,
    rowId: PropTypes.number.isRequired,
    rejectRequest: PropTypes.func.isRequired
}