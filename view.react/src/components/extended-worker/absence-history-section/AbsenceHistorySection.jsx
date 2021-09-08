import {Button, Form} from "react-bootstrap";
import {useHistory} from "react-router-dom";

import {useAbsenceHistory} from "../../../contexts/absence-history-context/absenceHistoryContext";
import {useWorkers} from "../../../contexts/workers-context/workersContext";
import {btnClass} from "../../../global-styles/btn.module.scss";
import {formatLogs} from "../../../helpers/AbsenceHistoryFormatterHelper";
import {AbsenceHistoryTab} from "../../absence-history-list/AbsenceHistoryTab";

export const AbsenceHistorySection = () => {
    const history = useHistory();

    const [absenceHistoryState] = useAbsenceHistory();
    const {recentUserHistory} = absenceHistoryState;

    const [workersState] = useWorkers();
    const {isEC} = workersState;
    const {userId, fullName} = isEC ? workersState.workers.selectedWorker : workersState.associates.selectedAssociate;

    const handleShowMoreClick = (e) => {
        e.currentTarget.blur();
        history.push({
            pathname: `/history/${userId}`,
            state: {fullName},
        });
    }

    const formattedRecentUserHistory = formatLogs(recentUserHistory).slice(0, 5);

    return (
        <div>
            <Form.Label><strong>Historia użytkownika</strong></Form.Label>
            <AbsenceHistoryTab logs={formattedRecentUserHistory} isHidden={true}/>
            <Button className={btnClass}
                    onClick={(e) => handleShowMoreClick(e)}
            >
                Pokaż więcej
            </Button>
        </div>
    );
};