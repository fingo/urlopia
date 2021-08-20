import {differenceInCalendarWeeks, differenceInDays, isWeekend} from "date-fns";

export const countWorkingDays = (startDate, endDate, holidays) => {
    const holidayCounter = holidays.filter(holiday => {
        const date = new Date(holiday.date);
        return startDate.getTime() <= date.getTime() && date.getTime() <= endDate.getTime() && !isWeekend(date);
    }).length;
    return (differenceInDays(endDate, startDate) - differenceInCalendarWeeks(endDate, startDate) * 2) + 1 - holidayCounter;
}