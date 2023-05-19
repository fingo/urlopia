import { TableBody, TableCell, TableRow } from "@mui/material";
import React from "react";
import { Form } from "react-bootstrap";

import { ColumnType } from "./Table.types";

interface IFilterRowProps<DataType extends object> {
  columns: ColumnType<DataType>[];
  filters: {
    [key in keyof DataType]?: string;
  };
  setFilter: (name: keyof DataType, value: string) => void;
}
export const FilterRow = <DataType extends object>({
  columns,
  filters,
  setFilter,
}: IFilterRowProps<DataType>) => {
  return (
    <TableBody>
      <TableRow>
        {columns.map(
          (column) =>
            !column.hidden && (
              <TableCell
                key={column.name.toString()}
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
