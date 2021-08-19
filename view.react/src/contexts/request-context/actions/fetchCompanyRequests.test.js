import {FETCH_COMPANY_REQUESTS_ACTION_PREFIX} from "../constants";
import {fetchCompanyRequestsReducer} from "./fetchCompanyRequests";

describe('cancelMyRequestReducer', () => {
    const sampleState = {
        fetching: false,
        error: null,
        requests: []
    }

    it('should set fetching flag to true and reset error on request', () => {
        // given
        const action = {
            type: `${FETCH_COMPANY_REQUESTS_ACTION_PREFIX}_request`
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

        const newState = fetchCompanyRequestsReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and add pending requests on success', () => {
        // given
        const sampleRequests = [
            {
                "id": 1,
                "type": "NORMAL",
                "requesterName": "Jan Kowalski",
                "status": "PENDING",
                "endDate": "2021-08-13",
                "startDate": "2021-08-12",
                "workingDays": 2,
                "acceptances": [{
                    "requesterName": "Adam Nowak",
                    "leaderName": "Mariusz Pudzianowski",
                    "status": "PENDING"
                }]
            },
            {
                "id": 2,
                "type": "NORMAL",
                "requesterName": "Adam Nowak",
                "status": "PENDING",
                "endDate": "2021-08-18",
                "startDate": "2021-08-16",
                "workingDays": 3,
                "acceptances": [{
                    "requesterName": "Adam Nowak",
                    "leaderName": "Radek Marek",
                    "status": "PENDING"
                }]
            }
        ]

        const action = {
            type: `${FETCH_COMPANY_REQUESTS_ACTION_PREFIX}_success`,
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

        const newState = fetchCompanyRequestsReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and set an error message on failure', () => {
        // given
        const action = {
            type: `${FETCH_COMPANY_REQUESTS_ACTION_PREFIX}_failure`,
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

        const newState = fetchCompanyRequestsReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })
})