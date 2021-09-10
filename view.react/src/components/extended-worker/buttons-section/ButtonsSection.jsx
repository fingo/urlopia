import PropTypes from "prop-types";
import {useState} from "react";
import {
    ClockHistory as HistoryIcon,
    PersonCheckFill as PresenceIcon,
    PersonXFill as AbsenceIcon
} from "react-bootstrap-icons";

import {useWorkers} from "../../../contexts/workers-context/workersContext";
import {AddAbsenceForm} from "../add-absence-form/AddAbsenceForm";
import {AddPresenceForm} from "../add-presence-form/AddPresenceForm";
import {WorkerRequestsHistory} from "../worker-requests-history/WorkerRequestsHistory";
import {ActionButton} from "./action-button/ActionButton";
import styles from './ButtonsSection.module.scss';

export const ButtonsSection = ({isUnspecifiedAbsences}) => {
    const [workerRequestsHistoryModalShow, setWorkerRequestsHistoryModalShow] = useState(false);
    const [addPresenceModalShow, setAddPresenceModalShow] = useState(false);
    const [addAbsenceModalShow, setAddAbsenceModalShow] = useState(false);

    const [workersState] = useWorkers();
    const {isEC} = workersState;
    const {userId, fullName} = isEC ? workersState.workers.selectedWorker : workersState.associates.selectedAssociate;

    return (
        <div className={styles.buttonsSection}>
            <ActionButton tooltipText='Historia wniosków'
                          icon={<HistoryIcon className={styles.icon}/>}
                          onButtonClick={(showStatus) => setWorkerRequestsHistoryModalShow(showStatus)}
                          showModal={workerRequestsHistoryModalShow}
                          modal={<WorkerRequestsHistory show={workerRequestsHistoryModalShow}
                                                        onHide={() => setWorkerRequestsHistoryModalShow(false)}
                                                        fullName={fullName}/>}
                          isWithNotification={false}
            />

            {
                isEC &&
                <ActionButton tooltipText='Dodaj obecność'
                              icon={<PresenceIcon className={styles.icon}/>}
                              onButtonClick={(showStatus) => setAddPresenceModalShow(showStatus)}
                              showModal={addPresenceModalShow}
                              modal={<AddPresenceForm show={addPresenceModalShow}
                                                      onHide={() => setAddPresenceModalShow(false)}
                                                      userId={userId}
                                                      fullName={fullName}/>}
                              isWithNotification={isUnspecifiedAbsences}
                />
            }

            <ActionButton tooltipText='Dodaj nieobecność'
                          icon={<AbsenceIcon className={styles.icon}/>}
                          onButtonClick={(showStatus) => setAddAbsenceModalShow(showStatus)}
                          showModal={addAbsenceModalShow}
                          modal={<AddAbsenceForm show={addAbsenceModalShow}
                                                 onHide={() => setAddAbsenceModalShow(false)}
                                                 userId={userId}
                                                 fullName={fullName}/>}
                          isWithNotification={false}
            />
        </div>
    );
};

ButtonsSection.propTypes = {
    isUnspecifiedAbsences: PropTypes.bool,
}

ButtonsSection.defaultProps = {
    isUnspecifiedAbsences: false,
}
