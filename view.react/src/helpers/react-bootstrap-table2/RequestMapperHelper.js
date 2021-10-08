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
    let color;
    switch (cell) {
        case ACCEPTED:
            cell = 'Zatwierdzony';
            color = 'success';
            break;
        case CANCELED:
            cell = 'Anulowany';
            color = 'secondary';
            break;
        case PENDING:
            cell = 'Oczekujący';
            color = 'warning';
            break;
        case REJECTED:
            cell = 'Odrzucony';
            color = 'danger';
            break;
        default:
            color = 'primary';
    }

    const request = requests.find(req => req.id === row.id)

    return (
        <div style={{display: "flex", justifyContent: "center"}}>
            <Badge pill bg={color}>{cell}</Badge>
            {acceptancesButtonFor(request, showModal)}
        </div>
    )
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