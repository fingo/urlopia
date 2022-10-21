import classNames from "classnames";
import PropTypes from "prop-types";
import {useState} from "react";
import {CheckSquareFill as AcceptIcon, XSquareFill as XIcon} from "react-bootstrap-icons";
import {BeatLoader} from "react-spinners";

import spinnerClasses from '../../global-styles/loading-spinner.module.scss';
import tableClasses from '../../global-styles/table-styles.module.scss'
import {textAsArrayFormatter} from "../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {ConfirmRejectModal} from "../../pages/absence-requests-page/confirm-reject-modal/ConfirmRejectModal";
import Table from "../table/Table";

export const CompanyRequestsList = ({
    requests,
    rejectRequest,
    acceptRequest,
    isFetching,
}) => {
    const [showConfirmRejectModal, setShowConfirmRejectModal] = useState(false);
    const [rowId, setRowId] = useState(0);

    const actionFormatter = (cell, row) => {
        const acceptBtnClass = classNames(tableClasses.actionBtn, 'text-success');
        const cancelBtnClass = classNames(tableClasses.actionBtn, 'text-danger');
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
                    }}
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
            name: 'applicant',
            text: 'Wnioskodawca',
            headerAlign: 'center',
            align: 'center',
            filter: true,
            sort: true,
            style: {verticalAlign: 'middle'},
        },
        {
            name: 'examiner',
            text: 'Rozpatrujący',
            headerAlign: 'center',
            align: 'center',
            filter: true,
            sort: true,
            formatter: textAsArrayFormatter,
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
            {
                !isFetching ?
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
                    :
                    <div className={spinnerClasses.spinner}>
                        <BeatLoader color='deepskyblue' size={50}/>
                        <h1>Pobieram dane...</h1>
                    </div>
            }
        </>
    );
}

CompanyRequestsList.propTypes = {
    requests: PropTypes.array,
    rejectRequest: PropTypes.func.isRequired,
    acceptRequest: PropTypes.func.isRequired,
    isFetching: PropTypes.bool,

}

CompanyRequestsList.defaultProps = {
    requests: [],
    isFetching: false,
}