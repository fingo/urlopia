import PropTypes from "prop-types";
import {useEffect, useState} from "react";
import {Alert} from "react-bootstrap";
import BootstrapTable from 'react-bootstrap-table-next';
import filterFactory, {textFilter} from "react-bootstrap-table2-filter";
import {BeatLoader} from "react-spinners";

import {fetchUsersPresenceConfirmations} from "../../contexts/presence-context/actions/fetchUsersPresenceConfirmations";
import {usePresence} from "../../contexts/presence-context/presenceContext";
import {changeIsEC} from "../../contexts/workers-context/actions/changeIsEC";
import {fetchAssociates} from "../../contexts/workers-context/actions/fetchAssociates";
import {fetchNoActionWorkers} from "../../contexts/workers-context/actions/fetchNoActionWorkers";
import {fetchUnspecifiedUsers} from "../../contexts/workers-context/actions/fetchUnspecifiedUsers";
import {fetchWorkers} from "../../contexts/workers-context/actions/fetchWorkers";
import {useWorkers} from "../../contexts/workers-context/workersContext";
import {spinner} from "../../global-styles/loading-spinner.module.scss";
import {AttentionIcon, TextWithIcon} from "../../helpers/icons/Icons";
import {textAsArrayFormatter} from "../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {tableClass} from "../../helpers/react-bootstrap-table2/tableClass";
import {ExtendedWorker} from "../extended-worker/ExtendedWorker";
import styles from './WorkersTable.module.scss';

export const URL = '/workers';

export const WorkersTable = ({isEC}) => {
    const [, presenceDispatcher] = usePresence();

    const [workersState, workersDispatch] = useWorkers();
    const workers = isEC ? workersState.workers.workers : workersState.associates.associates;
    const selectedUser = isEC ? workersState.workers.selectedWorker : workersState.associates.selectedAssociate;
    const unspecifiedAbsences = isEC && workersState.workers.unspecifiedAbsences;
    const {unspecifiedUsers} = workersState;
    const {areUnspecifiedAbsencesFetched} = workersState.workers;
    const {fetching} = isEC ? workersState.workers : workersState.associates;

    const [whichExpanded, setWhichExpanded] = useState([]);
    const [usersIdWithUnspecifiedAbsences, setUsersIdWithUnspecifiedAbsences] = useState([]);

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
        fetchNoActionWorkers(workersDispatch);
        fetchUsersPresenceConfirmations(presenceDispatcher);
        if (isEC) {
            fetchWorkers(workersDispatch);
        } else {
            fetchAssociates(workersDispatch);
        }
    }, [workersDispatch, isEC, presenceDispatcher]);

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

    const withNotifyFormatter = (cell, row) => {
        const usersKeys = Object.keys(unspecifiedAbsences);
        setUsersIdWithUnspecifiedAbsences(usersKeys);
        if (usersKeys.includes(row.userId.toString())) {
            return (
                <div className={styles.notify}>
                    <div className={styles.dot}>
                        <TextWithIcon
                            text=''
                            icon={<AttentionIcon/>}
                            showIcon={true}
                        />
                    </div>
                    {cell}
                </div>
            );
        } else {
            return cell;
        }

    }

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
            <ExtendedWorker workTime={row.workTime}
                            userId={row.userId}
                            isUnspecifiedAbsences={usersIdWithUnspecifiedAbsences.includes(row.userId.toString())}/>
        )
    };

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
            formatter: (cell, row) => withNotifyFormatter(cell, row),
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
            text: 'Zespoły',
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
            hidden: !isEC,
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

    if (fetching) {
        return (
            <div className={spinner}>
                <BeatLoader color='deepskyblue' size={50}/>
                <h1>Pobieram dane...</h1>
            </div>
        );
    }

    if (isEC) {
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
                <h1 className='text-center'>Pracownicy</h1>
                {
                    areUnspecifiedAbsencesFetched ?
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
                        :
                        <div className={spinner}>
                            <BeatLoader color='deepskyblue' size={50}/>
                            <h1>Pobieram dane...</h1>
                        </div>
                }
            </div>
        )
    }

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
            <h1 className='text-center'>Współpracownicy</h1>
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
    )
};

WorkersTable.propTypes = {
    isEC: PropTypes.bool,
}

WorkersTable.defaultProps = {
    isEC: true,
}