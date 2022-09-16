import { TableBody, TableHead, TableRow } from "@mui/material";
import MuiTable from "@mui/material/Table";

import { FilterRow } from "./FilterRow";
import { HeaderCell } from "./HeaderCell";
import { Row } from "./Row";
import { ColumnType, IExpandRow } from "./Table.types";
import useFilter from "./useFilter";
import useSort from "./useSort";

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
  const { filteredData, FilterComponent } = useFilter({ data, columns });
  const { orderBy, setOrderBy, sortedData } = useSort({
    data: filteredData,
    columns,
  });

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
        {sortedData.length > 0 && (
          <TableBody>
            {sortedData.map((row) => (
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
          </TableBody>
        )}
      </MuiTable>
    </div>
  );
};

export default Table;
