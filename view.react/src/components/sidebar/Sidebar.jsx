import ArrowForwardIosRoundedIcon from '@mui/icons-material/ArrowForwardIosRounded';
import BarChartOutlinedIcon from '@mui/icons-material/BarChartOutlined';
import CalendarMonthSharpIcon from '@mui/icons-material/CalendarMonthSharp';
import EmailRoundedIcon from '@mui/icons-material/EmailRounded';
import EventAvailableOutlinedIcon from '@mui/icons-material/EventAvailableOutlined';
import EventRepeatOutlinedIcon from '@mui/icons-material/EventRepeatOutlined';
import GroupAddOutlinedIcon from '@mui/icons-material/GroupAddOutlined';
import PeopleOutlineRoundedIcon from '@mui/icons-material/PeopleOutlineRounded';
import WatchLaterRoundedIcon from '@mui/icons-material/WatchLaterRounded';
import WorkHistoryOutlinedIcon from '@mui/icons-material/WorkHistoryOutlined';
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
                        <ArrowForwardIosRoundedIcon />
                    </button>
                    <Link 
                        onClick={onClickLinkOrOutside}
                        exact
                        activeClassName="active"
                        to="/calendar"
                    >
                        <CalendarMonthSharpIcon />
                        <span>Kalendarz</span>
                    </Link>
                    <Link 
                        onClick={onClickLinkOrOutside}
                        exact
                        activeClassName="active"
                        to="/requests"
                    >
                        <EmailRoundedIcon />
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
                        <WatchLaterRoundedIcon />
                        <span>Historia użytkownika</span>
                    </Link>
                    {isUserALeader &&
                        <Link to="/acceptances/history" onClick={onClickLinkOrOutside}>
                            <WorkHistoryOutlinedIcon />
                            <span>Historia akceptacji</span>
                        </Link>
                    }
                    {isUserAnAdmin && (
                        <>
                            <Link to="/associates" onClick={onClickLinkOrOutside}>
                            <PeopleOutlineRoundedIcon />
                                <span>Pracownicy</span>
                            </Link>
                            <Link to="/workers" onClick={onClickLinkOrOutside}>
                                <GroupAddOutlinedIcon />
                                <span>Pracownicy</span>
                            </Link>
                            <Link to="/holidays" onClick={onClickLinkOrOutside}>
                                <EventAvailableOutlinedIcon />
                                <span>Dni świąteczne</span>
                            </Link>
                            <Link to="/reports" onClick={onClickLinkOrOutside}>
                                <BarChartOutlinedIcon />
                                <span>Raporty</span>
                            </Link>
                            <Link to="/automaticVacationDays" onClick={onClickLinkOrOutside}>
                                <EventRepeatOutlinedIcon />
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
