import {CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX} from "../constants";
import {changeAcceptanceStatusReducer} from "./changeAcceptanceStatus";

describe('changeAcceptanceStatusReducer', () => {
    const sampleAcceptances = [
        {
            "id": 1,
            "requesterName": "Jan Kowalski",
            "status": "PENDING",
            "endDate": "2021-08-13",
            "startDate": "2021-08-12",
            "workingDays": 2,
        },
        {
            "id": 2,
            "requesterName": "Adam Nowak",
            "status": "PENDING",
            "endDate": "2021-08-18",
            "startDate": "2021-08-16",
            "workingDays": 3,
        }
    ]

    const sampleState = {
        myRequests: {
            fetching: false,
            error: null,
            requests: []
        },
        acceptances: {
            fetching: false,
            error: null,
            pending: sampleAcceptances
        },
        companyRequests: {
            fetching: false,
            error: null,
            requests: []
        },
        contextError: null
    }

    it('should reset context error on request', () => {
        // given
        const action = {
            type: `${CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX}_request`
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

        const newState = changeAcceptanceStatusReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should remove acceptance on success when new status is CANCELED', () => {
        // given
        const action = {
            type: `${CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX}_success`,
            response: {
                status: "CANCELED"
            },
            payload: {
                acceptanceId: 1
            }
        }

        // when
        const expectedState = {
            ...sampleState,
            acceptances: {
                ...sampleState.acceptances,
                pending: [
                    {
                        ...sampleAcceptances[1],
                    }
                ]
            }
        }

        const newState = changeAcceptanceStatusReducer(sampleState, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should update requests in each slice on success when new status is REJECTED', () => {
        // given
        const action = {
            type: `${CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX}_success`,
            response: {
                status: "REJECTED"
            },
            payload: {
                acceptanceId: 1
            }
        }

        // when
        const expectedState = {
            ...sampleState,
            acceptances: {
                ...sampleState.acceptances,
                pending: [
                    {
                        ...sampleAcceptances[1],
                    }
                ]
            },
        }

        const newState = changeAcceptanceStatusReducer(sampleState, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set a context error message on failure', () => {
        // given
        const action = {
            type: `${CHANGE_ACCEPTANCE_STATUS_ACTION_PREFIX}_failure`,
            error: "Some error message"
        }

        // when
        const expectedState = {
            ...sampleState,
            contextError: "Some error message"
        }

        const newState = changeAcceptanceStatusReducer(sampleState, action)

        // then
        return expect(newState).toEqual(expectedState)
    })
})
