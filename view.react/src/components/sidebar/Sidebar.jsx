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
import {useState} from "react";
import {Container, Nav} from 'react-bootstrap';

import {getCurrentUser} from "../../api/services/session.service";
import {useAppInfo} from "../../contexts/app-info-context/appInfoContext";
import {AttentionIcon, TextWithIcon} from "../../helpers/icons/Icons";
import {Link} from "./link/Link";
import styles from './Sidebar.module.scss';

export const Sidebar = ({onClickLinkOrOutside, acceptancesPresent}) => {
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
            <Container fluid className={styles.main}>
                <Nav className={sidebarClasses}>
                    <button onClick={() => setIsOpen(!isOpen)}>
                        <ArrowForwardIosRoundedIcon />
                    </button>
                    <Link
                        testId="CalendarLink"
                        onClick={onClickLinkOrOutside}
                        exact
                        activeClassName="active"
                        to="/calendar"
                    >
                        <CalendarMonthSharpIcon />
                        <span>Kalendarz</span>
                    </Link>
                    <Link
                        testId="RequestsLink"
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
                        testId="UsersHistoryLink"
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
                            <Link to="/associates" onClick={onClickLinkOrOutside} testId="ContractorsLink">
                            <PeopleOutlineRoundedIcon />
                                <span>Współpracownicy</span>
                            </Link>
                            <Link to="/workers" onClick={onClickLinkOrOutside} testId="EmployeesLink">
                                <GroupAddOutlinedIcon />
                                <span>Pracownicy</span>
                            </Link>
                            <Link to="/holidays" onClick={onClickLinkOrOutside} testId="HolidaysLink">
                                <EventAvailableOutlinedIcon />
                                <span>Dni świąteczne</span>
                            </Link>
                            <Link to="/reports" onClick={onClickLinkOrOutside} testId="ReportsLink">
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
            <div className={styles.versionContainer}>
                {`${version} ${commitId}`}
            </div>
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
