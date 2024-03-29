import {useEffect, useState} from "react";
import {Container, Navbar} from 'react-bootstrap';
import {GearFill as GearIcon, List as ListIcon, Power as PowerIcon} from "react-bootstrap-icons";

import {getFullUserName, getUserTeams} from "../../api/services/session.service";
import logoImg from '../../assets/logo.svg';
import UrlopiaLogo from '../../assets/logo-urlopia.png';
import {fetchWorkingHoursPreferences} from "../../contexts/user-preferences-context/actions/fetchWorkingHoursPreferences";
import {useUserPreferences} from "../../contexts/user-preferences-context/userPreferencesContext";
import { isNoAuthMode, logout } from "../../helpers/authentication/LogoutHelper";
import {PreferencesModal} from "../preferences-modal/PreferencesModal";
import { NoAuthUsersDropdown } from "./noauth-users-dropdown/NoAuthUsersDropdown";
import {TeamDropdown} from "./team-dropdown/TeamDropdown";
import styles from './TopBar.module.scss';

export const TopBar = () => {
    const userName = getFullUserName();
    const teams = getUserTeams();

    const [showModal, setShowModal] = useState(false);

    const [, userPreferencesDispatch] = useUserPreferences()

    useEffect(() => {
        fetchWorkingHoursPreferences(userPreferencesDispatch)
    }, [userPreferencesDispatch])

    const handleLogout = () => logout();
    const isNoAuth = isNoAuthMode();

    return (
        <>
            <PreferencesModal
                show={showModal}
                onHide={() => setShowModal(false)}
                modalTitle={"Preferencje użytkownika"}
                onClick={() => setShowModal(false)}
            />
            <Navbar className={styles.topBar} variant="dark" expand="lg" collapseOnSelect sticky='top'>
                <Container fluid className='px-0'>
                    <Navbar.Brand href='/#/calendar' className="d-none d-lg-block">
                        <img src={logoImg} alt="FINGO logo" className={styles.brandLogo}/>
                    </Navbar.Brand>

                    <img src={UrlopiaLogo} alt={"Urlopia"} className={styles.appLogo}/>
                    <div className={styles.mobileRightSide}>
                        {isNoAuth && <NoAuthUsersDropdown />}
                        <button type="button" className={styles.settingsButton} onClick={() => setShowModal(true)}>
                            <GearIcon className={styles.settingsIcon} size={20}/>
                        </button>
                        <TeamDropdown userName={userName} teams={teams}/>
                        <button type="button" className={styles.button} onClick={handleLogout}>
                            <PowerIcon className={styles.icon}/>
                        </button>
                    </div>
                </Container>
            </Navbar>
        </>
    );
}
