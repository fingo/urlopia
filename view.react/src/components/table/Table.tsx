import {TableBody, TableContainer, TableHead, TableRow} from "@mui/material";
import MuiTable from "@mui/material/Table";

import { FilterRow } from "./FilterRow";
import { HeaderCell } from "./HeaderCell";
import { Row } from "./Row";
import { ColumnType, IExpandRow, RowType } from "./Table.types";
import { getKeyFieldValue } from "./TableHelpers";
import useFilter from "./useFilter";
import useSort from "./useSort";

interface ITableProps<T> {
  keyField: string;
  data: RowType<T>[];
  columns: ColumnType<T>[];
  expandRow?: IExpandRow<T>;
  wrapperClasses?: string;
  hover?: boolean;
  striped?: boolean;
}

const Table = <T,>({
  keyField,
  data,
  columns,
  expandRow,
  wrapperClasses = "",
  hover = false,
  striped = false,
}: ITableProps<T>) => {
  const { filteredData, filters, setFilter } = useFilter({ data, columns });
  const {
    orderBy,
    setOrderBy,
    sortedData: sortedAndFilteredData,
  } = useSort({
    data: filteredData,
    columns,
  });

  return (
    <TableContainer className={wrapperClasses}>
      <MuiTable stickyHeader={true}>
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
                filters={filters}
                setFilter={setFilter}
            />
        )}
        {sortedAndFilteredData.length > 0 && (
            <TableBody>
              {sortedAndFilteredData.map((row) => (
                  <Row
                      key={getKeyFieldValue(row, keyField)}
                      keyFieldValue={getKeyFieldValue(row, keyField)}
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
    </TableContainer>
  );
};

export default Table;
