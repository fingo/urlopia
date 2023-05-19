import { Collapse, TableCell, TableRow } from "@mui/material";
import React from "react";

import { ColumnType, IExpandRow, RowType } from "./Table.types";

const getColumnStyle = <DataType extends object, Property extends keyof DataType>(column: ColumnType<DataType, Property>, row: RowType<DataType>) => {
  if (column.style === undefined) {
    return;
  }

  switch (typeof column.style) {
    case "object":
      return column.style;
    case "function":
      return column.style(row[column.name], row);
  }
};

const getRowStyle = (expandRow: boolean, striped: boolean) => {
  return {
    ...(expandRow && { "& > *": { borderBottom: "unset" } }),
    ...(striped && {
      [`&:nth-of-type(${expandRow ? "4n+1" : "odd"})`]: {
        backgroundColor: "#ececec",
      },
    }),
  };
};

interface IRowProps<DataType extends object> {
  keyFieldValue: string;
  columns: ColumnType<DataType>[];
  row: RowType<DataType>;
  expandRow?: IExpandRow<DataType>;
  striped: boolean;
  hover: boolean;
}

export const Row = <DataType extends object>({
  keyFieldValue,
  columns,
  row,
  expandRow,
  striped,
  hover,
}: IRowProps<DataType>) => {
  const onClick = () => {
    expandRow?.onExpand(
      row,
      !(expandRow?.expanded ?? []).some(
        (value) => value.toString() === keyFieldValue
      )
    );
  };

  return (
    <>
      <TableRow
        {...(expandRow && {
          onClick: () => onClick(),
        })}
        sx={getRowStyle(!!expandRow, striped)}
        hover={hover}
      >
        {columns.map((column) => {
          const baseStyle = { textAlign: column.align ?? "left" };
          const passedStyle = getColumnStyle(column, row) ?? {};

          return (
            !column.hidden && (
              <TableCell
                key={column.name.toString()}
                style={{ ...baseStyle, ...passedStyle }}
              >
                {column.formatter
                  ? column.formatter(row[column.name], row)
                  : (row[column.name] as any)}
              </TableCell>
            )
          );
        })}
      </TableRow>
      {expandRow && (
        <TableRow>
          <TableCell
            className="reset-expansion-style"
            sx={{ paddingBottom: 0, paddingTop: 0 }}
            colSpan={columns.reduce(
              (number, column) => number + +!column.hidden,
              0
            )}
          >
            <Collapse
              in={(expandRow?.expanded ?? []).some(
                (value) => value?.toString() === keyFieldValue
              )}
              timeout="auto"
              unmountOnExit
            >
              <div className="row-expand-slide-appear-done row-expand-slide-enter-done">
                <div className="row-expansion-style">
                  {expandRow?.renderer(row)}
                </div>
              </div>
            </Collapse>
          </TableCell>
        </TableRow>
      )}
    </>
  );
};
