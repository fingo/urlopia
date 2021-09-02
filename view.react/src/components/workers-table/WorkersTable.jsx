import {useEffect, useState} from "react";
import BootstrapTable from 'react-bootstrap-table-next';
import filterFactory, {textFilter} from "react-bootstrap-table2-filter";

import {fetchWorkers} from "../../contexts/workers-context/actions/fetchWorkers";
import {useWorkers} from "../../contexts/workers-context/workersContext";
import {textAsArrayFormatter} from "../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {tableClass} from "../../helpers/react-bootstrap-table2/tableClass";
import {ExtendedWorker} from "../extended-worker/ExtendedWorker";
import styles from './WorkersTable.module.scss';

export const URL = '/workers';

export const WorkersTable = () => {
    const [workersState, workersDispatch] = useWorkers();
    const {workers, selectedUser} = workersState;

    const [whichExpanded, setWhichExpanded] = useState([]);

    useEffect(() => {
        if (selectedUser) {
            setWhichExpanded([selectedUser.userId]);
        } else {
            setWhichExpanded([]);
        }
    }, [selectedUser]);

    useEffect(() => {
        fetchWorkers(workersDispatch);
    }, [workersDispatch]);

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
            <h1 className='text-center'>Pracownicy</h1>
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
