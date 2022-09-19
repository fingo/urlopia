import { useMemo, useState } from "react";

import { ColumnType, OrderByType, OrderType, RowType } from "./Table.types";
import { getStringValue } from "./TableHelpers";

interface IUseFiltersProps<T> {
  data: { [key: string]: T }[];
  columns: ColumnType<T>[];
}

const sortFactory = <T>(
  field: string | undefined,
  orderBy: OrderType | undefined,
  column: ColumnType<T>
) => {
  return (a: RowType<T>, b: RowType<T>) => {
    if (!field || !orderBy || !a[field] || !b[field]) {
      return 1;
    }

    const aString = getStringValue(a[field], column, a);
    const bString = getStringValue(b[field], column, b);

    const compare = aString.localeCompare(bString, "pl", {
      sensitivity: "base",
    });

    return orderBy === "desc" ? compare : -compare;
  };
};

const useSort = <T>({ data, columns }: IUseFiltersProps<T>) => {
  const [orderBy, setOrderBy] = useState<OrderByType>({
    field: undefined,
    order: undefined,
  });

  const sortedData = useMemo(() => {
    const columnIndex = columns.findIndex(
      (column) => column.name === orderBy.field
    );

    if (columnIndex > -1 && !columns[columnIndex].onSort) {
      data.sort(
        sortFactory(orderBy.field, orderBy.order, columns[columnIndex])
      );
    }

    return data;
  }, [columns, data, orderBy.field, orderBy.order]);

  return {
    orderBy,
    setOrderBy,
    sortedData,
  };
};

export default useSort;
