import {FETCH_USERS_VACATIONS_ACTION_PREFIX} from "../constants";
import {fetchUsersVacationsReducer} from "./fetchUsersVacations";

describe('fetchUsersVacationsReducer', () => {
    const sampleState = {
        fetching: false,
        error: null,
        usersVacations: {}
    }

    const sampleResponse = {
        usersVacations: {
            "2021-09-30": [4],
            "2021-10-28": [4, 3, 2],
            "2021-10-27": [4, 1],
        }
    }

    it('should set fetching flag to true and reset error on request', () => {
        // given
        const action = {
            type: `${FETCH_USERS_VACATIONS_ACTION_PREFIX}_request`
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

        const newState = fetchUsersVacationsReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and add users vacations on success', () => {
        // given
        const action = {
            type: `${FETCH_USERS_VACATIONS_ACTION_PREFIX}_success`,
            response: sampleResponse
        }

        const state = {
            ...sampleState,
            fetching: true
        }

        // when
        const expectedState = {
            ...sampleState,
            fetching: false,
            usersVacations: sampleResponse.usersVacations
        }

        const newState = fetchUsersVacationsReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })

    it('should set fetching flag to false and set an error message on failure', () => {
        // given
        const action = {
            type: `${FETCH_USERS_VACATIONS_ACTION_PREFIX}_failure`,
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

        const newState = fetchUsersVacationsReducer(state, action)

        // then
        return expect(newState).toEqual(expectedState)
    })
})
