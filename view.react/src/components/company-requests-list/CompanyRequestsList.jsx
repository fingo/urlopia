import classNames from "classnames";
import PropTypes from "prop-types";
import {useState} from "react";
import {CheckSquareFill as AcceptIcon, XSquareFill as XIcon} from "react-bootstrap-icons";
import BootstrapTable from "react-bootstrap-table-next";
import filterFactory, {textFilter} from "react-bootstrap-table2-filter";
import {BeatLoader} from "react-spinners";

import {spinner} from '../../global-styles/loading-spinner.module.scss';
import {actionBtn, actions} from '../../global-styles/table-styles.module.scss'
import {textAsArrayFormatter} from "../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {tableClass} from "../../helpers/react-bootstrap-table2/tableClass";
import {ConfirmRejectModal} from "../../pages/absence-requests-page/confirm-reject-modal/ConfirmRejectModal";

export const CompanyRequestsList = ({
    requests,
    rejectRequest,
    acceptRequest,
    isFetching,
}) => {
    const [showConfirmRejectModal, setShowConfirmRejectModal] = useState(false);
    const [rowId, setRowId] = useState(0);

    const actionFormatter = (cell, row) => {
        const acceptBtnClass = classNames(actionBtn, 'text-success');
        const cancelBtnClass = classNames(actionBtn, 'text-danger');
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
            dataField: 'id',
            hidden: true,
        },
        {
            dataField: 'applicant',
            text: 'Wnioskodawca',
            headerAlign: 'center',
            align: 'center',
            filter: textFilter({
                id: 'applicantCompanyRequestListFilter',
                placeholder: 'Filtruj...',
                delay: 0,
            }),
            sort: true,
            style: {verticalAlign: 'middle'},
        },
        {
            dataField: 'examiner',
            text: 'Rozpatrujący',
            headerAlign: 'center',
            align: 'center',
            filter: textFilter({
                id: 'examinerCompanyRequestListFilter',
                placeholder: 'Filtruj...',
                delay: 0,
            }),
            sort: true,
            formatter: textAsArrayFormatter,
            style: {verticalAlign: 'middle'},
        },
        {
            dataField: 'period',
            text: 'Termin',
            headerAlign: 'center',
            align: 'center',
            sort: true,
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
        <>
            {
                !isFetching ?
                    <>
                        <BootstrapTable
                            bootstrap4
                            keyField='id'
                            data={requests}
                            wrapperClasses={tableClass}
                            columns={columns}
                            filter={filterFactory()}
                            filterPosition='top'
                            bordered={false}
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
                    <div className={spinner}>
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