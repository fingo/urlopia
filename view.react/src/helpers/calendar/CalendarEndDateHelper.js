import moment from "moment";

export const getLastDayFromCalendarMonthView = (date) => {
    const fridayIsoNumber = 5;
    const dateMoment = moment(date,'YYYY-MM-DD');
    const dayOfWeek = dateMoment.isoWeekday();
    const firstDayFromCalendarMonthView = dateMoment.add(fridayIsoNumber-dayOfWeek,'d');
    if (firstDayFromCalendarMonthView.isBefore(date)){
        return date;
    }
    return firstDayFromCalendarMonthView.format("YYYY-MM-DD");
}