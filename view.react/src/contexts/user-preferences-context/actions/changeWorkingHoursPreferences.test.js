import {CHANGE_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX} from "../constants";
import {changeWorkingHoursPreferencesReducer} from "./changeWorkingHoursPreferences";

describe('changeWorkingHoursPreferencesReducer', () => {
    const sampleState = {
        fetching: false,
        error: null,
        preferences: {}
    }

    it('should return same state on request', () => {
        // given
        const action = {
            type: `${CHANGE_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_request`
        }

        const state = {
            ...sampleState,
        }

        // when
        const expectedState = {
            ...sampleState,
        }

        const newState = changeWorkingHoursPreferencesReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should replace changed preference on success', () => {
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

        const sampleResponse = {
            "userId": 1,
            "dayPreferences": {
                "1": {
                    "nonWorking": true,
                    "startTime": "10:00",
                    "endTime": "12:00"
                },
                "2": {
                    "nonWorking": true,
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

        const action = {
            type: `${CHANGE_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_success`,
            response: sampleResponse
        }

        const state = {
            ...sampleState,
            preferences: samplePreferences
        }

        // when
        const expectedState = {
            ...sampleState,
            preferences: {
                ...state.preferences,
                1: sampleResponse
            }
        }

        const newState = changeWorkingHoursPreferencesReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and set an error message on failure', () => {
        // given
        const action = {
            type: `${CHANGE_USER_WORKING_HOURS_PREFERENCES_ACTION_PREFIX}_failure`,
            error: "Some error message"
        }

        const state = {
            ...sampleState,
        }

        // when
        const expectedState = {
            ...sampleState,
            error: "Some error message"
        }

        const newState = changeWorkingHoursPreferencesReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })
})