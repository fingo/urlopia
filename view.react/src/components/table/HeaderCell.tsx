import { TableCell, TableSortLabel, TableSortLabelProps } from "@mui/material";
import React from "react";
import { ArrowDown, ArrowDownUp } from "react-bootstrap-icons";

import { ColumnType, OrderByType, OrderType } from "./Table.types";

interface ISortLabelProps extends TableSortLabelProps {
  disabled?: boolean;
}
const SortLabelWrapper = ({
  disabled = false,
  children,
  ...rest
}: ISortLabelProps) => {
  if (disabled) {
    return <>{children}</>;
  }

  return <TableSortLabel {...rest}>{children}</TableSortLabel>;
};
interface IHeaderCellProps<T> {
  column: ColumnType<T>;
  orderBy: OrderByType;
  setOrderBy: React.Dispatch<React.SetStateAction<OrderByType>>;
}
export const HeaderCell = <T,>({
  column,
  orderBy,
  setOrderBy,
}: IHeaderCellProps<T>) => {
  const setNewOrder = () => {
    let newDir: OrderType = "desc";
    const isDesc = orderBy.field === column.name && orderBy.order === "desc";

    if (isDesc) {
      newDir = "asc";
    }

    setOrderBy({
      field: column.name,
      order: newDir,
    });
    column.onSort?.(column.name, newDir);
  };

  return (
    <TableCell
      sx={{
        textAlign: column.headerAlign || "left",
        fontWeight: "bold",
      }}
      hidden={column.hideHeader}
      sortDirection={
        column.sort && orderBy.field === column.name && orderBy.order
      }
    >
      <SortLabelWrapper
        disabled={!column.sort}
        active={orderBy.field === column.name}
        direction={orderBy.field === column.name ? orderBy.order : undefined}
        sx={{
          "& .MuiTableSortLabel-icon": {
            opacity: 1,
          },
        }}
        onClick={() => setNewOrder()}
        IconComponent={column.name !== orderBy.field ? ArrowDownUp : ArrowDown}
      >
        {column.text}
      </SortLabelWrapper>
    </TableCell>
  );
};
