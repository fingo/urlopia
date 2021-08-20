import {FETCH_HOLIDAYS_ACTION_PREFIX} from "../constants";
import {fetchHolidaysReducer} from "./fetchHolidays";

describe('fetchHolidaysReducer', () => {
    const sampleState = {
        fetching: false,
        error: null,
        holidays: [],
    }

    it('should set fetching flag to true and reset error on request', () => {
        // given
        const action = {
            type: `${FETCH_HOLIDAYS_ACTION_PREFIX}_request`
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

        const newState = fetchHolidaysReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and add requests on success', () => {
        // given
        const sampleHolidays = [
            {
                "id": 28,
                "name": "Nowy Rok",
                "date": "2021-01-01"
            },
            {
                "id": 31,
                "name": "Poniedziałek Wielkanocny",
                "date": "2021-04-05"
            },
        ]

        const action = {
            type: `${FETCH_HOLIDAYS_ACTION_PREFIX}_success`,
            response: sampleHolidays,
        }

        const state = {
            ...sampleState,
            fetching: true,
        }

        // when
        const expectedState = {
            ...sampleState,
            fetching: false,
            holidays: sampleHolidays,
        }

        const newState = fetchHolidaysReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and set an error message on failure', () => {
        // given
        const action = {
            type: `${FETCH_HOLIDAYS_ACTION_PREFIX}_failure`,
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

        const newState = fetchHolidaysReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })
})