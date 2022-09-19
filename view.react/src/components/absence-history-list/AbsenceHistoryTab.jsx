import PropTypes from "prop-types";
import {useState} from "react";

import {hoursChangeMapper} from "../../helpers/react-bootstrap-table2/HistoryLogMapperHelper";
import {disableSortingFunc} from "../../helpers/react-bootstrap-table2/utils";
import {ChangeLogCountYear} from "../change-log-count-year/ChangeLogCountYear";
import Table from "../table/Table";

export const AbsenceHistoryTab = ({logs, isHidden, vacationTypeLabel, isAdminView, setSort, setRefresh}) => {
    const [whichExpanded, setWhichExpanded] = useState([]);

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
    ];

    return (
        <Table
            keyField='id'
            data={logs}
            expandRow={isAdminView && expandRowForAdmin}
            columns={columns}
            hover
            striped={isHidden}
        />
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