import PropTypes from "prop-types";
import {Form} from "react-bootstrap";

import {useAbsenceHistory} from "../../../contexts/absence-history-context/absenceHistoryContext";
import {fetchUserAbsenceHistory} from "../../../contexts/absence-history-context/actions/fetchUserAbsenceHistory";
import {useVacationDays} from "../../../contexts/vacation-days-context/vacationDaysContext";
import {changeRemainingDays} from "../../../contexts/workers-context/actions/changeRemainingDays";
import {changeWorkTime} from "../../../contexts/workers-context/actions/changeWorkTime";
import {useWorkers} from "../../../contexts/workers-context/workersContext";
import {pushSuccessNotification} from "../../../helpers/notifications/Notifications";
import {parseDaysPool} from "../../../helpers/parseDaysPoolToHours";
import {sendPutRequest} from "../../../helpers/RequestHelper";
import {updateVacationDays} from "../../../helpers/updateVacationDays";
import {ChangeDaysPoolForm} from "../change-days-pool-form/ChangeDaysPoolForm";
import styles from "./ChangeDaysPoolAndWorkTimeSection.module.scss";

const CHANGE_DAYS_POOL_URL_PREFIX = '/api/v2/users/';
const CHANGE_DAYS_POOL_URL_POSTFIX = '/vacation-days';

export const ChangeDaysPoolAndWorkTimeSection = ({workTime}) => {
    const [, absenceHistoryDispatch] = useAbsenceHistory();
    const [workersState, workersDispatch] = useWorkers();
    const {isEC} = workersState;
    const {userId} = isEC ? workersState.workers.selectedWorker : workersState.associates.selectedAssociate;

    const [, vacationDaysDispatch] = useVacationDays();

    const handleChangeDaysPool = (valuesFromForm) => {
        const {daysToChange, comment} = valuesFromForm;
        if (daysToChange !== '') {
            const hours = parseDaysPool(daysToChange);
            sendPutRequest(`${CHANGE_DAYS_POOL_URL_PREFIX}${userId}${CHANGE_DAYS_POOL_URL_POSTFIX}`, {
                hours,
                comment,
            }).then(data => {
                workersDispatch(changeRemainingDays(data.remainingDays.toString(), data.remainingHours.toString()));
                pushSuccessNotification("Pomyślnie zmieniono pulę godzin pracownika");
                updateVacationDays(vacationDaysDispatch);
            }).then(() => {
                fetchUserAbsenceHistory(absenceHistoryDispatch, userId);
            }).catch(error => {
                console.log('err: ', error);
            })
        }
    }

    const handleChangeWorkTime = e => {
        const {value} = e.target;
        changeWorkTime(workersDispatch, userId, value);
    }

    return (
        <div className={styles.forms}>
            <ChangeDaysPoolForm onSubmit={values => handleChangeDaysPool(values)}/>
            <Form>
                <Form.Label><strong>Zmień etat</strong></Form.Label>
                <Form.Select aria-label="Default select example"
                             className={styles.workingHoursSelection}
                             defaultValue={workTime}
                             onChange={e => handleChangeWorkTime(e)}
                             data-testid='selector'
                >
                    <option value='1/1'>pełny etat</option>
                    <option value='1/2'>1/2 etatu</option>
                    <option value='3/4'>3/4 etatu</option>
                    <option value='1/4'>1/4 etatu</option>
                    <option value='4/5'>4/5 etatu</option>
                    <option value='3/5'>3/5 etatu</option>
                    <option value='2/5'>2/5 etatu</option>
                    <option value='1/5'>1/5 etatu</option>
                    <option value='7/8'>7/8 etatu</option>
                    <option value='3/8'>3/8 etatu</option>
                    <option value='1/8'>1/8 etatu</option>
                    <option value='1/16'>1/16 etatu</option>
                </Form.Select>
            </Form>
        </div>
    );
};

ChangeDaysPoolAndWorkTimeSection.propTypes = {
    workTime: PropTypes.string.isRequired,
}
