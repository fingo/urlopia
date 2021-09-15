import classNames from "classnames";
import PropTypes from 'prop-types';
import {useEffect, useState} from "react";
import {Container, Nav} from 'react-bootstrap';

import {getCurrentUser} from "../../api/services/session.service";
import {useAppInfo} from "../../contexts/app-info-context/appInfoContext";
import {fetchPendingDays} from "../../contexts/vacation-days-context/actions/fetchPendingDays";
import {fetchVacationDays} from "../../contexts/vacation-days-context/actions/fetchVacationDays";
import {useVacationDays} from "../../contexts/vacation-days-context/vacationDaysContext";
import {AttentionIcon, TextWithIcon} from "../../helpers/icons/Icons";
import {Link} from "./link/Link";
import {LinkGroup} from "./link-group/LinkGroup";
import styles from './Sidebar.module.scss';

export const Sidebar = ({onClickLinkOrOutside, acceptancesPresent}) => {
    const {isAdmin: isUserAnAdmin} = getCurrentUser()
    const overlayClass = classNames(styles.overlay, 'd-lg-none');

    const [vacationDays, setVacationDays] = useState(0);
    const [vacationHours, setVacationHours] = useState(0);
    const [pendingDays, setPendingDays] = useState(0);
    const [pendingHours, setPendingHours] = useState(0);
    const [workTime, setWorkTime] = useState(8);

    const [vacationDaysState, vacationDaysDispatch] = useVacationDays();

    const [appInfoState, ] = useAppInfo()
    const {version, commitId} = appInfoState.appInfo

    useEffect(() => {
        fetchPendingDays(vacationDaysDispatch);
    }, [vacationDaysDispatch]);

    useEffect(() => {
        fetchVacationDays(vacationDaysDispatch);
    }, [vacationDaysDispatch]);

    useEffect(() => {
        const {pendingDays, pendingHours} = vacationDaysState.pendingDays;
        setPendingDays(pendingDays);
        setPendingHours(pendingHours);
    }, [vacationDaysState.pendingDays]);

    useEffect(() => {
        const {remainingDays, remainingHours, workTime} = vacationDaysState.vacationDays;
        setVacationDays(remainingDays);
        setVacationHours(remainingHours);
        setWorkTime(workTime);
    }, [vacationDaysState.vacationDays]);

    return (
        <>
            <Container fluid className={styles.main}>
                <Nav className={styles.nav}>
                    <Link to="/calendar" onClick={onClickLinkOrOutside}>Kalendarz</Link>
                    <Link to="/requests" onClick={onClickLinkOrOutside}>
                        <TextWithIcon
                            text={"Wnioski urlopowe"}
                            icon={<AttentionIcon />}
                            showIcon={acceptancesPresent}
                        />
                        <div className={styles.days}>
                            <p>Pozostały urlop: </p>
                            {
                                workTime === 8 ?
                                    <p><strong>{vacationDays-pendingDays}d</strong> {vacationHours-pendingHours}h (<strong>+{pendingDays}d</strong> {pendingHours}h)</p>
                                    :
                                    <p><strong>{vacationHours-pendingHours}h ({pendingHours}h)</strong></p>
                            }
                        </div>
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

                    <div className={styles.versionContainer}>
                        {`${version} ${commitId}`}
                    </div>
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
