import PropTypes from 'prop-types'
import {XLg as XIcon} from "react-bootstrap-icons";
import BootstrapTable from "react-bootstrap-table-next";
import filterFactory, {textFilter} from "react-bootstrap-table2-filter";

import {PENDING} from "../../constants/statuses";
import {
    requestPeriodFormatter,
    requestStatusMapper,
    requestTypeMapper
} from "../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {textAsArrayFormatter} from "../../helpers/react-bootstrap-table2/TableHelper";
import styles from "./UserRequestsList.module.scss";

export const UserRequestsTab = ({requests, cancelRequest}) => {
    const actionFormatter = (cell, row) => {
        if (row.status === PENDING) {
            return (
                <button
                    title='Anuluj wniosek'
                    className={styles.btn}
                    onClick={() => cancelRequest(row.id)}
                >
                    <XIcon/>
                </button>
            );
        }
    }

    const columns = [
        {
            dataField: 'id',
            hidden: true,
        },
        {
            dataField: 'periodInfo',
            text: 'Termin',
            headerAlign: 'center',
            align: 'center',
            formatter: textAsArrayFormatter
        },
        {
            dataField: 'type',
            text: 'Rodzaj',
            headerAlign: 'center',
            align: 'center',
            filter: textFilter({
                placeholder: 'Filtruj...',
                delay: 0,
            }),
            formatter: requestTypeMapper,
            filterValue: (cell) => requestTypeMapper(cell)
        },
        {
            dataField: 'status',
            text: 'Status',
            headerAlign: 'center',
            align: 'center',
            formatter: requestStatusMapper,
            filter: textFilter({
                placeholder: 'Filtruj...',
                delay: 0,
            }),
            filterValue: (cell) => requestStatusMapper(cell),
        },
        {
            dataField: 'actions',
            text: 'Akcje',
            headerAlign: 'center',
            formatter: actionFormatter,
            align: 'center',
        }
    ];

    const formattedRequests = requests.map(req => ({...req, periodInfo: requestPeriodFormatter(req)}))

    return (
        <BootstrapTable
            bootstrap4
            keyField='id'
            data={formattedRequests}
            wrapperClasses={`table-responsive ${styles.tableWrapper}`}
            columns={columns}
            filter={filterFactory()}
            filterPosition='top'
            bordered={false}
            hover
        />
    );
}

UserRequestsTab.propTypes = {
    requests: PropTypes.array,
    cancelRequest: PropTypes.func
}

UserRequestsTab.defaultProps = {
    requests: [],
    cancelRequest: () => {}
}