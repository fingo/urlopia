import PropTypes from "prop-types";
import {useState} from "react";
import {TrashFill as TrashIcon} from "react-bootstrap-icons";

import {DELETE_USER_HISTORY_LOG_URL} from "../../contexts/absence-history-context/constants";
import {hoursChangeMapper} from "../../helpers/react-bootstrap-table2/HistoryLogMapperHelper";
import {disableSortingFunc} from "../../helpers/react-bootstrap-table2/utils";
import {sendDeleteRequest} from "../../helpers/RequestHelper";
import {ChangeLogCountYear} from "../change-log-count-year/ChangeLogCountYear";
import Table from "../table/Table";
import styles from "./AbsenceHistoryTab.module.scss"
import {ConfirmRemoveHistoryLogModal} from "./confirm-remove-history-log-modal/ConfirmRemoveHistoryLogModal";
export const AbsenceHistoryTab = ({logs, isHidden, vacationTypeLabel, isAdminView, setSort, setRefresh}) => {
    const [whichExpanded, setWhichExpanded] = useState([]);
    const [showConfirmRemoveHistoryLogModal, setShowConfirmRemoveHistoryLogModal] = useState(false)
    const [logId, setLogId] = useState(0);

    const expandRowForAdmin = {
        onlyOneExpanding: true,
        onExpand: (row, isExpand) => {
            if (!isExpand) {
                setWhichExpanded([]);
            } else {
                setWhichExpanded([row.id]);
            }
        },
        expanded: whichExpanded,
        renderer: row => (
                <ChangeLogCountYear
                    isAdminView={isAdminView}
                    countForNextYear={!row.countForNextYear}
                    historyLogId={row.id}
                    setRefresh={setRefresh}
                />
            )
    };


    const actionFormatter = (cell, row) => {

        const historyLog = {...row}
        return (
            <>
                {historyLog.userDetailsChangeEvent && <div className={styles.actionButtonsContainer}>
                    <button onClick={() => {
                        setLogId(historyLog.id);
                        setShowConfirmRemoveHistoryLogModal(true);
                    }}>
                        <TrashIcon className={styles.removeButton} size={20}/>
                    </button>
                </div>}
            </>
        )
    }

    const deleteHistoryLog = (historyLogId) => {
        sendDeleteRequest(`${DELETE_USER_HISTORY_LOG_URL}/${historyLogId}`)
            .then(() => setRefresh())
            .catch(error => {
                console.log('err: ', error);
            });
    }

    const columns = [
        {
            name: 'id',
            hidden: true
        },
        {
            name: 'userWorkTime',
            hidden: true
        },
        {
            name: 'created',
            text: 'Utworzono',
            headerAlign: 'center',
            align: 'center',
            sort: true,
            sortFunc: disableSortingFunc,
            onSort: (field, order) => {
                setSort({field: field, order: order})
            },
            style: {verticalAlign: 'middle'},
            formatter: (cell, row) => {
                if (typeof row.hours == 'string' && row.hours.toLowerCase().includes("etat")) {
                    return ""
                }
                return cell;
            },
            hideHeader: isHidden

        },
        {
            name: 'deciderFullName',
            text: 'Rozpatrujący',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle'},
            hidden: isHidden
        },
        {
            name: 'hours',
            text: 'Wartość zmiany',
            headerAlign: 'center',
            align: 'center',
            style: function hoursChangeColor(cell) {
                if (typeof cell == 'number') {
                    let color;
                    switch (true) {
                        case cell > 0:
                            color = "green"
                            break;
                        case cell < 0:
                            color = "red"
                            break;
                        default:
                            color = "orange"
                            break;
                    }
                    return {color: color, verticalAlign: 'middle'};
                } else {
                    return {verticalAlign: 'middle', fontWeight: 'bold'}
                }
            },
            formatter: (cell, row) => hoursChangeMapper(cell, row.userWorkTime),
            hideHeader: isHidden
        },
        {
            name: "hoursRemaining",
            text: vacationTypeLabel,
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle'},
            hidden: isHidden,
            formatter: (cell, row) => hoursChangeMapper(cell, row.userWorkTime)
        },
        {
            name: "comment",
            text: 'Komentarz',
            headerAlign: 'center',
            align: 'center',
            sort: true,
            sortFunc: disableSortingFunc,
            onSort: (field, order) => {
                setSort({field, order})
            },
            style: {verticalAlign: 'middle'},
            hideHeader: isHidden

        },
        {
            name: 'countForNextYear',
            hidden: true
        },
        {
            name: 'action',
            text: 'Akcja',
            headerAlign: 'center',
            align: 'center',
            formatter: actionFormatter,
            style: {verticalAlign: 'middle'},
            hidden: isHidden
        },
    ];

    return (
        <>
            <Table
                keyField='id'
                data={logs}
                expandRow={isAdminView && expandRowForAdmin}
                columns={columns}
                hover
                striped={isHidden}
            />
            <ConfirmRemoveHistoryLogModal show={showConfirmRemoveHistoryLogModal}
                                          onHide={() => setShowConfirmRemoveHistoryLogModal(false)}
                                          historyLogId={logId}
                                          removeHistoryLog={deleteHistoryLog}/>
        </>

    );
}

AbsenceHistoryTab.propTypes = {
    logs: PropTypes.array,
    isHidden: PropTypes.bool,
    vacationTypeLabel: PropTypes.string,
    isAdminView: PropTypes.bool,
    setSort: PropTypes.func,
    setRefresh: PropTypes.func,
}

AbsenceHistoryTab.defaultProps = {
    logs: [],
    isHidden: false,
    isAdminView: false,
    vacationTypeLabel: "Pozostały urlop",
    setSort: () => {},
    setRefresh: () => {}
}