import {FETCH_ACCEPTANCES_HISTORY_ACTION_PREFIX} from "../constants";
import {fetchAcceptancesHistoryReducer} from "./fetchAcceptancesHistory";

describe('fetchAcceptancesHistoryReducer', () => {
    const sampleState = {
        fetching: false,
        error: null,
        pending: [],
        history: []
    }

    it('should set fetching flag to true and reset error on request', () => {
        // given
        const action = {
            type: `${FETCH_ACCEPTANCES_HISTORY_ACTION_PREFIX}_request`
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

        const newState = fetchAcceptancesHistoryReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and add requests on success', () => {
        // given
        const sampleAcceptances = [
            {
                "id": 2,
                "requestId": 11,
                "requesterName": "Adam Mickiewicz",
                "startDate": "2021-10-19",
                "endDate": "2021-10-25",
                "workingDays": 5,
                "status": "ACCEPTED",
                "leadersAcceptances": {
                    "Mary Smith": "PENDING",
                    "Radek Marek": "ACCEPTED"
                }
            }
        ]

        const action = {
            type: `${FETCH_ACCEPTANCES_HISTORY_ACTION_PREFIX}_success`,
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
            history: sampleAcceptances
        }

        const newState = fetchAcceptancesHistoryReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and set an error message on failure', () => {
        // given
        const action = {
            type: `${FETCH_ACCEPTANCES_HISTORY_ACTION_PREFIX}_failure`,
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

        const newState = fetchAcceptancesHistoryReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })
})