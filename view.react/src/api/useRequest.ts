import { useAccount, useMsal } from "@azure/msal-react";
import axios, { AxiosResponse } from "axios";

import { loginRequest } from "../authConfig";
import ApiOperation from "./operations/ApiOperation";
import Operations from "./operations/Operations";
import { ApiMethod } from "./operations/Operations.types";
import { IRequestInternalProps, IRequestProps } from "./useRequest.types";

const URL_PREFIX =
  process.env.NODE_ENV === "development" ? "http://localhost:8080" : "";

const client = axios.create({
  baseURL: `${URL_PREFIX}/api/v2`,
});

const requestInternal = <T>({
  url,
  method,
  params,
  additionalParams,
  data,
  token,
}: IRequestInternalProps): Promise<AxiosResponse<T>> => {
  if (token) {
    client.defaults.headers.common.Authorization = `Bearer ${token}`;
  }

  switch (method) {
    case ApiMethod.GET:
      return client.get(url, { params, ...additionalParams });
    case ApiMethod.POST:
      return client.post(url, data, additionalParams);
    case ApiMethod.PATCH:
      return client.patch(url, data, additionalParams);
    case ApiMethod.DELETE:
      return client.delete(url, additionalParams);
    default:
      throw new Error("Method not implemented");
  }
};

const useRequest = <T>(operation: ApiOperation) => {
  const { method, url: getUrl } = Operations[operation];
  const { instance, accounts } = useMsal();
  const account = useAccount(accounts[0]);

  const request = async ({ urlParams, ...rest }: IRequestProps = {}) => {
    const url = getUrl(urlParams || {});
    let token;
    if (account) {
      const response = await instance.acquireTokenSilent({
        ...loginRequest,
        account,
      });
      token = response.accessToken;
    }

    return requestInternal<T>({
      url,
      method,
      token,
      ...rest,
    });
  };

  return {
    request,
  };
};

export default useRequest;

