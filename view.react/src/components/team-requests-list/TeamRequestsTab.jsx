import classNames from "classnames";
import PropTypes from "prop-types";
import {CheckLg as AcceptIcon, XLg as XIcon} from "react-bootstrap-icons";
import BootstrapTable from "react-bootstrap-table-next";
import filterFactory, {textFilter} from "react-bootstrap-table2-filter";

import {textAsArrayFormatter} from "../../helpers/react-bootstrap-table2/TableHelper";
import styles from "./TeamRequestsList.module.scss";

export const TeamRequestsTab = ({requests, acceptRequest, rejectRequest}) => {
    const actionFormatter = (cell, row) => {
        const cancelBtnClass = classNames(styles.btn, 'text-danger');
        const acceptBtnClass = classNames(styles.btn, 'text-success');
        return (
            <div className={styles.actions}>
                <button
                    title='Zaakceptuj wniosek'
                    className={acceptBtnClass}
                    onClick={() => acceptRequest(row.id)}
                >
                    <AcceptIcon/>
                </button>

                <button
                    title='Odrzuć wniosek'
                    className={cancelBtnClass}
                    onClick={() => rejectRequest(row.id)}
                >
                    <XIcon/>
                </button>
            </div>
        );
    }

    const columns = [
        {
            dataField: 'id',
            hidden: true,
        },
        {
            dataField: 'requester',
            text: 'Wnioskodawca',
            headerAlign: 'center',
            align: 'center',
            filter: textFilter({
                id: 'requesterTeamRequestListFilter',
                placeholder: 'Filtruj...',
                delay: 0,
            }),
            sort: true,
            style: {verticalAlign: 'middle'},
        },
        {
            dataField: 'period',
            text: 'Termin',
            headerAlign: 'center',
            align: 'center',
            sort: true,
            formatter: textAsArrayFormatter,
            style: {verticalAlign: 'middle'},
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
}

TeamRequestsTab.propTypes = {
    requests: PropTypes.array,
    acceptRequest: PropTypes.func.isRequired,
    rejectRequest: PropTypes.func.isRequired,
}

TeamRequestsTab.defaultProps = {
    requests: [],
}
