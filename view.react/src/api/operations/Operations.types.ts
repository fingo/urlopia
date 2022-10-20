import { ResponseType } from 'axios';

import ApiOperation from './ApiOperation';

export enum ApiMethod {
  GET,
  POST,
  PUT,
  PATCH,
  DELETE,
}

export interface IUrlParamsType {
  [key: string]: string | number;
}

interface IApiOperation {
  method: ApiMethod;
  responseType?: ResponseType;
  url: (urlParams: IUrlParamsType) => string;
}

export type ApiOperationsType = {
  [key in ApiOperation]: IApiOperation;
};

