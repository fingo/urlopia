import { TableCell, TableSortLabel, TableSortLabelProps } from "@mui/material";
import React from "react";
import { ArrowDown, ArrowDownUp } from "react-bootstrap-icons";

import { ColumnType, OrderByType } from "./Table.types";
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
  const getCurrentColumnDir = () => {
    if (!column.sort || orderBy.field !== column.name) {
      return;
    }

    return orderBy.order;
  };

  const getNewColumnDir = () => {
    if (orderBy.field !== column.name) {
      return "desc";
    }

    return orderBy.order === "desc" ? "asc" : "desc";
  };

  const setNewOrder = () => {
    const newDir = getNewColumnDir();

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
      sortDirection={getCurrentColumnDir()}
    >
      <SortLabelWrapper
        disabled={!column.sort}
        active={orderBy.field === column.name}
        direction={getCurrentColumnDir()}
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
