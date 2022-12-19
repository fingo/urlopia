import React, {FunctionComponent, useState} from "react";

import useGetAutomaticVacationDaysQuery
    from "../../api/queryHooks/queries/AutomaticVacationDays/useGetAutomaticVacationDaysQuery";
import useUpdateAutomaticVacationDaysQuery
    from "../../api/queryHooks/queries/AutomaticVacationDays/useUpdateAutomaticVacationDaysQuery";
import {
    IAutomaticVacationDaysResponse,
    IAutomaticVacationDaysRow,
    IUpdateConfig
} from "../../api/types/AutomaticVacationDays.types";
import {AttentionIcon, TextWithIcon} from "../../helpers/icons/Icons";
import {simplifyWorkTime} from "../../helpers/WorkTimeFractionReduceHelper";
import {Spinner} from "../spinner/Spinner";
import Table from "../table/Table";
import {ColumnType, RowType} from "../table/Table.types";
import styles from './AutomaticVacationDaysTable.module.scss';
import {TextFieldWrapper} from "./text-field-wrapper/TextFieldWrapper";



export const AutomaticVacationDaysTable: FunctionComponent = () => {
    const [rows, setRows] = useState<IAutomaticVacationDaysRow[]>([])
    const {isFetching} = useGetAutomaticVacationDaysQuery((data) => buildRowFromData(data))
    const {mutate: updateDay} = useUpdateAutomaticVacationDaysQuery();

    const buildRowFromData = (data: IAutomaticVacationDaysResponse[]) => {
        const rows = data.map((automaticVacationDayResponse: IAutomaticVacationDaysResponse) => {
            const workTime = formatWorkTime(automaticVacationDayResponse);
            const nextYearProposition = formatNextYearProposition(automaticVacationDayResponse);
            const nextYearDaysBase = `${automaticVacationDayResponse.nextYearDaysBase}`
            return {...automaticVacationDayResponse, isEditMode: true, workTime, nextYearProposition, nextYearDaysBase}
        })
        setRows(rows)
    }
    const formatWorkTime = (automaticVacationDayResponse: IAutomaticVacationDaysResponse): string => {
        const {isEc, workTime} = automaticVacationDayResponse
        if (isEc) {
            return simplifyWorkTime(workTime)
        } else {
            return ''
        }
    }

    const formatNextYearProposition = (automaticVacationDayResponse: IAutomaticVacationDaysResponse) => {
        const {nextYearProposition, workTime} = automaticVacationDayResponse
        if (workTime === 8) {
            return `${nextYearProposition/8.0}d`
        }
        return `${nextYearProposition}h`
    }

    const withNotifyFormatter = (cell: any, row: RowType<any>) => {
        const nextYearProposition = getNumericValueOfNextYearProposition(row)
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

    const updateRow = (updatedRow: IAutomaticVacationDaysRow) => {
        setRows(prevState => {
            return prevState.map(row => {
                if (row.userId === updatedRow.userId){
                    return {...updatedRow};
                }
                return row;
            })
        })}

    const saveRow = (row: IAutomaticVacationDaysRow) => {
        const preparedProposition = getNumericValueOfNextYearProposition(row);

        const dataToSave: IUpdateConfig = {
            userId: row.userId,
            nextYearBase: Number(row.nextYearDaysBase),
            nextYearProposition: preparedProposition
        }
        updateDay(dataToSave)
        updateRow(row)
    }

    const nextYearPropositionFormatter = (cell: any, row: RowType<any>) => {
        const {nextYearProposition} = row
        return <TextFieldWrapper value={nextYearProposition}
                                 name={'nextYearProposition'}
                                 pattern={'^[0-9]*[h|d]$'}
                                 helperText={'niepoprawny format (liczba[d/h])'}
                                 row={row}
                                 saveRow={saveRow}
                />
    }
    const nextYearDaysBaseFormatter = (cell: any, row: RowType<any>) => {
        const {nextYearDaysBase} = row
        return <TextFieldWrapper value={nextYearDaysBase}
                                 name={'nextYearDaysBase'}
                                 pattern={'^[0-9]+$'}
                                 helperText={'tylko liczby są dopuszczalne'}
                                 row={row}
                                 saveRow={saveRow}
        />
    }

    const getNumericValueOfNextYearProposition = ({nextYearProposition}: any) => {
        return nextYearProposition.slice(-1) === 'h' ? Number(nextYearProposition.slice(0,-1)) :
                                                        Number(nextYearProposition.slice(0,-1)) * 8
    }


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
            formatter: (cell, row) => nextYearDaysBaseFormatter(cell, row),
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
                data={rows}
                columns={columns}
            />
        </div>
    )
}