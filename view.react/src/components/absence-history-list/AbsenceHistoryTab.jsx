import PropTypes from "prop-types";
import BootstrapTable from 'react-bootstrap-table-next';

import styles from '../../global-styles/AbsenceHistoryList.module.scss'
import {hoursChangeMapper} from "../../helpers/react-bootstrap-table2/HistoryLogMapperHelper";
import {tableClass} from "../../helpers/react-bootstrap-table2/tableClass";

export const AbsenceHistoryTab = ({logs, isHidden}) => {

    const columns = [
        {
            dataField: 'id',
            hidden: true
        },
        {
            dataField: 'userWorkTime',
            hidden: true
        },
        {
            dataField: 'created',
            text: 'Utworzono',
            headerAlign: 'center',
            align: 'center',
            sort: true,
            style: {verticalAlign: 'middle'},
            headerAttrs: {
                hidden: isHidden
            },
        },
        {
            dataField: 'deciderFullName',
            text: 'Rozpatrujący',
            headerAlign: 'center',
            align: 'center',
            sort: true,
            style: {verticalAlign: 'middle'},
            hidden: isHidden
        },
        {
            dataField: 'hours',
            text: 'Wartość zmiany',
            headerAlign: 'center',
            align: 'center',
            sort: true,
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
            headerAttrs: {
                hidden: isHidden
            }
        },
        {
            dataField: "hoursRemaining",
            text: 'Pozostały urlop',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle'},
            sort: true,
            hidden: isHidden,
            formatter: (cell, row) => hoursChangeMapper(cell, row.userWorkTime)
        },
        {
            dataField: "comment",
            text: 'Komentarz',
            headerAlign: 'center',
            sort: true,
            align: 'center',
            style: {verticalAlign: 'middle'},
            headerAttrs: {
                hidden: isHidden
            }
        }
    ];

    return (
        <BootstrapTable
            bootstrap4
            keyField='id'
            data={logs}
            wrapperClasses={tableClass}
            columns = {columns}
            bordered={false}
            hover
            striped={isHidden}
        />
    );
}

AbsenceHistoryTab.propTypes = {
    logs: PropTypes.array,
    isHidden: PropTypes.bool,
}

AbsenceHistoryTab.defaultProps = {
    logs: [],
    isHidden: false,
}