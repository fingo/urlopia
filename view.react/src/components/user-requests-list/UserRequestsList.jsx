import PropTypes from 'prop-types';
import {XLg as XIcon} from "react-bootstrap-icons";
import BootstrapTable from 'react-bootstrap-table-next';
import filterFactory, {textFilter} from 'react-bootstrap-table2-filter';

import {PENDING} from "../../constants/statuses";
import {statusMapper} from "../../helpers/react-bootstrap-table2/StatusMapperHelper";
import styles from './UserRequestsList.module.scss';

export const UserRequestsList = ({requests}) => {
    const actionFormatter = (cell, row) => {
        if (row.status === PENDING) {
            return (
                <button title='Anuluj wniosek' className={styles.btn}>
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
            dataField: 'period',
            text: 'Termin',
            headerAlign: 'center',
            align: 'center',
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
        },
        {
            dataField: 'status',
            text: 'Status',
            headerAlign: 'center',
            align: 'center',
            formatter: statusMapper,
            filter: textFilter({
                placeholder: 'Filtruj...',
                delay: 0,
            }),
            filterValue: (cell) => statusMapper(cell),
        },
        {
            dataField: 'actions',
            text: 'Akcje',
            headerAlign: 'center',
            formatter: actionFormatter,
            align: 'center',
        }
    ];

    return (
        <BootstrapTable
            bootstrap4
            keyField='id'
            data={requests}
            wrapperClasses={`table-responsive ${styles.tableWrapper}`}
            columns={columns}
            filter={filterFactory()}
            filterPosition='top'
            bordered={false}
            hover
        />
    );
};

UserRequestsList.propTypes = {
    requests: PropTypes.array,
}

UserRequestsList.defaultProps = {
    requests: [],
}