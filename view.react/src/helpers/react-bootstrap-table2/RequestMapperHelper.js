import {ACCEPTED, CANCELED, PENDING, REJECTED} from "../../constants/statuses";

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

export const requestTypeMapper = (cell) => {
    switch (cell) {
        case 'NORMAL':
            return 'Wypoczynkowy';
        case 'OCCASIONAL':
            return 'Okazjonalny';
        default:
            return cell;
    }
}

export const requestPeriodFormatter = ({startDate, endDate, workingDays}) => {
    let suffix = 'dni roboczych'
    if (workingDays < 5) {
        suffix = workingDays === 1 ? 'dzień roboczy' : 'dni robocze'
    }

    return [`${startDate} - ${endDate}`, `${workingDays} ${suffix}`];
}