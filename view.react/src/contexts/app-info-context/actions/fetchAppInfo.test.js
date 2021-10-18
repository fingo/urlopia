import {FETCH_APP_INFO_ACTION_PREFIX} from "../contants";
import {fetchAppInfoReducer} from "./fetchAppInfo";

describe('fetchAppInfoReducer', () => {
    const sampleState = {
        fetching: false,
        error: null,
        appInfo: {}
    }

    const sampleResponse = {
        "version": "2.1.0",
        "commitId": "4fd1c6",
        "buildZonedTime": "2021-09-15T12:36:01.106+02:00"
    }

    it('should set fetching flag to true and reset error on request', () => {
        // given
        const action = {
            type: `${FETCH_APP_INFO_ACTION_PREFIX}_request`
        }

        const state = {
            ...sampleState,
            error: "Some error message"
        }

        // when
        const expectedState = {
            ...sampleState,
            fetching: true,
            error: null
        }

        const newState = fetchAppInfoReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and add app info on success', () => {
        // given
        const action = {
            type: `${FETCH_APP_INFO_ACTION_PREFIX}_success`,
            response: sampleResponse
        }

        const state = {
            ...sampleState,
            fetching: true
        }

        // when
        const expectedState = {
            ...sampleState,
            fetching: false,
            appInfo: sampleResponse
        }

        const newState = fetchAppInfoReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and set an error message on failure', () => {
        // given
        const action = {
            type: `${FETCH_APP_INFO_ACTION_PREFIX}_failure`,
            error: "Some error message"
        }

        const state = {
            ...sampleState,
            fetching: true
        }

        // when
        const expectedState = {
            ...sampleState,
            fetching: false,
            error: "Some error message"
        }

        const newState = fetchAppInfoReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })
})
