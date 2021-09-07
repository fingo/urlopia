import PropTypes from "prop-types";
import {Modal} from "react-bootstrap";

import styles from './CalendarDayInfo.module.scss';

export const CalendarDayInfo = ({show, onHide, date, absentUsers}) => {
    return (
        <Modal
            show={show}
            onHide={onHide}
            centered
        >
            <Modal.Header closeButton>
                <Modal.Title>Osoby niedostępne w dniu: <strong>{date}</strong></Modal.Title>
            </Modal.Header>
            <Modal.Body className={`text-center`}>
                <ul className={styles.list} >
                    {
                        absentUsers.map((user, i) => {
                            return (
                                <li key={`${i}-${user.userName}`}>
                                    {user.userName} <span className={styles.muted}>({user.teams.length ? user.teams.join(', ') : '-'})</span>
                                </li>
                            )
                        })
                    }
                </ul>
            </Modal.Body>
        </Modal>
    );
};

CalendarDayInfo.propTypes = {
    show: PropTypes.bool,
    onHide: PropTypes.func.isRequired,
    date: PropTypes.string,
    absentUsers: PropTypes.array,
}

CalendarDayInfo.defaultProps = {
    show: false,
    date: 'date-unknown',
    absentUsers: [],
}
