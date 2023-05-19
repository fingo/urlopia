import { ColumnType,RowType } from "./Table.types";

export const getStringValue = <DataType extends object, Property extends keyof DataType>(
  value: DataType[Property],
  column: ColumnType<DataType, Property>,
  row: RowType<DataType>
) => {
  let stringValue: string | DataType[Property] | JSX.Element = value;

  const filteredValue = column.filterValue?.(value, row);
  const formattedValue = column.formatter?.(value, row);
  
  if (typeof filteredValue === 'string') {
    stringValue = filteredValue;
  } else if (typeof formattedValue === 'string') {
    stringValue = formattedValue;
  }

  if (stringValue?.toString() === undefined) {
    throw new Error(
      "Row value cannot be converted to string, please pass formatter or filterValue"
    );
  }

  return stringValue.toString();
};

export const getKeyFieldValue = <DataType extends object>(row: RowType<DataType>, keyField: keyof DataType) => {
  const key = row[keyField];

  if (key?.toString() === undefined) {
    throw new Error(
      "Each value of keyField should be convertable to string"
    );
  }

  return key.toString();
}
