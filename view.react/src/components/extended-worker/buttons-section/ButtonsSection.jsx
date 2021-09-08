import {useState} from "react";
import {Button, OverlayTrigger, Tooltip} from "react-bootstrap";
import {
    ClockHistory as HistoryIcon,
    PersonCheckFill as PresenceIcon,
    PersonXFill as AbsenceIcon
} from "react-bootstrap-icons";

import {useWorkers} from "../../../contexts/workers-context/workersContext";
import {btnClass} from "../../../global-styles/btn.module.scss";
import {AddAbsenceForm} from "../add-absence-form/AddAbsenceForm";
import {AddPresenceForm} from "../add-presence-form/AddPresenceForm";
import {WorkerRequestsHistory} from "../worker-requests-history/WorkerRequestsHistory";
import styles from './ButtonsSection.module.scss';

export const ButtonsSection = () => {
    const [addPresenceModalShow, setAddPresenceModalShow] = useState(false);
    const [addAbsenceModalShow, setAddAbsenceModalShow] = useState(false);
    const [workerRequestsHistoryModalShow, setWorkerRequestsHistoryModalShow] = useState(false);

    const [workersState] = useWorkers();
    const {isEC} = workersState;
    const {userId, fullName} = isEC ? workersState.workers.selectedWorker : workersState.associates.selectedAssociate;

    return (
        <div className={styles.buttonsSection}>
            <OverlayTrigger
                placement='bottom'
                overlay={
                    <Tooltip id='tooltip-requests-history'>
                        Historia wniosków
                    </Tooltip>
                }
            >
                <Button className={btnClass}
                        onClick={(e) => {
                            e.currentTarget.blur();
                            setWorkerRequestsHistoryModalShow(true);
                        }}
                >
                    <HistoryIcon className={styles.icon}/>
                </Button>
            </OverlayTrigger>
            {
                workerRequestsHistoryModalShow &&
                <WorkerRequestsHistory show={workerRequestsHistoryModalShow}
                                       onHide={() => setWorkerRequestsHistoryModalShow(false)}
                                       fullName={fullName}
                />
            }

            <OverlayTrigger
                placement='bottom'
                overlay={
                    <Tooltip id='tooltip-add-presence'>
                        Dodaj obecność
                    </Tooltip>
                }
            >
                <Button className={btnClass}
                        onClick={(e) => {
                            e.currentTarget.blur();
                            setAddPresenceModalShow(true);
                        }}>
                    <PresenceIcon className={styles.icon}/>
                </Button>
            </OverlayTrigger>
            {
                addPresenceModalShow &&
                <AddPresenceForm show={addPresenceModalShow}
                                 onHide={() => setAddPresenceModalShow(false)}
                                 userId={userId}
                                 fullName={fullName}
                />
            }

            <OverlayTrigger
                placement='bottom'
                overlay={
                    <Tooltip id='tooltip-add-absence'>
                        Dodaj nieobecność
                    </Tooltip>
                }
            >
                <Button className={btnClass}
                        onClick={(e) => {
                            e.currentTarget.blur();
                            setAddAbsenceModalShow(true);
                        }}>
                    <AbsenceIcon className={styles.icon}/>
                </Button>
            </OverlayTrigger>
            {
                addAbsenceModalShow &&
                <AddAbsenceForm show={addAbsenceModalShow}
                                onHide={() => setAddAbsenceModalShow(false)}
                                userId={userId}
                                fullName={fullName}
                />
            }
        </div>
    );
};
