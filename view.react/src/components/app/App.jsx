import '../../global-styles/index.scss';

import classNames from "classnames";
import {useState} from "react";
import {Col, Container, Row} from 'react-bootstrap';
import {BrowserRouter as Router} from "react-router-dom";

import {Sidebar} from "../sidebar/Sidebar";
import {TopBar} from "../topbar/TopBar";

export const App = () => {
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
        <Router>
            <div className="App">
                <TopBar userName={userName} teams={teams} onHamburgerClick={handleHamburgerClick}/>
                <Container fluid>
                    <Row>
                        <Col xs={3} className={sidebarColClass}>
                            <Sidebar onClickOutside={handleClickOutsideSidebar}/>
                        </Col>
                        <Col xs={12} lg={9}>
                            <h1>App</h1>
                        </Col>
                    </Row>
                </Container>
            </div>
        </Router>

    );
}
