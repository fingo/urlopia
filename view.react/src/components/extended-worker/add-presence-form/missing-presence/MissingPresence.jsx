import {Alert} from 'react-bootstrap';

import {useWorkers} from "../../../../contexts/workers-context/workersContext";
import styles from './MissingPresence.module.scss';

export const MissingPresence = () => {
    const [workersState] = useWorkers();
    const {userId} = workersState.workers.selectedWorker;
    const unspecifiedAbsences = workersState.workers.unspecifiedAbsences;
    const userAbsence = unspecifiedAbsences[userId.toString()];

    if (userAbsence && userAbsence.length) {
        return (
            <>
                <h5>Użytkownik nie podjął akcji w dniach: </h5>
                <div className={styles.alerts}>
                    {
                        userAbsence.map(absence => {
                            return (
                                <Alert key={absence} variant='danger' className='text-center'>
                                    <strong>{absence}</strong>
                                </Alert>
                            )
                        })
                    }
                </div>
            </>
        );
    }
    return null;
};
