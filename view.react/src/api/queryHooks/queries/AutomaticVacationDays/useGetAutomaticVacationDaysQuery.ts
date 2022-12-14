import {useQuery} from "@tanstack/react-query";

import {handleError} from "../../../../helpers/RequestHelper";
import ApiOperation from "../../../operations/ApiOperation";
import {IAutomaticVacationDaysResponse} from "../../../types/AutomaticVacationDays.types";
import useRequest from "../../../useRequest";
import {automaticVacationDays} from "../queryKeys";

const useGetAutomaticVacationDaysQuery = () => {
    const { request } = useRequest<IAutomaticVacationDaysResponse[]>(ApiOperation.GetAutomaticVacationDays);

    return useQuery(automaticVacationDays.lists(), async () => {
        const res = await request();

        return res.data;
    }, {
        onError: (err) => {
            handleError(err)
        }
    });
};

export default useGetAutomaticVacationDaysQuery;