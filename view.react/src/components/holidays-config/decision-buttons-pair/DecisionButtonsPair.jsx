import PropTypes from "prop-types";
import {CheckLg as AcceptIcon, XLg as RejectIcon} from "react-bootstrap-icons";

import styles from './DecisionButtonsPair.module.scss';

export const DecisionButtonsPair = ({onReject, onAccept}) => {

    return (
        <div className={styles.actionButtonsContainer}>
            <button onClick={onReject} data-testid="reject-btn">
                <RejectIcon className={styles.rejectButton} size={30}/>
            </button>
            <button onClick={onAccept} data-testid="accept-btn">
                <AcceptIcon className={styles.acceptButton} size={30}/>
            </button>
        </div>
    )
}

DecisionButtonsPair.propTypes = {
    onReject: PropTypes.func.isRequired,
    onAccept: PropTypes.func.isRequired
}
