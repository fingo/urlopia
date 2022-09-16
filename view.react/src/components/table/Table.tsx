import { TableBody, TableHead, TableRow } from "@mui/material";
import MuiTable from "@mui/material/Table";
import { useState } from "react";

import { FilterRow } from "./FilterRow";
import { HeaderCell } from "./HeaderCell";
import { Row } from "./Row";
import { ColumnType, IExpandRow, OrderByType } from "./Table.types";
import useFilter from "./useFilter";

interface ITableProps {
  keyField: string;
  data: any[];
  wrapperClasses?: string;
  columns: ColumnType[];
  expandRow?: IExpandRow;
  hover?: boolean;
  striped?: boolean;
}
const Table = ({
  keyField,
  data,
  wrapperClasses = "",
  columns,
  expandRow,
  hover = false,
  striped = false,
}: ITableProps) => {
  const [orderBy, setOrderBy] = useState<OrderByType>({
    field: undefined,
    order: undefined,
  });
  const { filteredData, FilterComponent } = useFilter({ data, columns });

  return (
    <div className={wrapperClasses}>
      <MuiTable>
        <TableHead>
          <TableRow>
            {columns.map(
              (column) =>
                !column.hidden && (
                  <HeaderCell
                    key={column.name}
                    orderBy={orderBy}
                    setOrderBy={setOrderBy}
                    column={column}
                  />
                )
            )}
          </TableRow>
        </TableHead>
        {columns.some((column) => column.filter) && (
          <FilterRow
            columns={columns}
            renderFilter={(column) => <FilterComponent column={column} />}
          />
        )}
        {filteredData.length > 0 && <TableBody>
          {filteredData.map((row) => (
            <Row
              key={row[keyField]}
              keyField={keyField}
              columns={columns}
              row={row}
              expandRow={expandRow}
              striped={striped}
              hover={hover}
            />
          ))}
        </TableBody>}
      </MuiTable>
    </div>
  );
};

export default Table;
