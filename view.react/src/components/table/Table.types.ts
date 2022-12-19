import { CSSProperties } from "react";

type AlignType =
  | "left"
  | "right"
  | "center"
  | "justify"
  | "initial"
  | "inherit";

export type OrderType = "asc" | "desc";

export type RowType<Cell> = { [key: string]: Cell };

export interface IExpandRow<T> {
  onExpand: (row: RowType<T>, isExpand: boolean) => void;
  expanded: string[];
  renderer: (row: RowType<T>) => JSX.Element;
}

type OnSortType = (field: string, order: OrderType) => void;
export type OrderByType = {
  field: string | undefined;
  order: OrderType | undefined;
};

export interface ColumnType<Cell, Row = RowType<Cell>> {
  name: string;
  text?: string;
  headerAlign?: AlignType;
  align?: AlignType;
  sort?: boolean;
  onSort?: OnSortType;
  style?: CSSProperties | ((cell: Cell, row: Row) => CSSProperties);
  formatter?: (cell: Cell, row: Row) => JSX.Element | string;
  filterValue?: (cell: Cell, row: Row) => string;
  hidden?: boolean;
  hideHeader?: boolean;
  filter?: boolean;
}
