import React from 'react';
import ReactDOM from 'react-dom';
import ReactNotification from 'react-notifications-component';
import {BrowserRouter as Router} from "react-router-dom";

import {App} from './components/app/App';
import {AppInfoProvider} from "./contexts/app-info-context/appInfoContext";
import reportWebVitals from './reportWebVitals';

ReactDOM.render(
    <React.StrictMode>
        <Router>
            <ReactNotification isMobile={true}/>
            <AppInfoProvider>
                <App/>
            </AppInfoProvider>
        </Router>
    </React.StrictMode>,
    document.getElementById('root'),
);

reportWebVitals();
