import {useState} from "react";
import {Button} from "react-bootstrap";
import {
    ClockHistory as HistoryIcon,
    PersonCheckFill as PresenceIcon,
    PersonXFill as AbsenceIcon
} from "react-bootstrap-icons";

import {btnClass} from "../../../global-styles/btn.module.scss";
import {AddAbsenceForm} from "../add-absence-form/AddAbsenceForm";
import {AddPresenceForm} from "../add-presence-form/AddPresenceForm";
import {WorkerRequestsHistory} from "../worker-requests-history/WorkerRequestsHistory";
import styles from './ButtonsSection.module.scss';

export const ButtonsSection = () => {
    const [addPresenceModalShow, setAddPresenceModalShow] = useState(false);
    const [addAbsenceModalShow, setAddAbsenceModalShow] = useState(false);
    const [workerRequestsHistoryModalShow, setWorkerRequestsHistoryModalShow] = useState(false);

    return (
        <div className={styles.buttonsSection}>
            <Button title='Historia wniosków' className={btnClass}
                    onClick={(e) => {
                        e.currentTarget.blur();
                        setWorkerRequestsHistoryModalShow(true);
                    }}
            >
                <HistoryIcon className={styles.icon}/>
            </Button>
            {
                workerRequestsHistoryModalShow &&
                <WorkerRequestsHistory show={workerRequestsHistoryModalShow}
                                       onHide={() => setWorkerRequestsHistoryModalShow(false)}
                />
            }

            <Button title='Dodaj obecność' className={btnClass}
                    onClick={(e) => {
                        e.currentTarget.blur();
                        setAddPresenceModalShow(true);
                    }}>
                <PresenceIcon className={styles.icon}/>
            </Button>
            {
                addPresenceModalShow &&
                <AddPresenceForm show={addPresenceModalShow} onHide={() => setAddPresenceModalShow(false)}/>
            }

            <Button title='Dodaj nieobecność' className={btnClass}
                    onClick={(e) => {
                        e.currentTarget.blur();
                        setAddAbsenceModalShow(true);
                    }}>
                <AbsenceIcon className={styles.icon}/>
            </Button>
            {
                addAbsenceModalShow &&
                <AddAbsenceForm show={addAbsenceModalShow} onHide={() => setAddAbsenceModalShow(false)}/>
            }

        </div>
    );
};
