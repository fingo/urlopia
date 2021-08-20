import {CREATE_REQUEST_ACTION_PREFIX} from "../constants";
import {createRequestReducer} from './createRequest';

describe('createRequestReducer', () => {
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
        contextError: null
    }

    it('should reset context error on request', () => {
        // given
        const action = {
            type: `${CREATE_REQUEST_ACTION_PREFIX}_request`
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

        const newState = createRequestReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set a context error message on failure', () => {
        // given
        const action = {
            type: `${CREATE_REQUEST_ACTION_PREFIX}_failure`,
            error: "Some error message"
        }

        // when
        const expectedState = {
            ...sampleState,
            contextError: "Some error message"
        }

        const newState = createRequestReducer(sampleState, action)

        // then
        return expect(newState).toEqual(expectedState)
    })
})