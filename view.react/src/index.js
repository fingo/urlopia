import React from 'react';
import ReactDOM from 'react-dom';
import ReactNotification from 'react-notifications-component';
import {BrowserRouter as Router} from "react-router-dom";

import {App} from './components/app/App';
import reportWebVitals from './reportWebVitals';

ReactDOM.render(
    <React.StrictMode>
        <Router>
            <ReactNotification isMobile={true} />
            <App/>
        </Router>
    </React.StrictMode>,
    document.getElementById('root'),
);

reportWebVitals();
