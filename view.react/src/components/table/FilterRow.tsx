import { TableBody, TableCell, TableRow } from "@mui/material";
import React from "react";
import { Form } from "react-bootstrap";

import { ColumnType } from "./Table.types";

interface IFilterRowProps<T> {
  columns: ColumnType<T>[];
  filters: {
    [key: string]: string;
  };
  setFilter: (name: string, value: string) => void;
}
export const FilterRow = <T,>({
  columns,
  filters,
  setFilter,
}: IFilterRowProps<T>) => {
  return (
    <TableBody>
      <TableRow>
        {columns.map(
          (column) =>
            !column.hidden && (
              <TableCell
                key={column.name}
                style={{ textAlign: column.headerAlign ?? "left" }}
                hidden={column.hideHeader}
              >
                {column.filter && (
                  <Form.Control
                    placeholder="Filtruj..."
                    value={filters[column.name] ?? ""}
                    onChange={(e) => setFilter(column.name, e.target.value)}
                  />
                )}
              </TableCell>
            )
        )}
      </TableRow>
    </TableBody>
  );
};
