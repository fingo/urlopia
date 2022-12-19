import {useMutation} from "@tanstack/react-query";

import {pushSuccessNotification} from "../../../../helpers/notifications/Notifications";
import { handleError } from "../../../../helpers/RequestHelper";
import ApiOperation from "../../../operations/ApiOperation";
import {IUpdateConfig} from "../../../types/AutomaticVacationDays.types";
import { ICalendarResponse } from "../../../types/Calendar.types";
import useRequest from "../../../useRequest";

const useUpdateAutomaticVacationDaysQuery = () => {
    const { request } = useRequest<ICalendarResponse>(ApiOperation.UpdateAutomaticVacationDayProposition);

    return useMutation(async (updateConfig : IUpdateConfig) => {
        const res = await request({data: {...updateConfig}});
        return res.data;
    }, {
        onError: (err) => {
            handleError(err)
        },
        onSuccess: () => {
            pushSuccessNotification("Pomyślnie zmieniono ustawienia automatycznego dodawania dni")
        }
    });
};

export default useUpdateAutomaticVacationDaysQuery;
