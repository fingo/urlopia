import PropTypes from "prop-types";
import {Button} from "react-bootstrap";

import {btnClass} from "../../global-styles/btn.module.scss";
import {sendPutRequest} from "../../helpers/RequestHelper";
import styles from "./ChangeLogCountYear.module.scss";


const CHANGE_COUNT_FOR_NEXT_YEAR = '/api/v2/absence-history/';

export const ChangeLogCountYear = ({isAdminView, countForNextYear, historyLogId, setRefresh}) => {

    const handleCountForNextYearChange = () => {
        sendPutRequest(`${CHANGE_COUNT_FOR_NEXT_YEAR}${historyLogId}`, {
            historyLogId,
            countForNextYear
        }).then(
            setRefresh()
        ).catch(error => {
            console.log('err: ', error);
        })
    }

    const getButtonMessage = countForNextYear?  `uwzględnij wniosek w roku późniejszym niż rok dodania`
                                                :`uwzględnij wniosek w roku dodania`;

    return (
        <div className={styles.forms}>
            {isAdminView && <Button
                className={btnClass}
                onClick={() => handleCountForNextYearChange()}
            >
                {getButtonMessage}
            </Button> }
        </div>
    );
};

ChangeLogCountYear.propTypes = {
    isAdminView: PropTypes.bool.isRequired,
    countForNextYear: PropTypes.bool.isRequired,
    historyLogId: PropTypes.number.isRequired,
    setRefresh: PropTypes.func.isRequired
}