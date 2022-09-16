import { TableBody, TableCell, TableRow } from "@mui/material";
import React from "react";

import { ColumnType } from "./Table.types";

interface IFilterRowProps {
  columns: ColumnType[];
  renderFilter: (column: ColumnType) => JSX.Element;
}
export const FilterRow = ({ columns, renderFilter }: IFilterRowProps) => {
  return (
    <TableBody>
      <TableRow>
        {columns.map(
          (column) =>
            !column.hidden && (
              <TableCell
                key={column.name}
                style={{ textAlign: column.headerAlign || "left" }}
                hidden={column.hideHeader}
              >
                {column.filter && renderFilter(column)}
              </TableCell>
            )
        )}
      </TableRow>
    </TableBody>
  );
};
