import {CHANGE_REQUEST_STATUS_ACTION_PREFIX} from "../constants";
import {changeRequestStatusReducer} from "./changeRequestStatus";

describe('changeRequestStatusReducer', () => {
    const sampleRequests = [
        {
            "id": 1,
            "type": "NORMAL",
            "requesterName": "Jan Kowalski",
            "status": "ACCEPTED",
            "endDate": "2021-08-13",
            "startDate": "2021-08-12",
            "workingDays": 2,
            "acceptances": []
        },
        {
            "id": 2,
            "type": "NORMAL",
            "requesterName": "Jan Kowalski",
            "status": "PENDING",
            "endDate": "2021-08-18",
            "startDate": "2021-08-16",
            "workingDays": 3,
            "acceptances": []
        }
    ]

    const sampleState = {
        myRequests: {
            fetching: false,
            error: null,
            requests: sampleRequests
        },
        teamRequests: {
            fetching: false,
            error: null,
            requests: sampleRequests
        },
        companyRequests: {
            fetching: false,
            error: null,
            requests: sampleRequests
        },
        workerRequestsHistory: {
            fetching: false,
            error: null,
            requests: []
        },
        contextError: null
    }

    it('should reset context error on request', () => {
        // given
        const action = {
            type: `${CHANGE_REQUEST_STATUS_ACTION_PREFIX}_request`
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

        const newState = changeRequestStatusReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should update requests in each slice on success when new status is CANCELED', () => {
        // given
        const action = {
            type: `${CHANGE_REQUEST_STATUS_ACTION_PREFIX}_success`,
            response: {
                status: "CANCELED"
            },
            payload: {
                requestId: 1
            }
        }

        // when
        const expectedState = {
            ...sampleState,
            myRequests: {
                ...sampleState.myRequests,
                requests: [
                    {
                        ...sampleRequests[1],
                    }
                ]
            },
            teamRequests: {
                ...sampleState.teamRequests,
                requests: [
                    {
                        ...sampleRequests[1],
                    }
                ]
            },
            companyRequests: {
                ...sampleState.companyRequests,
                requests: [
                    {
                        ...sampleRequests[1],
                    }
                ]
            },
            workerRequestsHistory: {
                ...sampleState.workerRequestsHistory,
            },
        }

        const newState = changeRequestStatusReducer(sampleState, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should update requests in each slice on success when new status is ACCEPTED', () => {
        // given
        const action = {
            type: `${CHANGE_REQUEST_STATUS_ACTION_PREFIX}_success`,
            response: {
                status: "ACCEPTED"
            },
            payload: {
                requestId: 2
            }
        }

        // when
        const expectedState = {
            ...sampleState,
            myRequests: {
                ...sampleState.myRequests,
                requests: [
                    {
                        ...sampleRequests[0]
                    }
                ]
            },
            teamRequests: {
                ...sampleState.teamRequests,
                requests: [
                    {
                        ...sampleRequests[0],
                    }
                ]
            },
            companyRequests: {
                ...sampleState.companyRequests,
                requests: [
                    {
                        ...sampleRequests[0],
                    }
                ]
            },
            workerRequestsHistory: {
                ...sampleState.workerRequestsHistory,
            },
        }

        const newState = changeRequestStatusReducer(sampleState, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should update requests in each slice on success when new status is REJECTED', () => {
        // given
        const action = {
            type: `${CHANGE_REQUEST_STATUS_ACTION_PREFIX}_success`,
            response: {
                status: "REJECTED"
            },
            payload: {
                requestId: 2
            }
        }

        // when
        const expectedState = {
            ...sampleState,
            myRequests: {
                ...sampleState.myRequests,
                requests: [
                    {
                        ...sampleRequests[0]
                    }
                ]
            },
            teamRequests: {
                ...sampleState.teamRequests,
                requests: [
                    {
                        ...sampleRequests[0],
                    }
                ]
            },
            companyRequests: {
                ...sampleState.companyRequests,
                requests: [
                    {
                        ...sampleRequests[0],
                    }
                ]
            },
            workerRequestsHistory: {
                ...sampleState.workerRequestsHistory,
            },
        }

        const newState = changeRequestStatusReducer(sampleState, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set a context error message on failure', () => {
        // given
        const action = {
            type: `${CHANGE_REQUEST_STATUS_ACTION_PREFIX}_failure`,
            error: "Some error message"
        }

        // when
        const expectedState = {
            ...sampleState,
           contextError: "Some error message"
        }

        const newState = changeRequestStatusReducer(sampleState, action)

        // then
        return expect(newState).toEqual(expectedState)
    })
})