import { useMemo, useState } from "react";

import { ColumnType, RowType } from "./Table.types";
import { getStringValue } from "./TableHelpers";

interface IUseFiltersProps<T> {
  data: RowType<T>[];
  columns: ColumnType<T>[];
}

const useFilter = <T,>({ data, columns }: IUseFiltersProps<T>) => {
  const [filters, setFilters] = useState<{ [key: string]: string }>({});

  const filteredData = useMemo(
    () =>
      data.filter((row) =>
        Object.entries(filters).every(([fieldName, filter]) => {
          const columnIndex = columns.findIndex(
            (column) => column.name === fieldName
          );

          const value = getStringValue(
            row[fieldName],
            columns[columnIndex],
            row
          );

          return value.toLowerCase().includes(filter.toLowerCase());
        })
      ),
    [columns, data, filters]
  );

  return {
    filteredData,
    filters,
    setFilter: (name: string, value: string) => {
      setFilters((prev) => {
        return {
          ...prev,
          [name]: value,
        };
      });
    },
  };
};

export default useFilter;
