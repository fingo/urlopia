import PropTypes from "prop-types";
import {useEffect, useState} from "react";
import {Alert, Button} from "react-bootstrap";
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
import {btnClass} from "../../global-styles/btn.module.scss";
import {spinner} from "../../global-styles/loading-spinner.module.scss";
import {AttentionIcon, TextWithIcon} from "../../helpers/icons/Icons";
import {textAsArrayFormatter} from "../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {tableClass} from "../../helpers/react-bootstrap-table2/tableClass";
import {sortedUsers} from "../../helpers/sorts/UsersSortHelper";
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
    const [showActive, setShowActive] = useState(true);

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
        fetchUnspecifiedUsers(workersDispatch,showActive);
        fetchNoActionWorkers(workersDispatch, showActive);
        fetchUsersPresenceConfirmations(presenceDispatcher);
        if (isEC) {
            fetchWorkers(workersDispatch,showActive);
        } else {
            fetchAssociates(workersDispatch,showActive);
        }
    }, [workersDispatch, isEC, showActive, presenceDispatcher]);

    const formattedWorkers = sortedUsers(workers, "fullName").map(worker => {
        const workTime = `${worker.workTime.numerator}/${worker.workTime.denominator}`;
        return {
            userId: worker.userId,
            fullName: worker.fullName,
            mailAddress: worker.mailAddress,
            teams: worker.teams,
            workTime,
        }
    });

    const handleClick = active => {
        setShowActive(!active)
    }

    const getButtonMessage = (EC,isActiveShowed) => {
        const sufix = EC? "pracowników": "współpracowników"
        return isActiveShowed? `Pokaż nieaktywnych ${sufix}`:`Pokaż aktywnych ${sufix}`;

    }

    const withNotifyFormatter = (cell, row) => {
        if (unspecifiedAbsences[row.userId] !== undefined) {
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
                            isUnspecifiedAbsences={unspecifiedAbsences[row.userId] !== undefined}/>
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
                <Button
                    className={btnClass}
                    onClick={() => handleClick(showActive)}
                >
                    {getButtonMessage(isEC,showActive)}
                </Button>
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
            <Button
                className={btnClass}
                onClick={() => handleClick(showActive)}
            >
                {getButtonMessage(isEC,showActive)}
            </Button>
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