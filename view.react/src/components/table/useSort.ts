import { useMemo, useState } from "react";

import { ColumnType, OrderByType, OrderType } from "./Table.types";

interface IUseFiltersProps {
  data: any[];
  columns: ColumnType[];
}

const sortFactory = (field: string | undefined, orderBy: OrderType|undefined) => {
  return (a: any, b: any) => {
    if (!field || !orderBy || !a[field] || !b[field]) {
      return 1;
    }

    const compare = a[field]
    .toString()
    .localeCompare(b[field].toString(), "pl", { sensitivity: "base" })

    return orderBy === 'desc' ? compare : -compare;
  };
};

const useSort = ({ data, columns }: IUseFiltersProps) => {
  const [orderBy, setOrderBy] = useState<OrderByType>({
    field: undefined,
    order: undefined,
  });

  const sortedData = useMemo(() => {
    const columnIndex = columns.findIndex(
      (column) => column.name === orderBy.field
    );

    if (columnIndex > -1 && !columns[columnIndex].onSort) {
      data.sort(sortFactory(orderBy.field, orderBy.order))
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
