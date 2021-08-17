import {FETCH_MY_REQUESTS_ACTION_PREFIX} from "../constants";
import {fetchMyRequestsReducer} from "./fetchMyRequests";

describe('cancelMyRequestReducer', () => {
    const sampleState = {
        fetching: false,
        error: null,
        requests: []
    }

    it('should set fetching flag to true and reset error on request', () => {
        // given
        const action = {
            type: `${FETCH_MY_REQUESTS_ACTION_PREFIX}_request`
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

        const newState = fetchMyRequestsReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and add requests on success', () => {
        // given
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

        const action = {
            type: `${FETCH_MY_REQUESTS_ACTION_PREFIX}_success`,
            response: {
                content: sampleRequests
            }
        }

        const state = {
            ...sampleState,
            fetching: true
        }

        // when
        const expectedState = {
            ...sampleState,
            fetching: false,
            requests: sampleRequests
        }

        const newState = fetchMyRequestsReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and set an error message on failure', () => {
        // given
        const action = {
            type: `${FETCH_MY_REQUESTS_ACTION_PREFIX}_failure`,
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

        const newState = fetchMyRequestsReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })
})