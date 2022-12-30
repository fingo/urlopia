import ApiOperation from "./ApiOperation";
import {ApiMethod, ApiOperationsType} from "./Operations.types";


const Operations: ApiOperationsType = {
  [ApiOperation.GetCalendar]: {
    method: ApiMethod.GET,
    url: () => '/calendar'
  },
  [ApiOperation.GetAutomaticVacationDays]: {
    method: ApiMethod.GET,
    url: () => `/automatic-vacation-days`
  },
  [ApiOperation.UpdateAutomaticVacationDayProposition]: {
    method: ApiMethod.PATCH,
    url: () => `/automatic-vacation-days`
  },
  [ApiOperation.GetUsers]: {
    method: ApiMethod.GET,
    url: () => `/users`
  },

}

export default Operations;
