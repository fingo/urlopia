import React, {FunctionComponent} from "react";

import useGetAutomaticVacationDaysQuery
    from "../../api/queryHooks/queries/AutomaticVacationDays/useGetAutomaticVacationDaysQuery";
import {AttentionIcon, TextWithIcon} from "../../helpers/icons/Icons";
import {Spinner} from "../spinner/Spinner";
import Table from "../table/Table";
import {ColumnType, RowType} from "../table/Table.types";
import styles from './AutomaticVacationDaysTable.module.scss';

export const AutomaticVacationDaysTable: FunctionComponent = () => {

    const {data, isFetching} = useGetAutomaticVacationDaysQuery()

    const withNotifyFormatter = (cell: any, row: RowType<any>) => {
        const {nextYearProposition} = row
        if ( nextYearProposition === 0) {
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

    const workTimeFormatter = (cell: any, row: RowType<any>) => {
        const {isEc, workTime} = row
        if (isEc) {
            return workTime === 8 ? `1` : `${workTime / 8}`;
        } else {
            return ''
        }
    }

    const nextYearPropositionFormatter = (cell: any, row: RowType<any>) => {
        const {nextYearProposition, workTime} = row
        if (workTime === 8) {
            return `${nextYearProposition/8.0}d`
        } else {
            return `${nextYearProposition}h`
        }
    }

    //todo : do sth with any
    const columns: ColumnType<any>[] = [
        {
            name: 'userId',
            hidden: true,
        },
        {
            name: 'userFullName',
            text: 'Imię i nazwisko',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle', cursor: 'pointer'},
            filter: true,
            sort: true,
            formatter: (cell, row) => withNotifyFormatter(cell, row),
        },
        {
            name: 'workTime',
            text: 'Etat',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle', cursor: 'pointer'},
            filter: true,
            sort: true,
            formatter: (cell, row) => workTimeFormatter(cell, row)
        },
        {
            name: 'nextYearProposition',
            text: 'Propozycja na kolejny rok',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle', cursor: 'pointer'},
            filter: true,
            sort: true,
            formatter: (cell, row) => nextYearPropositionFormatter(cell, row)
        },
        {
            name: 'nextYearDaysBase',
            text: 'Wymiar urlopu na nowy rok',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle', cursor: 'pointer'},
            filter: true,
            sort: true,
        },
    ];

    if (isFetching) {
        return (
                <Spinner waitMessage={`Pobieram dane...`} />
            )
    }

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>Dni na nowy rok</h1>
            <Table
                keyField='userId'
                data={data || []}
                columns={columns}
            />
        </div>
    )
}