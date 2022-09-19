import { Collapse, TableCell, TableRow } from "@mui/material";
import React from "react";

import { ColumnType, IExpandRow, RowType } from "./Table.types";

interface IRowProps<T> {
  keyFieldValue: string;
  columns: ColumnType<T>[];
  row: RowType<T>;
  expandRow?: IExpandRow<T>;
  striped: boolean;
  hover: boolean;
}

const getColumnStyle = <T,>(column: ColumnType<T>, row: RowType<T>) => {
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
      [`&:nth-of-type(${expandRow ? '4n+1' : 'odd'})`]: {
        backgroundColor: "#ececec",
      },
    }),
  };
};

export const Row = <T,>({
  keyFieldValue,
  columns,
  row,
  expandRow,
  striped,
  hover,
}: IRowProps<T>) => {
  const onClick = () => {
    expandRow?.onExpand(
      row,
      !(expandRow?.expanded || []).some(
        (value) => value.toString() === keyFieldValue
      )
    );
  };

  console.log(striped)

  return (
    <>
      <TableRow
        className="mainRow"
        {...(expandRow && {
          onClick: () => onClick(),
        })}
        sx={getRowStyle(!!expandRow, striped)}
        hover={hover}
      >
        {columns.map((column) => {
          const baseStyle = { textAlign: column.align || "left" };
          const passedStyle = getColumnStyle(column, row) || {};

          return (
            !column.hidden && (
              <TableCell
                key={column.name}
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
              in={(expandRow?.expanded || []).some(
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
