import { useMemo, useState } from "react";

import { ColumnType, RowType } from "./Table.types";
import { getStringValue } from "./TableHelpers";

interface IUseFiltersProps<DataType extends object> {
  data: RowType<DataType>[];
  columns: ColumnType<DataType>[];
}

const useFilter = <DataType extends object>({ data, columns }: IUseFiltersProps<DataType>) => {
  const [filters, setFilters] = useState<{ [key in keyof DataType]?: string }>({});

  const filteredData = useMemo(
    () =>
      data.filter((row) =>
        (Object.entries(filters) as [keyof DataType, string][]).every(([fieldName, filter]) => {
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
    setFilter: (name: keyof DataType, value: string) => {
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
