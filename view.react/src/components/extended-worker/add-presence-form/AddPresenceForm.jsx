import PropTypes from "prop-types";
import {Modal} from 'react-bootstrap';

import {changeNoActionWorkers} from "../../../contexts/workers-context/actions/changeNoActionWorkers";
import {useWorkers} from "../../../contexts/workers-context/workersContext";
import {PresenceConfirmationPanel} from "../../presence-confirmation-panel/PresenceConfirmationPanel";
import {MissingPresence} from "./missing-presence/MissingPresence";

export const AddPresenceForm = ({show, onHide, fullName, userId}) => {
    const [, workersDispatch] = useWorkers();
    const handlePresenceConfirmation = (date) => {
        workersDispatch(changeNoActionWorkers(userId, date));
    }

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
                <PresenceConfirmationPanel userId={userId} onConfirmation={handlePresenceConfirmation}/>
                <MissingPresence />
            </Modal.Body>
        </Modal>
    );
}

AddPresenceForm.propTypes = {
    show: PropTypes.bool,
    onHide: PropTypes.func.isRequired,
    fullName: PropTypes.string,
}

AddPresenceForm.defaultProps = {
    show: false,
    fullName: 'unknown',
}