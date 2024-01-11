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
            <VacationDaysProvider>
                <TopBar onHamburgerClick={handleHamburgerClick}/>
                <Container fluid>
                    <UsersVacationsProvider>
                        <Row>
                            <Col xs={12} lg={12} xl={12} className='d-flex'>
                                <Sidebar
                                  onClickLinkOrOutside={handleClickOutsideSidebar}
                                  acceptancesPresent={acceptancesPresent}
                                />
                                <RequestProvider>
                                    {isUserALeader &&
                                        <AcceptanceLoader setAcceptancesPresent={setAcceptancesPresent}/>}
                                    <div className={styles.mainContent}>
                                        <MainContentRouting acceptancesPresent={acceptancesPresent}/>
                                    </div>
                                </RequestProvider>
                            </Col>
                        </Row>
                    </UsersVacationsProvider>
                </Container>
            </VacationDaysProvider>
        </UserPreferencesProvider>
    );
}
