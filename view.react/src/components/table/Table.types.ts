import { CSSProperties } from "react";

type AlignType =
| "left"
| "right"
| "center"
| "justify"
| "initial"
| "inherit";

export type OrderType = "asc" | "desc";

export interface IExpandRow {
onExpand: (row: any, isExpand: boolean) => void;
expanded: number[];
renderer: (row: any) => JSX.Element;
}

type OnSortType = (field: string, order: OrderType) => void;
export type OrderByType = {
field: string | undefined;
order: OrderType | undefined;
};

export interface ColumnType<C extends object = any, R = any> {
name: string;
text: string;
headerAlign?: AlignType;
align?: AlignType;
sort?: boolean;
onSort?: OnSortType;
style?: CSSProperties | ((cell: C, row: R) => CSSProperties);
formatter?: (cell: C, row: R) => JSX.Element;
filterValue?: (cell: C, row: R) => string;
hidden?: boolean;
hideHeader?: boolean;
filter?: boolean;
}