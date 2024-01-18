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
import { Badge } from '@mui/material';
import classNames from "classnames";
import PropTypes from 'prop-types';
import {useState} from "react";
import {Nav} from 'react-bootstrap';

import {getCurrentUser} from "../../api/services/session.service";
import {useAppInfo} from "../../contexts/app-info-context/appInfoContext";
import {AttentionIcon, TextWithIcon} from "../../helpers/icons/Icons";
import {Link} from "./link/Link";
import styles from './Sidebar.module.scss';

export const Sidebar = ({acceptancesPresent}) => {
    const {isAdmin: isUserAnAdmin, isLeader: isUserALeader} = getCurrentUser();
    const {ec: isUserEC} = getCurrentUser();
    const overlayClass = classNames(styles.overlay, 'd-lg-none');

    const [appInfoState, ] = useAppInfo()
    const {version, commitId} = appInfoState.appInfo

    const [isOpen, setIsOpen] = useState(false);

    const sidebarClasses = classNames(styles.nav, {
        [styles['nav--open']]: isOpen
      });

    return (
        <>
            <div className={styles.main}>
                <Nav className={sidebarClasses}>
                    <button onClick={() =>  (setIsOpen(!isOpen))}>
                        <ArrowForwardIosRoundedIcon />
                    </button>
                    <Link
                        testId="CalendarLink"
                        exact
                        activeClassName="active"
                        to="/calendar"
                    >
                        <CalendarMonthSharpIcon />
                        <span className={styles['text']}>Kalendarz</span>
                    </Link>
                    <Link
                        testId="RequestsLink"
                        exact
                        activeClassName="active"
                        to="/requests"
                    >
                        <Badge 
                            invisible={!acceptancesPresent}
                            anchorOrigin={{
                                vertical: "top",
                                horizontal: "left",
                            }} 
                            variant="dot"
                            size="small"
                            color="warning"
                        >
                            <EmailRoundedIcon />
                        </Badge>
                        <span className={styles['text']}>{isUserEC ? "Wnioski urlopowe" : "Wnioski o przerwę"}</span>
                    </Link>
                    <Link
                        testId="UsersHistoryLink"
                        exact
                        activeClassName="active"
                        to="/history"
                    >
                        <WatchLaterRoundedIcon />
                        <span className={styles['text']}>Historia użytkownika</span>
                    </Link>
                    {isUserALeader &&
                        <Link to="/acceptances/history">
                            <WorkHistoryOutlinedIcon />
                            <span className={styles['text']}>Historia akceptacji</span>
                        </Link>
                    }
                    {isUserAnAdmin && (
                        <>
                            <Link to="/associates" testId="ContractorsLink">
                            <PeopleOutlineRoundedIcon />
                                <span className={styles['text']}>Współpracownicy</span>
                            </Link>
                            <Link to="/workers" testId="EmployeesLink">
                                <GroupAddOutlinedIcon />
                                <span className={styles['text']}>Pracownicy</span>
                            </Link>
                            <Link to="/holidays" testId="HolidaysLink">
                                <EventAvailableOutlinedIcon />
                                <span className={styles['text']}>Dni świąteczne</span>
                            </Link>
                            <Link to="/reports" testId="ReportsLink">
                                <BarChartOutlinedIcon />
                                <span className={styles['text']}>Raporty</span>
                            </Link>
                            <Link to="/automaticVacationDays">
                                <EventRepeatOutlinedIcon />
                                <span className={styles['text']}>Dni na nowy rok</span>
                            </Link>
                        </>
                    )}
                </Nav>
            </div>
            <div className={styles.versionContainer}>
                {`${version} ${commitId}`}
            </div>
        </>
    );
}

Sidebar.propTypes = {
    newAcceptancesPresent: PropTypes.bool
}

Sidebar.defaultProps = {
    newAcceptancesPresent: false
}
