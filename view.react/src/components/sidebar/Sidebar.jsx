import classNames from "classnames";
import PropTypes from 'prop-types';
import {
    Container,
    Nav
} from 'react-bootstrap';

import {getCurrentUser} from "../../api/services/session.service";
import {AttentionIcon, TextWithIcon} from "../../helpers/icons/Icons";
import {Link} from "./link/Link";
import {LinkGroup} from "./link-group/LinkGroup";
import styles from './Sidebar.module.scss';

export const Sidebar = ({onClickLinkOrOutside, newAcceptancesPresent}) => {
    const {isAdmin: isUserAnAdmin} = getCurrentUser()
    const overlayClass = classNames(styles.overlay, 'd-lg-none');
    return (
        <>
            <Container fluid className={styles.main}>
                <Nav className={styles.nav}>
                    <Link to="/calendar" onClick={onClickLinkOrOutside}>Kalendarz</Link>
                    <Link to="/requests" onClick={onClickLinkOrOutside}>
                        <TextWithIcon
                            text={"Wnioski urlopowe"}
                            icon={<AttentionIcon />}
                            showIcon={newAcceptancesPresent}
                        />
                    </Link>
                    <Link to="/history" onClick={onClickLinkOrOutside}>Historia użytkownika</Link>
                    {isUserAnAdmin && (
                        <>
                            <LinkGroup name="Konfiguracja aplikacji">
                                <Link to="/workers" onClick={onClickLinkOrOutside}>Pracownicy</Link>
                                <Link to="/associates" onClick={onClickLinkOrOutside}>Współpracownicy</Link>
                                <Link to="/holidays" onClick={onClickLinkOrOutside}>Dni świąteczne</Link>
                            </LinkGroup>
                            <Link to="/reports" onClick={onClickLinkOrOutside}>Raporty</Link>
                        </>
                    )}
                </Nav>
            </Container>

            <div className={overlayClass}
                 onClick={onClickLinkOrOutside}/>
        </>
    );
}

Sidebar.propTypes = {
    onClickLinkOrOutside: PropTypes.func,
    newAcceptancesPresent: PropTypes.bool
}

Sidebar.defaultProps = {
    onClickLinkOrOutside: () => null,
    newAcceptancesPresent: false
}
