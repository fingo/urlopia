import {FETCH_USER_ABSENCE_HISTORY_PREFIX} from "../constants";
import {fetchUserAbsenceHistoryReducer} from "./fetchUserAbsenceHistory";

describe('fetchUserAbsenceHistoryReducer', () => {
    const sampleState = {
        fetching: false,
        error: null,
        absenceHistory: []
    }

    it('should set fetching flag to true and reset error on request', () => {

        const action = {
            type: `${FETCH_USER_ABSENCE_HISTORY_PREFIX}_absence-history`
        }

        const state = {
            ...sampleState,
            error: "Some error message"
        }

        const expectedState = {
            ...sampleState,
            fetching: true,
            error: null
        }

        const newState = fetchUserAbsenceHistoryReducer(state, action)

        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and add absenceHistory on success', () => {

        const sampleAbsenceHistory = [
            {
                "id": 1,
                "comment": "2021-11-12 - 2021-11-12 (Narodziny dziecka)",
                "hours": 0.0,
                "userWorkTime": 8.0,
                "created": "2021-08-20 11:53:26",
                "hoursRemaining": 0.0,
                "workTimeNumerator": 1,
                "workTimeDenominator": 1,
                "deciders": []
            },
            {
                "id": 2,
                "comment": "2021-11-12 - 2021-11-12 (Narodziny dziecka)",
                "hours": 0.0,
                "userWorkTime": 8.0,
                "created": "2021-08-20 11:53:52",
                "hoursRemaining": 0.0,
                "workTimeNumerator": 1,
                "workTimeDenominator": 1,
                "deciders": []
            }
        ]

        const action = {
            type: `${FETCH_USER_ABSENCE_HISTORY_PREFIX}_success`,
            response: sampleAbsenceHistory
        }

        const state = {
            ...sampleState,
            fetching: true
        }

        const expectedState = {
            ...sampleState,
            fetching: false,
            absenceHistory: sampleAbsenceHistory
        }

        const newState = fetchUserAbsenceHistoryReducer(state, action)

        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and set an error message on failure', () => {

        const action = {
            type: `${FETCH_USER_ABSENCE_HISTORY_PREFIX}_failure`,
            error: "Some error message"
        }

        const state = {
            ...sampleState,
            fetching: true
        }

        const expectedState = {
            ...sampleState,
            fetching: false,
            error: "Some error message"
        }

        const newState = fetchUserAbsenceHistoryReducer(state, action)

        return expect(newState).toEqual(expectedState)
    })
})