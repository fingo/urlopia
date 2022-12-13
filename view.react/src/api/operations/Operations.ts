import ApiOperation from "./ApiOperation";
import { ApiMethod, ApiOperationsType } from "./Operations.types";


const Operations: ApiOperationsType = {
  [ApiOperation.GetCalendar]: {
    method: ApiMethod.GET,
    url: () => '/calendar'
  }
}

export default Operations;
