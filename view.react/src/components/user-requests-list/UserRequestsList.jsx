import classNames from "classnames";
import PropTypes from 'prop-types'
import {XSquareFill as XIcon} from "react-bootstrap-icons";
import BootstrapTable from "react-bootstrap-table-next";
import filterFactory, {textFilter} from "react-bootstrap-table2-filter";
import {BeatLoader} from "react-spinners";

import {PENDING} from "../../constants/statuses";
import {spinner} from '../../global-styles/loading-spinner.module.scss';
import {actionBtn} from '../../global-styles/table-styles.module.scss';
import {
    requestPeriodFormatter,
    requestStatusMapper,
    requestTypeMapper,
    statusFormatter
} from "../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {tableClass} from "../../helpers/react-bootstrap-table2/tableClass";

export const UserRequestsList = ({
    requests,
    cancelRequest,
    isFetching,
}) => {
    const actionFormatter = (cell, row) => {
        const cancelBtnClass = classNames(actionBtn, 'text-warning');
        if (row.status === PENDING) {
            return (
                <button
                    title='Anuluj wniosek'
                    className={cancelBtnClass}
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
            style: {verticalAlign: 'middle'},
        },
        {
            dataField: 'type',
            text: 'Rodzaj',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle'},
            filter: textFilter({
                id: 'typeUserRequestsListFilter',
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
            style: {verticalAlign: 'middle'},
            formatter: statusFormatter,
            filter: textFilter({
                id: 'statusUserRequestsListFilter',
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
            style: {verticalAlign: 'middle'},
        }
    ];


    const formattedRequests = requests.map(req => ({...req, periodInfo: requestPeriodFormatter(req)}))

    return (
        <>
            {
                !isFetching ?
                    requests.length ?
                        <BootstrapTable
                            bootstrap4
                            keyField='id'
                            data={formattedRequests}
                            wrapperClasses={tableClass}
                            columns={columns}
                            filter={filterFactory()}
                            filterPosition='top'
                            bordered={false}
                            hover
                        />
                        :
                        <h1 className={spinner}>Tabela jest pusta...</h1>
                    :
                    <div className={spinner}>
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