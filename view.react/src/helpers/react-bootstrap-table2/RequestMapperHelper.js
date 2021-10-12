import {Badge} from "react-bootstrap";

import {ClickablePill} from "../../components/clickable-badge/ClickablePill";
import {ACCEPTED, CANCELED, EXPIRED, PENDING, REJECTED} from "../../constants/statuses";

export const requestStatusMapper = (cell) => {
    switch (cell) {
        case ACCEPTED:
            cell = 'Zatwierdzony';
            return cell;
        case CANCELED:
            cell = 'Anulowany';
            return cell;
        case PENDING:
            cell = 'Oczekujący';
            return cell;
        case REJECTED:
            cell = 'Odrzucony';
            return cell;
        default:
            return cell;
    }
}

export const statusFormatter = (cell, row, requests, showModal) => {
    const request = requests.find(req => req.id === row.id)

    return (
        <div style={{display: "flex", justifyContent: "center"}}>
            {formatRequestBadge(cell)}
            {acceptancesButtonFor(request, showModal)}
        </div>
    )
}

export const formatRequestBadge = (status) => {
    let color;
    let text;

    switch (status) {
        case ACCEPTED:
            text = 'Zatwierdzony';
            color = 'success';
            break;
        case CANCELED:
            text = 'Anulowany';
            color = 'secondary';
            break;
        case PENDING:
            text = 'Oczekujący';
            color = 'warning';
            break;
        case REJECTED:
            text = 'Odrzucony';
            color = 'danger';
            break;
        default:
            color = 'primary';
    }

    return <Badge pill bg={color}>{text}</Badge>
}

const acceptancesButtonFor = (request, showModal) => {
    if (!request) {
        return null;
    }

    const numberOfLeaders = request.acceptances.length
    const numberOfExamined = request.acceptances.filter(acc => acc.status !== PENDING && acc.status !== EXPIRED).length

    return (
        <ClickablePill onClick={() => showModal(request.id)}>
            {`(${numberOfExamined}/${numberOfLeaders})`}
        </ClickablePill>
    )
}

export const requestTypeMapper = (cell) => {
    switch (cell) {
        case 'NORMAL':
            return 'Wypoczynkowy';
        case 'OCCASIONAL':
            return 'Okolicznościowy';
        case 'SPECIAL':
            return 'Specjalny';
        default:
            return cell;
    }
}

export const requestPeriodFormatter = ({startDate, endDate, workingDays}) => {
    let suffix = 'dni roboczych'
    if (workingDays < 5) {
        suffix = workingDays === 1 ? 'dzień roboczy' : 'dni robocze'
    }

    return `${startDate} - ${endDate} (${workingDays} ${suffix})`;
}

export const textAsArrayFormatter = (examinersArr) => {
    let str = '';
    examinersArr.map((examiner, i) => {
        return i === examinersArr.length - 1 ?
            str = str.concat(`${examiner}`)
            :
            str = str.concat(`${examiner}, `)
    });
    return str;
}