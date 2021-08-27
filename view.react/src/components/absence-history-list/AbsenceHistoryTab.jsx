import BootstrapTable from 'react-bootstrap-table-next';

import {hoursChangeMapper} from "../../helpers/react-bootstrap-table2/HistoryLogMapperHelper";
import styles from './AbsenceHistoryList.module.scss'

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
            style: function hoursChangColor(cell) {
                let color = "orange";
                switch(true) {
                    case cell > 0:
                        color = "green"
                        break;
                    case cell < 0:
                        color = "red"
                        break;
                    default:
                        break;
                }
                return {color: color, verticalAlign: 'middle'};
            },
            formatter: (cell, row) => hoursChangeMapper(cell, row.userWorkTime)

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
            style: {verticalAlign: 'middle'}
        }
    ];

    return (
        <BootstrapTable
            bootstrap4
            keyField='id'
            data={logs}
            wrapperClasses={`table-responsive ${styles.tableWrapper}`}
            columns = {columns}
            bordered={false}
            hover
        />
    );

}