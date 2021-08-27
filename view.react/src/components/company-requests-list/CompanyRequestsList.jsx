import classNames from "classnames";
import PropTypes from "prop-types";
import {CheckLg as AcceptIcon, XLg as XIcon} from "react-bootstrap-icons";
import BootstrapTable from "react-bootstrap-table-next";
import filterFactory, {textFilter} from "react-bootstrap-table2-filter";
import {BeatLoader} from "react-spinners";

import {spinner} from '../../global-styles/loading-spinner.module.scss';
import {actionBtn, actions} from '../../global-styles/table-styles.module.scss'
import {examinerFormatter} from "../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {tableClass} from "../../helpers/react-bootstrap-table2/tableClass";

export const CompanyRequestsList = ({
    requests,
    rejectRequest,
    acceptRequest,
    isFetching,
}) => {
    const actionFormatter = (cell, row) => {
        const cancelBtnClass = classNames(actionBtn, 'text-danger');
        const acceptBtnClass = classNames(actionBtn, 'text-success');
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
                    title='Anuluj wniosek'
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
            formatter: examinerFormatter,
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
                    requests.length ?
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