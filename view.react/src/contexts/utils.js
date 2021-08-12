/**
 * Creates a reducer that forwards actions to suitable reducers defined in a
 * **reducerMappings** param which holds action type prefixes as keys and
 * forward data as values.
 *
 * If your initial state looks like this:
 *
 * ```javascript
 * const initialState = {
 *     your: {
 *         state: {
 *             slice: {
 *                 p1: "v1",
 *                 p2: "v2",
 *                 p3: "v3"
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * Then if you want your reducer to modify only part of the state you have to
 * pass your reducer with **slicePath** property:
 *
 * ```javascript
 * const reducerMappings = {
 *     "action/type/prefix": {
 *         slicePath: "your.state.slice",
 *         reducer: yourReducer
 *     }
 * }
 * ```
 *
 * But if you are modifying the whole state then setting your reducer as the only
 * value is also valid:
 *
 * ```javascript
 * const reducerMappings = {
 *     "action/type/prefix": yourReducer
 * }
 * ```
 *
 * @param   {Object} reducerMappings
 * @returns {(function(Object, Object): (Object))}
 * @throws  {Error} if no suitable reducer was found
 */
export const createForwardingReducer = (reducerMappings) => (state, action) => {
    for (const [actionTypePrefix, values] of Object.entries(reducerMappings)) {
        if (action.type.startsWith(actionTypePrefix)) {
            const isValueReducer = values instanceof Function
            const forwardInfo = isValueReducer ? {reducer: values} : values
            return forwardAction(state, action, forwardInfo)
        }
    }
    throw new Error(`Unhandled action type: ${action.type}`)
}

const forwardAction = (state, action, {slicePath, reducer}) => {
    return slicePath ? updateSlice(state, action, slicePath, reducer) : reducer(state, action)
}

const updateSlice = (state, action, slicePath, reducer) => {
    if (!slicePath.includes('.')) {
        return {
            ...state,
            [slicePath]: reducer(state[slicePath], action)
        }
    } else {
        const idx = slicePath.indexOf('.')
        const currentProperty = slicePath.substring(0, idx)
        const nestedSlicePath = slicePath.substring(idx + 1)
        return {
            ...state,
            [currentProperty]: updateSlice(state[currentProperty], action, nestedSlicePath, reducer)
        }
    }
}