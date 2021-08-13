import {ACCEPTED, CANCELED, PENDING, REJECTED} from "../../constants/statuses";

export const statusMapper = (cell) => {
    switch (cell) {
        case ACCEPTED:
            cell = 'Zatwierdzony';
            return cell;
        case CANCELED:
            cell = '!!Anulowany';
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