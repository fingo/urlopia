import {createForwardingReducer} from "./utils";

describe('createForwardingReducer', () => {
    const sampleState = {
        a1: 1,
        a2: {
            b1: {
                c1: 1,
                c2: 2,
                c3: {
                    d1: 1,
                    d2: 2
                }
            },
            b2: {}
        }
    }

    it('should pass whole state to subreducer when no slicePath is given', () => {
        // given
        const subreducer = (state, action) => {
            if (action.type === "action/prefix/do_something") {
                return {
                    ...state,
                    a2: {
                        ...state.a2,
                        b1: {
                            ...state.a2.b1,
                            c3: {
                                ...state.a2.b1.c3,
                                d2: 999
                            }
                        }
                    }
                }
            }
            fail("Unhandled action type")
        }

        const mappings = {
            "action/prefix": {
                reducer: subreducer
            }
        }

        // when
        const forwardingReducer = createForwardingReducer(mappings)

        // then
        const expectedState = {...sampleState}
        expectedState.a2.b1.c3.d2 = 999

        const actualState = forwardingReducer(sampleState, {type: "action/prefix/do_something"})

        return expect(actualState).toEqual(expectedState)
    })

    it('should pass only slice of the state to subreducer when slicePath is given', () => {
        // given
        const subreducer = (state, action) => {
            if (action.type === "action/prefix/do_something") {
                return {
                    ...state,
                    d2: 999
                }
            }
            fail("Unhandled action type")
        }

        const mappings = {
            "action/prefix": {
                slicePath: "a2.b1.c3",
                reducer: subreducer
            }
        }

        // when
        const forwardingReducer = createForwardingReducer(mappings)

        // then
        const expectedState = {...sampleState}
        expectedState.a2.b1.c3.d2 = 999

        const actualState = forwardingReducer(sampleState, {type: "action/prefix/do_something"})

        return expect(actualState).toEqual(expectedState)
    })

    it('should allow for specifying subreducer as the only value in mappings', () => {
        // given
        const subreducer = (state, action) => {
            if (action.type === "action/prefix/do_something") {
                return {
                    ...state,
                    a1: 999
                }
            }
            fail("Unhandled action type")
        }

        const mappings = {
            "action/prefix": subreducer
        }

        // when
        const forwardingReducer = createForwardingReducer(mappings)

        // then
        const expectedState = {...sampleState}
        expectedState.a1 = 999

        const actualState = forwardingReducer(sampleState, {type: "action/prefix/do_something"})

        return expect(actualState).toEqual(expectedState)
    })
})