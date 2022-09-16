import { Collapse, TableCell, TableRow } from "@mui/material";
import React from "react";

import { ColumnType, IExpandRow } from "./Table.types";

interface IRowProps {
  keyField: string;
  columns: ColumnType[];
  row: any;
  expandRow?: IExpandRow;
  striped: boolean;
  hover: boolean;
}

export const Row = ({
  keyField,
  columns,
  row,
  expandRow,
  striped,
  hover,
}: IRowProps) => {
  const onClick = () => {
    expandRow?.onExpand(
      row,
      !(expandRow?.expanded || []).includes(row[keyField])
    );
  };

  return (
    <>
      <TableRow
        {...(expandRow && {
          onClick: () => onClick(),
          sx: { "& > *": { borderBottom: "unset" } },
        })}
        {...(striped && {
          sx: {
            "&:nth-of-type(odd)": {
              backgroundColor: "#ececec",
            },
          },
        })}
        hover={hover}
      >
        {columns.map((column) => {
          const baseStyle = { textAlign: column.align || "left" };
          let passedStyle;

          if (!!column.style && typeof column.style === "object") {
            passedStyle = column.style;
          } else if (!!column.style && typeof column.style === "function") {
            passedStyle = column.style(row[column.name], row);
          }

          return (
            !column.hidden && (
              <TableCell
                key={column.name}
                style={{ ...baseStyle, ...passedStyle }}
              >
                {column.formatter
                  ? column.formatter(row[column.name], row)
                  : row[column.name]}
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
              in={(expandRow?.expanded || []).includes(row[keyField])}
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
