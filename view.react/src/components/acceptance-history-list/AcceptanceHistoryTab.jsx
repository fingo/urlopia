import {useState} from "react";
import BootstrapTable from "react-bootstrap-table-next";

import {EXPIRED, PENDING} from "../../constants/statuses";
import {formatRequestBadge, requestPeriodFormatter} from "../../helpers/react-bootstrap-table2/RequestMapperHelper";
import {tableClass} from "../../helpers/react-bootstrap-table2/tableClass";
import {ClickablePill} from "../clickable-badge/ClickablePill";
import {AcceptancesModal, getAcceptanceBadgeFor} from "../user-requests-list/AcceptancesModal";

export const AcceptanceHistoryTab = ({acceptances}) => {
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
            dataField: 'id',
            hidden: true,
        },
        {
            dataField: 'requester',
            text: 'Wnioskodawca',
            headerAlign: 'center',
            align: 'center',
            sort: true,
            style: {verticalAlign: 'middle'},
        },
        {
            dataField: 'period',
            text: 'Termin',
            headerAlign: 'center',
            align: 'center',
            sort: true,
            style: {verticalAlign: 'middle'},
        },
        {
            dataField: 'decision',
            text: 'Twoja decyzja',
            headerAlign: 'center',
            align: 'center',
            style: {verticalAlign: 'middle'},
            formatter: decisionFormatter,
        },
        {
            dataField: 'requestStatus',
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
            <BootstrapTable
                bootstrap4
                keyField='id'
                data={formattedAcceptances}
                wrapperClasses={tableClass}
                columns = {columns}
                bordered={false}
                hover
            />
        </>
    );
}