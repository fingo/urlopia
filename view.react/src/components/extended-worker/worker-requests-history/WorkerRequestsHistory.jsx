import PropTypes from "prop-types";
import {useEffect} from "react";
import {Button, Modal} from "react-bootstrap";

import {
    acceptRequest,
    cancelRequest,
    rejectRequest
} from "../../../contexts/request-context/actions/changeRequestStatus";
import {fetchWorkerRequestsHistory} from "../../../contexts/request-context/actions/fetchWorkerRequestsHistory";
import {useRequests} from "../../../contexts/request-context/requestContext";
import {btnClass} from "../../../global-styles/btn.module.scss";
import {requestPeriodFormatter} from "../../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {WorkerRequestsHistoryTable} from "./worker-requests-history-table/WorkerRequestsHistoryTable";
import styles from './WorkerRequestsHistory.module.scss';

export const WorkerRequestsHistory = ({show, onHide, fullName}) => {
    const [requestsState, requestsDispatch] = useRequests();
    const {requests, fetching} = requestsState.workerRequestsHistory;

    useEffect(() => {
        const firstName = fullName?.split(' ')[0];
        const lastName = fullName?.split(' ')[1];
        fetchWorkerRequestsHistory(requestsDispatch, firstName, lastName);
    }, [requestsDispatch, fullName]);

    const formattedRequests = requests.map(req => {
        return {
            id: req.id,
            period: requestPeriodFormatter(req),
            examiner: req.acceptances.map(acc => acc.leaderName),
            type: req.type,
            status: req.status,
        }
    })

    return (
        <Modal
            dialogClassName={styles.modal}
            show={show}
            onHide={onHide}
            size="lg"
            aria-labelledby="contained-modal-title-vcenter"
            centered
        >
            <Modal.Header closeButton>
                <Modal.Title id="contained-modal-title-vcenter">
                    Historia wniosków: <strong>{fullName}</strong>
                </Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <WorkerRequestsHistoryTable
                    requests={formattedRequests}
                    acceptRequest={(requestId) => acceptRequest(requestsDispatch, {requestId})}
                    cancelRequest={(requestId) => cancelRequest(requestsDispatch, {requestId})}
                    rejectRequest={(requestId) => rejectRequest(requestsDispatch, {requestId})}
                    isFetching={fetching}
                />
            </Modal.Body>
            <Modal.Footer>
                <Button onClick={onHide} className={btnClass}>Anuluj</Button>
            </Modal.Footer>
        </Modal>
    );
};

WorkerRequestsHistory.propTypes = {
    show: PropTypes.bool,
    onHide: PropTypes.func.isRequired,
    fullName: PropTypes.string,
}

WorkerRequestsHistory.defaultProps = {
    show: false,
    fullName: 'unknown',
}