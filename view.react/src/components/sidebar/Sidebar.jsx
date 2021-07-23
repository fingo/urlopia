import classNames from "classnames";
import PropTypes from 'prop-types';
import {
    Container,
    Nav
} from 'react-bootstrap';

import {Link} from "./link/Link";
import {LinkGroup} from "./linkGroup/LinkGroup";
import styles from './Sidebar.module.scss';

export const Sidebar = ({onClickOutside}) => {
    const overlayClass = classNames(styles.overlay, 'd-lg-none');
    return (
        <>
            <Container fluid className={styles.main}>
                <Nav className={styles.nav}>
                    <Link to="/calendar">Kalendarz</Link>
                    <Link to="/requests">Wnioski urlopowe</Link>
                    <Link to="/history">Historia nieobecności</Link>
                    <LinkGroup name="Konfiguracja aplikacji">
                        <Link to="/workers">Pracownicy</Link>
                        <Link to="/associates">Współpracownicy</Link>
                        <Link to="/holidays">Dni świąteczne</Link>
                    </LinkGroup>
                    <Link to="/reports">Raporty</Link>
                </Nav>
            </Container>

            <div className={overlayClass}
                 onClick={onClickOutside}/>
        </>
    );
}

Sidebar.propTypes = {
    onClickOutside: PropTypes.func.isRequired,
}
