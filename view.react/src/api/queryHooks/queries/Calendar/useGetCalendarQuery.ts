import { useQuery } from "@tanstack/react-query";

import { handleError } from "../../../../helpers/RequestHelper";
import ApiOperation from "../../../operations/ApiOperation";
import { ICalendarResponse } from "../../../types/Calendar.types";
import useRequest from "../../../useRequest";
import { calendarKeys } from "../queryKeys";

interface IProps {
  startDate: string;
  endDate: string;
}

const useGetCalendarQuery = ({
  startDate, endDate
}: IProps) => {
  const { request } = useRequest<ICalendarResponse>(ApiOperation.GetCalendar);

  return useQuery(calendarKeys.list({startDate, endDate}), async () => {
    const res = await request({
      params: {
        startDate,
        endDate,
        filter: 'active:true'
      },
    });

    return res.data;
  }, {
    onError: (err) => {
      handleError(err)
    }
  });
};

export default useGetCalendarQuery;
