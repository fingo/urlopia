import classNames from "classnames";
import PropTypes from "prop-types";
import {useState} from "react";
import {CheckSquareFill as AcceptIcon, XSquareFill as XIcon} from "react-bootstrap-icons";
import BootstrapTable from "react-bootstrap-table-next";
import filterFactory, {textFilter} from "react-bootstrap-table2-filter";
import {BeatLoader} from "react-spinners";

import {ACCEPTED, PENDING} from "../../../../constants/statuses";
import {spinner} from '../../../../global-styles/loading-spinner.module.scss';
import {actionBtn, actions} from '../../../../global-styles/table-styles.module.scss';
import {
    requestPeriodFormatter,
    requestStatusMapper, requestTypeMapper,
    statusFormatter,
    textAsArrayFormatter
} from "../../../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {tableClass} from "../../../../helpers/react-bootstrap-table2/tableClass";
import {AcceptancesModal} from "../../../user-requests-list/AcceptancesModal";
import styles from './WorkerRequestsHistoryTable.module.scss';

export const WorkerRequestsHistoryTable = ({
    requests,
    acceptRequest,
    cancelRequest,
    rejectRequest,
    isFetching,
}) => {
    const [modalsShow, setModalsShow] = useState({})

    const actionFormatter = (cell, row) => {
        const acceptBtnClass = classNames(actionBtn, 'text-success');
        const cancelBtnClass = classNames(actionBtn, 'text-warning');
        const rejectBtnClass = classNames(actionBtn, 'text-danger');
        if (row.status === PENDING) {
            return (
                <div className={actions}>
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
                <div className={actions}>
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
            dataField: 'id',
            hidden: true,
        },
        {
            dataField: 'period',
            text: 'Termin',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle'},
            sort: true,
        },
        {
            dataField: 'examiner',
            text: 'Rozpatrujący',
            headerAlign: 'center',
            align: 'center',
            filter: textFilter({
                id: 'examinerWorkerRequestsHistoryTableFilter',
                placeholder: 'Filtruj...',
                delay: 0,
            }),
            formatter: textAsArrayFormatter,
            style: {verticalAlign: 'middle'},
            sort: true,
        },
        {
            dataField: 'type',
            text: 'Rodzaj',
            headerAlign: 'center',
            align: 'center',
            formatter: requestTypeMapper,
            filter: textFilter({
                id: 'typeWorkerRequestsHistoryTableFilter',
                placeholder: 'Filtruj...',
                delay: 0,
            }),
            filterValue: (cell) => requestTypeMapper(cell),
            style: {verticalAlign: 'middle'},
            sort: true,
        },
        {
            dataField: 'status',
            text: 'Status',
            headerAlign: 'center',
            align: 'center',
            formatter: (cell, row) => statusFormatter(cell, row, requests, showModal),
            filter: textFilter({
                id: 'statusWorkerRequestsHistoryTableFilter',
                placeholder: 'Filtruj...',
                delay: 0,
            }),
            filterValue: (cell) => requestStatusMapper(cell),
            style: {verticalAlign: 'middle'},
            sort: true,
        },
        {
            dataField: 'actions',
            text: 'Akcje',
            headerAlign: 'center',
            formatter: actionFormatter,
            align: 'center',
            style: {verticalAlign: 'middle'},
        },
    ];

    const tableWrapperClass = classNames(tableClass, styles.table);
    return (
        <>
            {
                !isFetching ?
                    <>
                        {modals}
                        <BootstrapTable
                            bootstrap4
                            keyField='id'
                            data={formattedRequests}
                            wrapperClasses={tableWrapperClass}
                            columns={columns}
                            filter={filterFactory()}
                            filterPosition='top'
                            bordered={false}
                            hover
                        />
                    </>
                    :
                    <div className={spinner}>
                        <BeatLoader color='deepskyblue' size={50}/>
                        <h1>Pobieram dane...</h1>
                    </div>
            }
        </>
    );
}

WorkerRequestsHistoryTable.propTypes = {
    requests: PropTypes.array,
    acceptRequest: PropTypes.func.isRequired,
    cancelRequest: PropTypes.func.isRequired,
    rejectRequest: PropTypes.func.isRequired,
    isFetching: PropTypes.bool,
}

WorkerRequestsHistoryTable.defaultProps = {
    requests: [],
    isFetching: false,
}