import '../../global-styles/index.scss';
import '../../global-styles/date-picker.scss';
import '../../global-styles/notification.scss';

import classNames from "classnames";
import {useEffect, useState} from "react";
import {Col, Container, Row} from 'react-bootstrap';

import {getCurrentUser} from "../../api/services/session.service";
import {USER_DATA_KEY, USER_DETAILS_URL} from "../../constants/session.keystorage";
import {fetchAppInfo} from "../../contexts/app-info-context/actions/fetchAppInfo";
import {useAppInfo} from "../../contexts/app-info-context/appInfoContext";
import {RequestProvider} from "../../contexts/request-context/requestContext";
import {UserPreferencesProvider} from "../../contexts/user-preferences-context/userPreferencesContext";
import {UsersVacationsProvider} from "../../contexts/users-vacations-context/usersVacationsContext";
import {VacationDaysProvider} from "../../contexts/vacation-days-context/vacationDaysContext";
import {sendGetRequest} from "../../helpers/RequestHelper";
import {MainContentRouting} from "../../router/MainContentRouting";
import {AcceptanceLoader} from "../acceptance-loader/AcceptanceLoader";
import {Sidebar} from "../sidebar/Sidebar";
import {TopBar} from "../topbar/TopBar";
import styles from './Main.module.scss';
export const Main = () => {


    const [, setUser] = useState({isLeader: false, isAdmin: false})
    const {isLeader: isUserALeader} = getCurrentUser();

    const [isSidebarOpen, setIsSidebarOpen] = useState(false);
    const [acceptancesPresent, setAcceptancesPresent] = useState(false);

    const [, appInfoDispatch] = useAppInfo()

    useEffect(() => {
        sendGetRequest(USER_DETAILS_URL)
            .then(data => {
                if (data){
                    localStorage.setItem(USER_DATA_KEY, JSON.stringify(data))
                    setUser(data)
                }
            })
    }, []);


    useEffect(() => {
        fetchAppInfo(appInfoDispatch)
    }, [appInfoDispatch])

    const handleHamburgerClick = () => {
        setIsSidebarOpen(!isSidebarOpen);
    }

    const handleClickOutsideSidebar = () => {
        setIsSidebarOpen(false);
    }
    const sidebarColClass = classNames('m-0', 'p-0', {'d-none d-lg-block': !isSidebarOpen});

    return (
        <UserPreferencesProvider>
            <TopBar onHamburgerClick={handleHamburgerClick}/>
            <Container fluid>
                <VacationDaysProvider>
                    <UsersVacationsProvider>
                        <Row>
                            <Col xs={3} xl={2} className={sidebarColClass}>
                                <Sidebar
                                    onClickLinkOrOutside={handleClickOutsideSidebar}
                                    acceptancesPresent={acceptancesPresent}
                                />
                            </Col>
                            <Col xs={12} lg={9} xl={10} className={styles.mainContent}>
                                <RequestProvider>
                                    {isUserALeader &&
                                        <AcceptanceLoader setAcceptancesPresent={setAcceptancesPresent}/>}
                                    <MainContentRouting
                                        acceptancesPresent={acceptancesPresent}
                                    />
                                </RequestProvider>
                            </Col>
                        </Row>
                    </UsersVacationsProvider>
                </VacationDaysProvider>
            </Container>
        </UserPreferencesProvider>
    );
}
