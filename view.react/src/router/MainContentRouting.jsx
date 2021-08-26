import {Redirect, Route, Switch} from "react-router-dom";

import {getCurrentUser} from "../api/services/session.service";
import {AbsenceRequestsPage, URL as VacationRequestsURL} from '../pages/absence-requests-page/AbsenceRequestsPage';
import {AssociatesPage, URL as AssociatesURL} from '../pages/associates-page/AssociatesPage';
import {CalendarPage, URL as CalendarURL} from '../pages/calendar-page/CalendarPage';
import {HistoryPage, URL as HistoryURL} from '../pages/history-page/HistoryPage';
import {HolidaysPage, URL as HolidaysURL} from '../pages/holidays-page/HolidaysPage';
import {Page404} from '../pages/page-404/Page404';
import {ReportsPage, URL as ReportsURL} from '../pages/reports-page/ReportsPage';
import {URL as WorkersURL, WorkersPage} from '../pages/workers-page/WorkersPage';

export const MainContentRouting = ({newAcceptancesPresent, setNewAcceptancesPresent}) => {
    const {isAdmin: isUserAnAdmin} = getCurrentUser()
    return (
        <Switch>
            <Route path="/" exact>
                <Redirect to={CalendarURL}/>
            </Route>

            <Route path={CalendarURL} exact>
                <CalendarPage/>
            </Route>

            <Route path={VacationRequestsURL} exact>
                    <AbsenceRequestsPage
                        newAcceptancesPresent={newAcceptancesPresent}
                        setNewAcceptancesPresent={setNewAcceptancesPresent}
                    />
            </Route>

            <Route path={HistoryURL} exact>
                <HistoryPage/>
            </Route>

            {isUserAnAdmin && (
              <>
                  <Route path={WorkersURL} exact>
                      <WorkersPage/>
                  </Route>

                  <Route path={AssociatesURL} exact>
                      <AssociatesPage/>
                  </Route>

                  <Route path={HolidaysURL} exact>
                      <HolidaysPage/>
                  </Route>

                  <Route path={ReportsURL} exact>
                      <ReportsPage/>
                  </Route>
              </>
            )}

            <Route path="*" >
                <Page404/>
            </Route>
        </Switch>
    );
}
