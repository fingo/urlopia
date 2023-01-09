import { AxiosResponse } from "axios";

import {axiosClient} from "./client";
import ApiOperation from "./operations/ApiOperation";
import Operations from "./operations/Operations";
import { ApiMethod } from "./operations/Operations.types";
import { IRequestInternalProps, IRequestProps } from "./useRequest.types";

const requestInternal = <T>({
  url,
  method,
  params,
  additionalParams,
  data,
}: IRequestInternalProps): Promise<AxiosResponse<T>> => {

  switch (method) {
    case ApiMethod.GET:
      return axiosClient.get(url, { params, ...additionalParams });
    case ApiMethod.POST:
      return axiosClient.post(url, data, additionalParams);
    case ApiMethod.PATCH:
      return axiosClient.patch(url, data, additionalParams);
    case ApiMethod.DELETE:
      return axiosClient.delete(url, additionalParams);
    default:
      throw new Error("Method not implemented");
  }
};

const useRequest = <T>(operation: ApiOperation) => {
  const { method, url: getUrl } = Operations[operation];

  const request = async ({ urlParams, ...rest }: IRequestProps = {}) => {
    const url = getUrl(urlParams || {});

    return requestInternal<T>({
      url,
      method,
      ...rest,
    });
  };

  return {
    request,
  };
};

export default useRequest;

