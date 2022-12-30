import { useQuery } from "@tanstack/react-query";

import { handleError } from "../../../../helpers/RequestHelper";
import ApiOperation from "../../../operations/ApiOperation";
import {UserOutput} from "../../../types/Users.types";
import useRequest from "../../../useRequest";
import {userKeys} from "../queryKeys";

interface IProps {
    active: boolean,
}

const useGetUsers = ({active}: IProps) => {
    const { request } = useRequest<UserOutput[]>(ApiOperation.GetUsers);

    return useQuery(userKeys.list({active}), async () => {
        const res = await request();

        return res.data;
    }, {
        onError: (err) => {
            handleError(err)
        }
    });
};

export default useGetUsers;
