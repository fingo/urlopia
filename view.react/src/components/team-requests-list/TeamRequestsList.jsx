import classNames from "classnames";
import PropTypes from "prop-types";
import {useState} from "react";
import {CheckLg as AcceptIcon, XLg as XIcon} from "react-bootstrap-icons";

import tableClasses from '../../global-styles/table-styles.module.scss';
import {ConfirmRejectModal} from "../../pages/absence-requests-page/confirm-reject-modal/ConfirmRejectModal";
import Table from "../table/Table";

export const TeamRequestsList = ({
    requests,
    acceptRequest,
    rejectRequest,
}) => {

    const [showConfirmRejectModal, setShowConfirmRejectModal] = useState(false);
    const [rowId, setRowId] = useState(0);

    const actionFormatter = (cell, row) => {
        const cancelBtnClass = classNames(tableClasses.actionBtn, 'text-danger');
        const acceptBtnClass = classNames(tableClasses.actionBtn, 'text-success');

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
                    className={cancelBtnClass}
                    onClick={() => {
                        setRowId(row.id);
                        setShowConfirmRejectModal(true);
                        }
                    }
                >
                    <XIcon/>
                </button>
            </div>
        );
    }

    const columns = [
        {
            name: 'id',
            hidden: true,
        },
        {
            name: 'requester',
            text: 'Wnioskodawca',
            headerAlign: 'center',
            align: 'center',
            filter: true,
            sort: true,
            style: {verticalAlign: 'middle'},
        },
        {
            name: 'period',
            text: 'Termin',
            headerAlign: 'center',
            align: 'center',
            sort: true,
            style: {verticalAlign: 'middle'},
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

    return (
        <>
            <Table
                keyField='id'
                data={requests}
                columns={columns}
                hover
            />
            <ConfirmRejectModal
                show = {showConfirmRejectModal}
                onHide = {() => setShowConfirmRejectModal(false)}
                rowId = {rowId}
                rejectRequest = {rejectRequest}
            />
        </>
    );
}

TeamRequestsList.propTypes = {
    requests: PropTypes.array,
    acceptRequest: PropTypes.func.isRequired,
    rejectRequest: PropTypes.func.isRequired,
}

TeamRequestsList.defaultProps = {
    requests: [],
}
