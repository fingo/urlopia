import classNames from "classnames";
import PropTypes from "prop-types";
import {useState} from "react";
import {CheckSquareFill as AcceptIcon, XSquareFill as XIcon} from "react-bootstrap-icons";
import {BeatLoader} from "react-spinners";

import {ACCEPTED, PENDING} from "../../../../constants/statuses";
import spinnerClasses from '../../../../global-styles/loading-spinner.module.scss';
import tableClasses from '../../../../global-styles/table-styles.module.scss';
import {getPaginationForPage} from "../../../../helpers/pagination/PaginationHelper";
import {
    requestPeriodFormatter,
    requestStatusMapper,
    requestTypeMapper,
    statusFormatter,
    textAsArrayFormatter
} from "../../../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {disableSortingFunc} from "../../../../helpers/react-bootstrap-table2/utils";
import Table from "../../../table/Table";
import {AcceptancesModal} from "../../../user-requests-list/AcceptancesModal";
import styles from './WorkerRequestsHistoryTable.module.scss';

export const WorkerRequestsHistoryTable = ({
    requests,
    requestsPage,
    acceptRequest,
    cancelRequest,
    rejectRequest,
    isFetching,
    setPageNumber,
    setSort
}) => {
    const [modalsShow, setModalsShow] = useState({})

    const pagination = getPaginationForPage({
        page: requestsPage,
        onClick: pageNumber => setPageNumber(pageNumber)
    })

    const actionFormatter = (cell, row) => {
        const acceptBtnClass = classNames(tableClasses.actionBtn, 'text-success');
        const cancelBtnClass = classNames(tableClasses.actionBtn, 'text-warning');
        const rejectBtnClass = classNames(tableClasses.actionBtn, 'text-danger');
        if (row.status === PENDING) {
            return (
                <div className={tableClasses.actions}>
                    <button
                        title='Zaakceptuj wniosek'
                        className={acceptBtnClass}
                        onClick={() => acceptRequest(row.id)}
                    >
                        <AcceptIcon/>
                    </button>

                    <button
                        title='Odrzuć wniosek'
                        className={rejectBtnClass}
                        onClick={() => rejectRequest(row.id)}
                    >
                        <XIcon/>
                    </button>
                </div>
            );
        } else if (row.status === ACCEPTED) {
            return (
                <div className={tableClasses.actions}>
                    <button
                        title='Anuluj wniosek'
                        className={cancelBtnClass}
                        onClick={() => cancelRequest(row.id)}
                    >
                        <XIcon/>
                    </button>
                </div>
            );
        }
    }

    const showModal = requestId => setModalsShow({...modalsShow, [requestId]: true})
    const hideModal = requestId => setModalsShow({...modalsShow, [requestId]: false})

    const modals = requests.map(req => (
        <AcceptancesModal
            key={req.id}
            request={req}
            show={modalsShow[req.id]}
            onHide={() => hideModal(req.id)}
        />
    ))

    const formattedRequests = requests.map(req => {
        return {
            id: req.id,
            period: requestPeriodFormatter(req),
            examiner: req.acceptances.map(acc => acc.leaderName),
            type: req.type,
            status: req.status,
        }
    })

    const columns = [
        {
            name: 'id',
            hidden: true,
        },
        {
            name: 'period',
            text: 'Termin',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle'},
            sort: true,
            sortFunc: disableSortingFunc,
            onSort: (field, order) => {
                const sortField = "startDate"
                setSort({field: sortField, order: order})
            },
        },
        {
            name: 'examiner',
            text: 'Rozpatrujący',
            headerAlign: 'center',
            align: 'center',
            formatter: textAsArrayFormatter,
            style: {verticalAlign: 'middle'},
        },
        {
            name: 'type',
            text: 'Rodzaj',
            headerAlign: 'center',
            align: 'center',
            formatter: requestTypeMapper,
            filterValue: (cell) => requestTypeMapper(cell),
            style: {verticalAlign: 'middle'},
            sort: true,
            sortFunc: disableSortingFunc,
            onSort: (field, order) => {
                const sortField = "type"
                setSort({field: sortField, order: order})
            },
        },
        {
            name: 'status',
            text: 'Status',
            headerAlign: 'center',
            align: 'center',
            formatter: (cell, row) => statusFormatter(cell, row, requests, showModal),
            filterValue: (cell) => requestStatusMapper(cell),
            style: {verticalAlign: 'middle'},
            sort: true,
            sortFunc: disableSortingFunc,
            onSort: (field, order) => {
                const sortField = "status"
                setSort({field: sortField, order: order})
            },
        },
        {
            name: 'actions',
            text: 'Akcje',
            headerAlign: 'center',
            formatter: actionFormatter,
            align: 'center',
            style: {verticalAlign: 'middle'},
        },
    ];

    const isLoading = requests.length === 0 && isFetching

    return (
        <>
            {
                !isLoading ?
                    <>
                        {modals}
                        <Table
                            keyField='id'
                            data={formattedRequests}
                            wrapperClasses={styles.table}
                            columns={columns}
                            hover
                        />
                        {pagination}
                    </>
                    :
                    <div className={spinnerClasses.spinner}>
                        <BeatLoader color='deepskyblue' size={50}/>
                        <h1>Pobieram dane...</h1>
                    </div>
            }
        </>
    );
}

WorkerRequestsHistoryTable.propTypes = {
    requests: PropTypes.array,
    requestsPage: PropTypes.object.isRequired,
    acceptRequest: PropTypes.func.isRequired,
    cancelRequest: PropTypes.func.isRequired,
    rejectRequest: PropTypes.func.isRequired,
    isFetching: PropTypes.bool,
    setPageNumber: PropTypes.func.isRequired,
    setSort: PropTypes.func.isRequired,
}

WorkerRequestsHistoryTable.defaultProps = {
    requests: [],
    isFetching: false,
}