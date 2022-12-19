import {useQuery} from "@tanstack/react-query";

import {handleError} from "../../../../helpers/RequestHelper";
import ApiOperation from "../../../operations/ApiOperation";
import {IAutomaticVacationDaysResponse} from "../../../types/AutomaticVacationDays.types";
import useRequest from "../../../useRequest";
import {automaticVacationDays} from "../queryKeys";

interface IonSuccessCallback { (data: IAutomaticVacationDaysResponse[]): void }
const useGetAutomaticVacationDaysQuery = (onSuccessCallback: IonSuccessCallback) => {
    const { request } = useRequest<IAutomaticVacationDaysResponse[]>(ApiOperation.GetAutomaticVacationDays);

    return useQuery(automaticVacationDays.lists(), async () => {
        const res = await request({params: {
            filter: `user.active:TRUE`}
        });

        return res.data;
    }, {
        onSuccess: (res: IAutomaticVacationDaysResponse[]) => {
            onSuccessCallback(res)
        },
        onError: (err) => {
            handleError(err)
        }
    });
};

export default useGetAutomaticVacationDaysQuery;