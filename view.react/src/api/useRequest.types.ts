import { AxiosRequestConfig } from 'axios';

import { ApiMethod,IUrlParamsType } from './operations/Operations.types';

interface IRequestParams {
  params?: Record<string, unknown>;
  data?: Record<string, unknown> | FormData;
  additionalParams?: Partial<AxiosRequestConfig>;
}

export interface IRequestProps extends IRequestParams {
  urlParams?: IUrlParamsType;
}

export interface IRequestInternalProps extends IRequestParams {
  url: string;
  method: ApiMethod;
  token?: string;
}
