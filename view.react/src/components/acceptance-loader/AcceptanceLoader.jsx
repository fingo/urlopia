import {useEffect, useState} from "react";

import {fetchAcceptances} from "../../contexts/request-context/actions/fetchAcceptances";
import {useRequests} from "../../contexts/request-context/requestContext";

export const AcceptanceLoader = ({setNewAcceptancesPresent}) => {
    const [requestsState, requestsDispatch] = useRequests()
    const [acceptancesFetched, setAcceptancesFetched] = useState(false)
    const {fetching: fetchingAcceptances, requests: acceptances} = requestsState.teamRequests

    useEffect(() => {
        fetchAcceptances(requestsDispatch)
    }, [requestsDispatch])

    useEffect(() => {
        if (!acceptancesFetched && fetchingAcceptances) {
            setAcceptancesFetched(true)
        }
    }, [fetchingAcceptances, setAcceptancesFetched, acceptancesFetched])

    useEffect(() => {
        const initialDataIsFetched = !fetchingAcceptances && acceptancesFetched
        if (initialDataIsFetched) {
            const oldCount = parseInt(localStorage.getItem("acceptancesCount"))
            const newCount = acceptances.length
            if (newCount > oldCount) {
                setNewAcceptancesPresent(true)
            }
            localStorage.setItem("acceptancesCount", newCount)
        }
    }, [fetchingAcceptances, acceptancesFetched, acceptances, setNewAcceptancesPresent])

    return null;
}