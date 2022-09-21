import {useState} from "react";

import {EXPIRED, PENDING} from "../../constants/statuses";
import {formatRequestBadge, requestPeriodFormatter} from "../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {disableSortingFunc} from "../../helpers/react-bootstrap-table2/utils";
import {ClickablePill} from "../clickable-badge/ClickablePill";
import Table from "../table/Table";
import {AcceptancesModal, getAcceptanceBadgeFor} from "../user-requests-list/AcceptancesModal";

export const AcceptanceHistoryTab = ({acceptances, setSort}) => {
    const [modalsShow, setModalsShow] = useState({});

    const showModal = requestId => setModalsShow({...modalsShow, [requestId]: true})
    const hideModal = requestId => setModalsShow({...modalsShow, [requestId]: false})

    const modals = acceptances.map(acc => {
        let requestAcceptances = []
        for (const [leaderName, status] of Object.entries(acc.leadersAcceptances)) {
            requestAcceptances.push({leaderName, status})
        }
        return (
            <AcceptancesModal
                key={acc.requestId}
                request={{
                    acceptances: requestAcceptances
                }}
                show={modalsShow[acc.requestId]}
                onHide={() => hideModal(acc.requestId)}
            />
        )
    })

    const acceptancesButtonFor = (acceptance, onClick) => {
        if (!acceptance) {
            return null
        }

        const leadersAcceptances = acceptance.leadersAcceptances
        const numberOfLeaders = Object.keys(leadersAcceptances).length
        const numberOfExamined = Object.values(leadersAcceptances).filter(status => status !== PENDING && status !== EXPIRED).length

        return (
            <ClickablePill onClick={onClick}>
                {`(${numberOfExamined}/${numberOfLeaders})`}
            </ClickablePill>
        )
    }

    const decisionFormatter = (cell) => {
        return getAcceptanceBadgeFor(cell)
    }

    const requestStatusFormatter = (cell, row) => {
        const acceptance = acceptances.find(acc => acc.id === row.id)

        return (
            <div style={{display: "flex", justifyContent: "center"}}>
                {formatRequestBadge(cell)}
                {acceptancesButtonFor(acceptance, () => showModal(acceptance.requestId))}
            </div>
        )
    }

    const columns = [
        {
            name: 'id',
            hidden: true,
        },
        {
            name: 'requester',
            text: 'Wnioskodawca',
            headerAlign: 'center',
            align: 'center',
            sort: true,
            sortFunc: disableSortingFunc,
            onSort: (field, order) => {
                const sortField = "request.requester.firstName"
                setSort({field: sortField, order: order})
            },
            style: {verticalAlign: 'middle'},
        },
        {
            name: 'period',
            text: 'Termin',
            headerAlign: 'center',
            align: 'center',
            sort: true,
            sortFunc: disableSortingFunc,
            onSort: (field, order) => {
                const sortField = "request.startDate"
                setSort({field: sortField, order: order})
            },
            style: {verticalAlign: 'middle'},
        },
        {
            name: 'decision',
            text: 'Twoja decyzja',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle'},
            formatter: decisionFormatter,
        },
        {
            name: 'requestStatus',
            text: 'Status wniosku',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle'},
            formatter: requestStatusFormatter,
        }
    ];

    const formattedAcceptances = acceptances.map(acc => ({
        id: acc.id,
        requester: acc.requesterName,
        period: requestPeriodFormatter(acc),
        decision: acc.status,
        requestStatus: acc.requestStatus
    }))

    return (
        <>
            {modals}
            <Table
                keyField='id'
                data={formattedAcceptances}
                columns = {columns}
                hover
            />
        </>
    );
}