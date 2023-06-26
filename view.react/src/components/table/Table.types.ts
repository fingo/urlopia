import { CSSProperties } from "react";

type AlignType =
  | "left"
  | "right"
  | "center"
  | "justify"
  | "initial"
  | "inherit";

export type OrderType = "asc" | "desc";

export type RowType<DataType extends object> = DataType;

export interface IExpandRow<DataType extends object> {
  onExpand: (row: RowType<DataType>, isExpand: boolean) => void;
  expanded: string[];
  renderer: (row: RowType<DataType>) => JSX.Element;
}

export type OrderByType<DataType extends object> = {
  field: keyof DataType | undefined;
  order: OrderType | undefined;
};

export type ColumnsType<DataType extends object> = { [Property in keyof DataType]: {
  name: Property;
  text?: string;
  headerAlign?: AlignType;
  align?: AlignType;
  sort?: boolean;
  onSort?: (field: Property, order: OrderType) => void;
  style?: CSSProperties | ((cell: DataType[Property], row: DataType) => CSSProperties);
  formatter?: (cell: DataType[Property], row: DataType) => JSX.Element | string;
  filterValue?: (cell: DataType[Property], row: DataType) => string;
  hidden?: boolean;
  hideHeader?: boolean;
  filter?: boolean;
} }

export type ColumnType<DataType extends object, Property extends keyof DataType = keyof DataType> = ColumnsType<DataType>[Property]
