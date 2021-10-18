import {FETCH_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX} from "../constants";
import {fetchWorkingHoursPreferencesReducer} from "./fetchWorkingHoursPreferences";

describe('fetchWorkingHoursPreferencesReducer', () => {
    const sampleState = {
        fetching: false,
        error: null,
        preferences: {}
    }

    it('should set fetching flag to true and reset error on request', () => {
        // given
        const action = {
            type: `${FETCH_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_request`
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

        const newState = fetchWorkingHoursPreferencesReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and add preferences on success', () => {
        // given
        const samplePreferences = {
            1: {
                "userId": 1,
                "dayPreferences": {
                    "1": {
                        "nonWorking": false,
                        "startTime": "10:00",
                        "endTime": "12:00"
                    },
                    "2": {
                        "nonWorking": false,
                        "startTime": "10:00",
                        "endTime": "12:00"
                    },
                    "3": {
                        "nonWorking": false,
                        "startTime": "10:00",
                        "endTime": "12:00"
                    },
                    "4": {
                        "nonWorking": false,
                        "startTime": "10:00",
                        "endTime": "12:00"
                    },
                    "5": {
                        "nonWorking": false,
                        "startTime": "08:00",
                        "endTime": "18:00"
                    }
                }
            },
            2: {
                "userId": 2,
                "dayPreferences": {
                    "1": {
                        "nonWorking": false,
                        "startTime": "10:00",
                        "endTime": "12:00"
                    },
                    "2": {
                        "nonWorking": false,
                        "startTime": "10:00",
                        "endTime": "12:00"
                    },
                    "3": {
                        "nonWorking": true,
                        "startTime": "10:00",
                        "endTime": "12:00"
                    },
                    "4": {
                        "nonWorking": true,
                        "startTime": "10:00",
                        "endTime": "12:00"
                    },
                    "5": {
                        "nonWorking": true,
                        "startTime": "08:00",
                        "endTime": "18:00"
                    }
                }
            }
        }

        const action = {
            type: `${FETCH_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_success`,
            response: samplePreferences
        }

        const state = {
            ...sampleState,
            fetching: true
        }

        // when
        const expectedState = {
            ...sampleState,
            fetching: false,
            preferences: samplePreferences
        }

        const newState = fetchWorkingHoursPreferencesReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and set an error message on failure', () => {
        // given
        const action = {
            type: `${FETCH_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_failure`,
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

        const newState = fetchWorkingHoursPreferencesReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })
})