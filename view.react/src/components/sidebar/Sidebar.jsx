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
import {formatHoursToDays} from "../../helpers/RemainingDaysFormatterHelper";
import {Link} from "./link/Link";
import styles from './Sidebar.module.scss';

export const Sidebar = ({onClickLinkOrOutside, acceptancesPresent}) => {
    const {isAdmin: isUserAnAdmin, isLeader: isUserALeader} = getCurrentUser();
    const {ec: isUserEC} = getCurrentUser();
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

    const remainingDays = vacationDays-pendingDays
    const remainingHours = vacationHours - pendingHours
    const remainingHoursAsDays = formatHoursToDays(remainingHours/workTime)
    const pendingHoursAsDays  = formatHoursToDays(pendingHours/workTime)

    const leaveLabel = isUserEC ? "Pozostały urlop: " : "Pozostała przerwa: "

    const [click, setClick] = useState(false);
    const handleClick = () => setClick(!click);

    const slickBarClasses = classNames(styles.nav, {
        [styles['nav--clicked']]: click
      });

    return (
        <>
            <Container fluid className={styles.main}>
                <Nav className={slickBarClasses} clicked={click}>
                    <button clicked={click} onClick={() => handleClick()}>
                        <img src={require('../../assets/sidebar/rightArrow.svg').default} alt="rightArrow_icon"/>
                    </button>
                    <Link 
                        onClick={onClickLinkOrOutside}
                        exact
                        activeClassName="active"
                        to="/calendar"
                    >
                        <img src={require('../../assets/sidebar/calendar.svg').default} alt="calendar_icon"/>
                        <span>Kalendarz</span>
                    </Link>
                    <Link 
                        onClick={onClickLinkOrOutside}
                        exact
                        activeClassName="active"
                        to="/requests"
                    >
                        <img src={require('../../assets/sidebar/requests.svg').default} alt="requests_icon"/>
                        <TextWithIcon
                            text={isUserEC ? "Wnioski urlopowe" : "Wnioski o przerwę"}
                            icon={<AttentionIcon />}
                            showIcon={acceptancesPresent}
                        />
                    </Link>
                    <Link 
                        onClick={onClickLinkOrOutside}
                        exact
                        activeClassName="active"
                        to="/history"
                    >
                        <img src={require('../../assets/sidebar/history.svg').default} alt="history_icon"/>
                        <span>Historia użytkownika</span>
                    </Link>
                    {isUserALeader &&
                        <Link to="/acceptances/history" onClick={onClickLinkOrOutside}>
                            <img src={require('../../assets/sidebar/acceptanceHistory.svg').default} alt="acceptanceHistory_icon"/>
                            <span>Historia akceptacji</span>
                        </Link>
                    }
                    {isUserAnAdmin && (
                        <>
                            <Link to="/associates" onClick={onClickLinkOrOutside}>
                                <img src={require('../../assets/sidebar/associates.svg').default} alt="associates_icon"/>
                                <span>Pracownicy</span>
                            </Link>
                            <Link to="/workers" onClick={onClickLinkOrOutside}>
                                <img src={require('../../assets/sidebar/workers.svg').default} alt="workers_icon"/>
                                <span>Pracownicy</span>
                            </Link>
                            <Link to="/holidays" onClick={onClickLinkOrOutside}>
                                <img src={require('../../assets/sidebar/holidays.svg').default} alt="holidays_icon"/>
                                <span>Dni świąteczne</span>
                            </Link>
                            <Link to="/reports" onClick={onClickLinkOrOutside}>
                                <img src={require('../../assets/sidebar/reports.svg').default} alt="reports_icon"/>
                                <span>Raporty</span>
                            </Link>
                            <Link to="/automaticVacationDays" onClick={onClickLinkOrOutside}>
                                <img src={require('../../assets/sidebar/automaticVacationDays.svg').default} alt="automaticVacationDays_icon"/>
                                <span>Dni na nowy rok</span>
                            </Link>
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
