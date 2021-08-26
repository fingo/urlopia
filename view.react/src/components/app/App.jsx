import '../../global-styles/index.scss';

import classNames from "classnames";
import {useState} from "react";
import {Col, Container, Row} from 'react-bootstrap';
import {Redirect} from "react-router-dom";

import {getCurrentUser} from "../../api/services/session.service";
import {RequestProvider} from "../../contexts/request-context/requestContext";
import {LoginPage} from "../../pages/login-page/LoginPage";
import {MainContentRouting} from "../../router/MainContentRouting";
import {AcceptanceLoader} from "../acceptance-loader/AcceptanceLoader";
import {Sidebar} from "../sidebar/Sidebar";
import {TopBar} from "../topbar/TopBar";
import styles from './App.module.scss';

export const App = () => {
    const user = getCurrentUser()
    const {token: sessionToken, isLeader: isUserALeader} = user
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);
    const [newAcceptancesPresent, setNewAcceptancesPresent] = useState(false);

    const handleHamburgerClick = () => {
        setIsSidebarOpen(!isSidebarOpen);
    }

    const handleClickOutsideSidebar = () => {
        setIsSidebarOpen(false);
    }

    const sidebarColClass = classNames('m-0', 'p-0', {'d-none d-lg-block': !isSidebarOpen});
    return (
        <>
            {
                !sessionToken
                    ?

                    <>
                        <Redirect to='/'/>
                        <LoginPage/>
                    </>

                    :

                    <>
                        <TopBar onHamburgerClick={handleHamburgerClick}/>
                        <Container fluid>
                            <Row>
                                <Col xs={3} xl={2} className={sidebarColClass}>
                                    <Sidebar
                                        onClickLinkOrOutside={handleClickOutsideSidebar}
                                        newAcceptancesPresent={newAcceptancesPresent}
                                    />
                                </Col>
                                <Col xs={12} lg={9} xl={10} className={styles.mainContent}>
                                    <RequestProvider>
                                        {isUserALeader && <AcceptanceLoader setNewAcceptancesPresent={setNewAcceptancesPresent}/>}
                                        <MainContentRouting
                                            newAcceptancesPresent={newAcceptancesPresent}
                                            setNewAcceptancesPresent={setNewAcceptancesPresent}
                                        />
                                    </RequestProvider>
                                </Col>
                            </Row>
                        </Container>
                    </>
            }
        </>
    );
}
