import {FETCH_ACCEPTANCES_ACTION_PREFIX} from "../constants";
import {fetchAcceptancesReducer} from "./fetchAcceptances";

describe('fetchAcceptancesReducer', () => {
    const sampleState = {
        fetching: false,
        error: null,
        requests: []
    }

    it('should set fetching flag to true and reset error on request', () => {
        // given
        const action = {
            type: `${FETCH_ACCEPTANCES_ACTION_PREFIX}_request`
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

        const newState = fetchAcceptancesReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and add requests on success', () => {
        // given
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

        const action = {
            type: `${FETCH_ACCEPTANCES_ACTION_PREFIX}_success`,
            response: {
                content: sampleAcceptances
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
            requests: sampleAcceptances
        }

        const newState = fetchAcceptancesReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and set an error message on failure', () => {
        // given
        const action = {
            type: `${FETCH_ACCEPTANCES_ACTION_PREFIX}_failure`,
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

        const newState = fetchAcceptancesReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })
})