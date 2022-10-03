import PropTypes from "prop-types";
import {useEffect, useState} from "react";
import {Button, Modal} from "react-bootstrap";

import {
    acceptRequest,
    cancelRequest,
    rejectRequest
} from "../../../contexts/request-context/actions/changeRequestStatus";
import {fetchWorkerRequestsHistory} from "../../../contexts/request-context/actions/fetchWorkerRequestsHistory";
import {useRequests} from "../../../contexts/request-context/requestContext";
import {btnClass} from "../../../global-styles/btn.module.scss";
import {WorkerRequestsHistoryTable} from "./worker-requests-history-table/WorkerRequestsHistoryTable";
import styles from './WorkerRequestsHistory.module.scss';

export const WorkerRequestsHistory = ({show, onHide, fullName, userId}) => {
    const [requestsState, requestsDispatch] = useRequests();
    const {requests, requestsPage, fetching} = requestsState.workerRequestsHistory;

    const [pageNumber, setPageNumber] = useState(0)
    const [currentSort, setCurrentSort] = useState({field: "created", order: "desc"})

    useEffect(() => {
        fetchWorkerRequestsHistory(requestsDispatch, userId, pageNumber, currentSort.field, currentSort.order);
    }, [requestsDispatch, userId, pageNumber, currentSort]);

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
                    requests={requests}
                    requestsPage={requestsPage}
                    acceptRequest={(requestId) => acceptRequest(requestsDispatch, {requestId})}
                    cancelRequest={(requestId) => cancelRequest(requestsDispatch, {requestId})}
                    rejectRequest={(requestId) => rejectRequest(requestsDispatch, {requestId})}
                    isFetching={fetching}
                    setPageNumber={setPageNumber}
                    setSort={setCurrentSort}
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
    userId: PropTypes.number
}

WorkerRequestsHistory.defaultProps = {
    show: false,
    fullName: 'unknown',
}