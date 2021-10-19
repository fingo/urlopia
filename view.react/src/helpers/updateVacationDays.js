import {fetchPendingDays} from "../contexts/vacation-days-context/actions/fetchPendingDays";
import {fetchVacationDays} from "../contexts/vacation-days-context/actions/fetchVacationDays";

export const updateVacationDays = (vacationDaysDispatch) => {
    fetchPendingDays(vacationDaysDispatch);
    fetchVacationDays(vacationDaysDispatch);
}