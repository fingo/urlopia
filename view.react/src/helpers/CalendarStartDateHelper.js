import moment from "moment";

export const getFirstDayFromCalendarMonthView = (date) => {
    const dateMoment = moment(date,'YYYY-MM-DD');
    const dayOfWeek = dateMoment.isoWeekday();
    const firstDayFromCalendarMonthView = dateMoment.subtract(dayOfWeek-1,'d');
    return firstDayFromCalendarMonthView.format("YYYY-MM-DD");
}