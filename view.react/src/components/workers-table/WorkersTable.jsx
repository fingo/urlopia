import PropTypes from "prop-types";
import {useEffect, useState} from "react";
import {Alert} from "react-bootstrap";
import BootstrapTable from 'react-bootstrap-table-next';
import filterFactory, {textFilter} from "react-bootstrap-table2-filter";

import {changeIsEC} from "../../contexts/workers-context/actions/changeIsEC";
import {fetchAssociates} from "../../contexts/workers-context/actions/fetchAssociates";
import {fetchUnspecifiedUsers} from "../../contexts/workers-context/actions/fetchUnspecifiedUsers";
import {fetchWorkers} from "../../contexts/workers-context/actions/fetchWorkers";
import {useWorkers} from "../../contexts/workers-context/workersContext";
import {textAsArrayFormatter} from "../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {tableClass} from "../../helpers/react-bootstrap-table2/tableClass";
import {ExtendedWorker} from "../extended-worker/ExtendedWorker";
import styles from './WorkersTable.module.scss';

export const URL = '/workers';

export const WorkersTable = ({isEC}) => {
    const [workersState, workersDispatch] = useWorkers();
    const workers = isEC ? workersState.workers.workers : workersState.associates.associates;
    const selectedUser = isEC ? workersState.workers.selectedWorker : workersState.associates.selectedAssociate;
    const {unspecifiedUsers} = workersState;

    const [whichExpanded, setWhichExpanded] = useState([]);

    useEffect(() => {
        if (selectedUser) {
            setWhichExpanded([selectedUser.userId]);
        } else {
            setWhichExpanded([]);
        }
    }, [selectedUser]);

    useEffect(() => {
        workersDispatch(changeIsEC(isEC));
        fetchUnspecifiedUsers(workersDispatch);
        if (isEC) {
            fetchWorkers(workersDispatch);
        } else {
            fetchAssociates(workersDispatch);
        }
    }, [workersDispatch, isEC]);

    const expandRow = {
        onlyOneExpanding: true,
        onExpand: (row, isExpand) => {
            if (!isExpand) {
                setWhichExpanded([]);
            } else {
                setWhichExpanded([row.userId]);
            }
        },
        expanded: whichExpanded,
        renderer: row => (
            <ExtendedWorker workTime={row.workTime} userId={row.userId}/>
        )
    };

    const formattedWorkers = workers.map(worker => {
        const workTime = `${worker.workTime.numerator}/${worker.workTime.denominator}`;
        return {
            userId: worker.userId,
            fullName: worker.fullName,
            mailAddress: worker.mailAddress,
            teams: worker.teams,
            workTime,
        }
    });

    const columns = [
        {
            dataField: 'userId',
            hidden: true,
        },
        {
            dataField: 'fullName',
            text: 'Imię i nazwisko',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle', cursor: 'pointer'},
            filter: textFilter({
                placeholder: 'Filtruj...',
                delay: 0,
            }),
            sort: true,
        },
        {
            dataField: 'mailAddress',
            text: 'E-mail',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle', cursor: 'pointer'},
            filter: textFilter({
                placeholder: 'Filtruj...',
                delay: 0,
            }),
            sort: true,
        },
        {
            dataField: 'teams',
            text: 'Zespół',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle', cursor: 'pointer'},
            filter: textFilter({
                placeholder: 'Filtruj...',
                delay: 0,
            }),
            formatter: textAsArrayFormatter,
            sort: true,
        },
        {
            dataField: 'workTime',
            text: 'Etat',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle', cursor: 'pointer'},
            filter: textFilter({
                placeholder: 'Filtruj...',
                delay: 0,
            }),
            sort: true,
        },
    ];

    return (
        <div className={styles.main}>
            {
                unspecifiedUsers.length > 0 &&
                <Alert variant='danger' className={styles.unspecifiedUsersAlert}>
                    <p>Użytkownicy, którzy nie są ani na etacie, ani na B2B:</p>
                    {
                        unspecifiedUsers.map((user, i) =>
                            <strong key={i}>
                            <span key={i}>
                                {i > 0 && ", "}
                                {user.fullName}
                            </span>
                            </strong>
                        )
                    }
                </Alert>
            }
            <h1 className='text-center'>{isEC ? 'Pracownicy' : 'Współpracownicy'}</h1>
            <BootstrapTable
                bootstrap4
                keyField='userId'
                data={formattedWorkers}
                wrapperClasses={tableClass}
                columns={columns}
                expandRow={expandRow}
                filter={filterFactory()}
                filterPosition='top'
                bordered={false}
            />
        </div>
    );
};

WorkersTable.propTypes = {
    isEC: PropTypes.bool,
}

WorkersTable.defaultProps = {
    isEC: true,
}