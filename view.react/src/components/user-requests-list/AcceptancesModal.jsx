import {Badge, Modal} from "react-bootstrap";

import {ACCEPTED, EXPIRED, PENDING, REJECTED} from "../../constants/statuses";
import styles from './AcceptancesModal.module.scss'

export const AcceptancesModal = ({request, show, onHide}) => {
    const title = "Akceptacje wniosku"
    const acceptances = request.acceptances

    return (
        <Modal
            show={show}
            size="md"
            centered
            onHide={onHide}
        >
            <Modal.Header closeButton>
                <Modal.Title>
                    {title}
                </Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {acceptances.map((acc, idx) => (
                    <div key={idx} className={styles.row}>
                        <div className={styles.col}>
                            {acc.leaderName}
                        </div>
                        <div className={styles.col}>
                            {getAcceptanceBadgeFor(acc.status)}
                        </div>
                    </div>
                ))}
            </Modal.Body>
        </Modal>
    );
}

export const getAcceptanceBadgeFor = (status) => {
    let color;
    let text;

    switch (status) {
        case ACCEPTED:
            color = 'success';
            text = 'Wniosek zatwierdzony';
            break;
        case REJECTED:
            color = 'danger';
            text = 'Wniosek odrzucony';
            break;
        case PENDING:
            color = 'warning';
            text = 'Oczekiwanie na decyzję';
            break;
        case EXPIRED:
            color = 'secondary';
            text = 'Akceptacja wygasła'
            break;
        default:
            color = 'secondary';
            text = ''
    }

    return (
        <Badge pill bg={color}>
            {text}
        </Badge>
    )
}
