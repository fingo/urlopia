import ApiOperation from "./ApiOperation";
import {ApiMethod, ApiOperationsType} from "./Operations.types";


const Operations: ApiOperationsType = {
  [ApiOperation.GetCalendar]: {
    method: ApiMethod.GET,
    url: () => '/calendar'
  },
  [ApiOperation.GetAutomaticVacationDays]: {
    method: ApiMethod.GET,
    url: () => '/automatic-vacation-days'
  }
}

export default Operations;
