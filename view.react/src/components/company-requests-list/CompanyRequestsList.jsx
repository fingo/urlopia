import classNames from "classnames";
import PropTypes from "prop-types";
import {CheckLg as AcceptIcon, XLg as XIcon} from "react-bootstrap-icons";
import BootstrapTable from 'react-bootstrap-table-next';
import filterFactory, {textFilter} from 'react-bootstrap-table2-filter';

import styles from './CompanyRequestsList.module.scss';

export const CompanyRequestsList = ({requests}) => {
    const textAsArrayFormatter = (cell) => {
        return cell.map(elem => {
            return (
                <p key={`elem-${elem}`}>
                    {elem}
                </p>
            );
        });
    }

    const actionFormatter = () => {
        const cancelBtnClass = classNames(styles.btn, 'text-danger');
        const acceptBtnClass = classNames(styles.btn, 'text-success');
        return (
            <div className={styles.actions}>
                <button title='Zaakceptuj wniosek' className={acceptBtnClass}>
                    <AcceptIcon/>
                </button>

                <button title='Anuluj wniosek' className={cancelBtnClass}>
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
            filter: textFilter({
                id: 'periodCompanyRequestListFilter',
                placeholder: 'Filtruj...',
                delay: 0,
            }),
            sort: true,
            formatter: textAsArrayFormatter,
            style: {verticalAlign: 'middle'},
        },
        {
            dataField: 'type',
            text: 'Rodzaj',
            headerAlign: 'center',
            align: 'center',
            filter: textFilter({
                id: 'typeCompanyRequestListFilter',
                placeholder: 'Filtruj...',
                delay: 0,
            }),
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
        <div className={styles.main}>
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
        </div>

    );
};

CompanyRequestsList.propTypes = {
    requests: PropTypes.array,
}

CompanyRequestsList.defaultProps = {
    requests: [],
}
