import {formatDate} from "./DateFormatterHelper";

export const isHoliday = (day, holidays) => {
    const formattedDay = formatDate(day);
    return holidays.some(holiday => holiday.date === formattedDay);
};