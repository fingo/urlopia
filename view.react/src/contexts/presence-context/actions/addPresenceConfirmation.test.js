import {USER_DATA_KEY} from "../../../constants/session.keystorage";
import {mockLocalStorage} from "../../../helpers/TestHelper";
import {ADD_PRESENCE_CONFIRMATION_ACTION_PREFIX} from "../constants";
import {addPresenceConfirmationReducer} from "./addPresenceConfirmation";

describe("addPresenceConfirmationReducer", () => {
    const sampleState = {
        myConfirmations: {
            fetching: false,
            error: null,
            confirmations: {}
        },
        usersConfirmations: {
            fetching: false,
            error: null,
            confirmations: {}
        }
    }

    const sessionStorageMock = mockLocalStorage()

    beforeEach(() => {
        sessionStorageMock.clear()
    })

    it('should return same state on request', () => {
        // given
        const action = {
            type: `${ADD_PRESENCE_CONFIRMATION_ACTION_PREFIX}_request`
        }

        const state = {
            ...sampleState,
            contextError: "Some error message"
        }

        // when
        const expectedState = {
            ...sampleState,
            contextError: null
        }
        const newState = addPresenceConfirmationReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should add confirmation only to my confirmations slice when user is not admin on success', () => {
        // given
        const action = {
            type: `${ADD_PRESENCE_CONFIRMATION_ACTION_PREFIX}_success`,
            response: {
                date: "2021-08-23",
                startTime: "08:00",
                endTime: "16:00",
                userId: 1
            }
        }

        const state = {
            ...sampleState,
            myConfirmations: {
                confirmations: {
                    "2021-08-19": createSampleConfirmation("2021-08-19", 1),
                    "2021-08-20": createSampleConfirmation("2021-08-20", 1),
                }
            }
        }

        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            userId: 1,
            roles: ["ROLES_WORKER"]
        }))

        // when
        const expectedState = {
            ...state,
            myConfirmations: {
                confirmations: {
                    ...state.myConfirmations.confirmations,
                    "2021-08-23": {
                        date: "2021-08-23",
                        startTime: "08:00",
                        endTime: "16:00",
                        userId: 1,
                    },
                }
            }
        }

        const newState = addPresenceConfirmationReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should add confirmation to both my confirmations and users confirmations when user is admin on success', () => {
        // given
        const action = {
            type: `${ADD_PRESENCE_CONFIRMATION_ACTION_PREFIX}_success`,
            response: {
                date: "2021-08-23",
                startTime: "08:00",
                endTime: "16:00",
                userId: 1,
            }
        }

        const state = {
            ...sampleState,
            myConfirmations: {
                confirmations: {
                    "2021-08-19": createSampleConfirmation("2021-08-19", 1),
                    "2021-08-20": createSampleConfirmation("2021-08-20", 1),
                }
            },
            usersConfirmations: {
                confirmations: {
                    1: {
                        "2021-08-19": createSampleConfirmation("2021-08-19", 1),
                        "2021-08-20": createSampleConfirmation("2021-08-20", 1),
                    },
                    2: {
                        "2021-08-19": createSampleConfirmation("2021-08-19", 2),
                        "2021-08-20": createSampleConfirmation("2021-08-20", 2),
                    }
                }
            }
        }

        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            userId: 1,
            roles: ["ROLES_WORKER", "ROLES_ADMIN"]
        }))

        // when
        const expectedState = {
            ...state,
            myConfirmations: {
                confirmations: {
                    ...state.myConfirmations.confirmations,
                    "2021-08-23": createSampleConfirmation("2021-08-23", 1),
                }
            },
            usersConfirmations: {
                confirmations: {
                    ...state.usersConfirmations.confirmations,
                    1: {
                        ...state.usersConfirmations.confirmations[1],
                        "2021-08-23": {
                            date: "2021-08-23",
                            startTime: "08:00",
                            endTime: "16:00",
                            userId: 1,
                        },
                    }
                }
            }
        }

        const newState = addPresenceConfirmationReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should add confirmation only to users confirmations slice when user is admin and is confirming presence of someone else on success', () => {
        // given
        const action = {
            type: `${ADD_PRESENCE_CONFIRMATION_ACTION_PREFIX}_success`,
            response: {
                date: "2021-08-23",
                startTime: "08:00",
                endTime: "16:00",
                userId: 2,
            }
        }

        const state = {
            ...sampleState,
            myConfirmations: {
                confirmations: {
                    "2021-08-19": createSampleConfirmation("2021-08-19", 1),
                    "2021-08-20": createSampleConfirmation("2021-08-20", 1),
                }
            },
            usersConfirmations: {
                confirmations: {
                    1: {
                        "2021-08-19": createSampleConfirmation("2021-08-19", 1),
                        "2021-08-20": createSampleConfirmation("2021-08-20", 1),
                    },
                    2: {
                        "2021-08-19": createSampleConfirmation("2021-08-19", 2),
                        "2021-08-20": createSampleConfirmation("2021-08-20", 2),
                    }
                }
            }
        }

        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            userId: 1,
            roles: ["ROLES_WORKER", "ROLES_ADMIN"]
        }))

        // when
        const expectedState = {
            ...state,
            usersConfirmations: {
                confirmations: {
                    ...state.usersConfirmations.confirmations,
                    2: {
                        ...state.usersConfirmations.confirmations[2],
                        "2021-08-23": {
                            date: "2021-08-23",
                            startTime: "08:00",
                            endTime: "16:00",
                            userId: 2,
                        },
                    }
                }
            }
        }

        const newState = addPresenceConfirmationReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set an error message on failure', () => {
        // given
        const action = {
            type: `${ADD_PRESENCE_CONFIRMATION_ACTION_PREFIX}_failure`,
            error: "Some error message"
        }

        // when
        const expectedState = {
            ...sampleState,
            contextError: "Some error message"
        }

        const newState = addPresenceConfirmationReducer(sampleState, action)

        // then
        return expect(newState).toEqual(expectedState)
    })
})

const createSampleConfirmation = (date, userId) => ({
    date,
    startTime: "08:00",
    endTime: "16:00",
    userId
})