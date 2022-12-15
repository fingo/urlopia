export interface IAutomaticVacationDaysResponse {
    userId: number,
    userFullName: string,
    workTime: number,
    nextYearProposition: number,
    nextYearDaysBase: number,
    isEc: boolean,
}

export interface IUpdateConfig {
    userId: number
    nextYearBase: number
    nextYearProposition: number
}

export interface IAutomaticVacationDaysRow{
    isEditMode: boolean,
    userId: number,
    userFullName: string,
    workTime: string,
    nextYearProposition: string,
    nextYearDaysBase: string,
    isEc: boolean,}