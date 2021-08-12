import {createForwardingReducer} from "../utils";

const requestReducersMappings = {}

export const requestReducer = createForwardingReducer(requestReducersMappings)
