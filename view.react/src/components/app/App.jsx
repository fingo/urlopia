import '../../global-styles/index.scss';

import classNames from "classnames";
import {useState} from "react";
import {Col, Container, Row} from 'react-bootstrap';
import {Redirect} from "react-router-dom";

import {getCurrentUser} from "../../api/services/session.service";
import {LoginPage} from "../../pages/login-page/LoginPage";
import {MainContentRouting} from "../../router/MainContentRouting";
import {Sidebar} from "../sidebar/Sidebar";
import {TopBar} from "../topbar/TopBar";

export const App = () => {
    const sessionToken = getCurrentUser()?.token;
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);

    const handleHamburgerClick = () => {
        setIsSidebarOpen(!isSidebarOpen);
    }

    const handleClickOutsideSidebar = () => {
        setIsSidebarOpen(false);
    }

    const userName = 'Jan Kowalski';
    const teams = [
        {name: 'DL', leader: 'Piotr Nowak'},
        {name: 'TEAM_ABC', leader: 'Karol Kapitalończyk-Mikołajczyk'},
    ];

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
                        <TopBar userName={userName} teams={teams} onHamburgerClick={handleHamburgerClick}/>
                        <Container fluid>
                            <Row>
                                <Col xs={3} className={sidebarColClass}>
                                    <Sidebar onClickOutside={handleClickOutsideSidebar}/>
                                </Col>
                                <Col xs={12} lg={9}>
                                    <MainContentRouting/>
                                </Col>
                            </Row>
                        </Container>
                    </>
            }
        </>
    );
}
