import PropTypes from "prop-types";
import {Modal} from 'react-bootstrap';

import {useWorkers} from "../../../contexts/workers-context/workersContext";
import {PresenceConfirmationPanel} from "../../presence-confirmation-panel/PresenceConfirmationPanel";

export const AddPresenceForm = ({show, onHide}) => {
    const [workersState] = useWorkers();
    const {fullName} = workersState.selectedUser;

    return (
        <Modal
            show={show}
            onHide={onHide}
            size="lg"
            aria-labelledby="contained-modal-title-vcenter"
            centered
        >
            <Modal.Header closeButton>
                <Modal.Title id="contained-modal-title-vcenter">
                    Dodaj obecność dla: <strong>{fullName}</strong>
                </Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <PresenceConfirmationPanel />
            </Modal.Body>
        </Modal>
    );
}

AddPresenceForm.propTypes = {
    show: PropTypes.bool,
    onHide: PropTypes.func.isRequired,
}

AddPresenceForm.defaultProps = {
    show: false,
}