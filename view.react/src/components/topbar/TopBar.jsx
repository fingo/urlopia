import classNames from "classnames";
import PropTypes from 'prop-types';
import {Container, Navbar} from 'react-bootstrap';
import {List as ListIcon, Power as PowerIcon} from "react-bootstrap-icons";
import {useHistory} from "react-router-dom";

import {logout} from "../../api/services/session.service";
import logoImg from '../../assets/logo.png';
import {TeamDropdown} from "./team-dropdown/TeamDropdown";
import styles from './TopBar.module.scss';

export const TopBar = ({
       userName,
       teams,
       onHamburgerClick,
}) => {
    const history = useHistory();

    const handleLogout = () => {
        logout();
        history.go(0);
    }

    const listBtnClass = classNames('d-lg-none', styles.button);
    const listIconClass = classNames(styles.hamburger, styles.icon);
    return (
        <Navbar className={styles.topBar} variant="dark" expand="lg" collapseOnSelect sticky='top'>
            <Container fluid className='px-0'>
                <button
                    type="button"
                    className={listBtnClass}
                    onClick={onHamburgerClick}
                >
                    <ListIcon className={listIconClass} />
                </button>

                <Navbar.Brand href='/calendar' className="d-none d-lg-block">
                    <img src={logoImg} alt="FINGO logo"/>
                </Navbar.Brand>

                <h1 style={{color: 'white'}}>Urlopia</h1>

                <div className={styles.mobileRightSide}>
                    <TeamDropdown userName={userName} teams={teams}/>

                    <button type="button" className={styles.button} onClick={handleLogout}>
                        <PowerIcon className={styles.icon} />
                    </button>
                </div>
            </Container>
        </Navbar>
    );
}

TopBar.propTypes = {
    userName: PropTypes.string.isRequired,
    teams: PropTypes.arrayOf(
        PropTypes.shape({
            name: PropTypes.string,
            leader: PropTypes.string,
        })).isRequired,
    onHamburgerClick: PropTypes.func.isRequired,
}