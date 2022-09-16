import React, { useMemo, useState } from "react";
import { Form } from "react-bootstrap";

import { ColumnType } from "./Table.types";

interface IUseFiltersProps {
  data: any[];
  columns: ColumnType[];
}

const useFilter = ({ data, columns }: IUseFiltersProps) => {
  const [filters, setFilters] = useState<
    { fieldName: string; filter: string }[]
  >([]);

  const filteredData = useMemo(
    () =>
      data.filter((row) =>
        filters.every((filter) => {
          let value = row[filter.fieldName];
          const columnIndex = columns.findIndex(
            (column) => column.name === filter.fieldName
          );
          const filterValue = columns[columnIndex].filterValue;
          const formatter = columns[columnIndex].formatter;
          if (filterValue) {
            value = filterValue(row[filter.fieldName], row);
          } else if (formatter) {
            value = formatter(row[filter.fieldName], row);
          }
          return value.toLowerCase().includes(filter.filter.toLowerCase());
        })
      ),
    [columns, data, filters]
  );

  const FilterComponent = useMemo(
    () =>
      ({ column }: { column: ColumnType }) =>
        (
          <Form.Control
            placeholder="Filtruj..."
            onChange={(e) => {
              setFilters((prev) => {
                const index = prev.findIndex(
                  (filter) => filter.fieldName === column.name
                );
                if (index === -1) {
                  return [
                    ...prev,
                    {
                      fieldName: column.name,
                      filter: e.target.value,
                    },
                  ];
                }

                const newFilter = [...prev];
                newFilter[index] = {
                  ...newFilter[index],
                  filter: e.target.value,
                };

                return newFilter;
              });
            }}
          />
        ),
    []
  );

  return {
    filteredData,
    FilterComponent,
  };
};

export default useFilter;
