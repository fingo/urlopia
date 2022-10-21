import classNames from "classnames";
import PropTypes from 'prop-types'
import {useState} from "react";
import {XSquareFill as XIcon} from "react-bootstrap-icons";
import {BeatLoader} from "react-spinners";

import {PENDING} from "../../constants/statuses";
import {useVacationDays} from "../../contexts/vacation-days-context/vacationDaysContext";
import spinnerClasses from '../../global-styles/loading-spinner.module.scss';
import tableClasses from '../../global-styles/table-styles.module.scss';
import {
    requestPeriodFormatter,
    requestStatusMapper,
    requestTypeMapper,
    statusFormatter
} from "../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {updateVacationDays} from "../../helpers/updateVacationDays";
import Table from "../table/Table";
import {AcceptancesModal} from "./AcceptancesModal";

export const UserRequestsList = ({
    requests,
    cancelRequest,
    isFetching,
}) => {
    const [modalsShow, setModalsShow] = useState({});

    const [, vacationDaysDispatch] = useVacationDays();

    const actionFormatter = (cell, row) => {
        const cancelBtnClass = classNames(tableClasses.actionBtn, 'text-warning');
        if (row.status === PENDING) {
            return (
                <button
                    title='Anuluj wniosek'
                    className={cancelBtnClass}
                    onClick={async () => {
                        await cancelRequest(row.id);
                        updateVacationDays(vacationDaysDispatch);
                    }}
                >
                    <XIcon/>
                </button>
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

    const columns = [
        {
            name: 'id',
            hidden: true,
        },
        {
            name: 'periodInfo',
            text: 'Termin',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle'},
        },
        {
            name: 'type',
            text: 'Rodzaj',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle'},
            filter: true,
            formatter: requestTypeMapper,
            filterValue: (cell) => requestTypeMapper(cell)
        },
        {
            name: 'status',
            text: 'Status',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle'},
            formatter: (cell, row) => statusFormatter(cell, row, requests, showModal),
            filter: true,
            filterValue: (cell) => requestStatusMapper(cell),
        },
        {
            name: 'actions',
            text: 'Akcje',
            headerAlign: 'center',
            formatter: actionFormatter,
            align: 'center',
            style: {verticalAlign: 'middle'},
        }
    ];


    const formattedRequests = requests.map(req => ({...req, periodInfo: requestPeriodFormatter(req)}))

    return (
        <>
            {
                !isFetching ?
                    <>
                        {modals}
                        <Table
                            keyField='id'
                            data={formattedRequests}
                            columns={columns}
                            bordered={false}
                            hover
                        />
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

UserRequestsList.propTypes = {
    requests: PropTypes.array,
    cancelRequest: PropTypes.func.isRequired,
    isFetching: PropTypes.bool,
}

UserRequestsList.defaultProps = {
    requests: [],
    isFetching: false,
}