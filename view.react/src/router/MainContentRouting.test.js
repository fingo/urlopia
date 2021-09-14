import {render, screen} from "@testing-library/react";
import {MemoryRouter} from "react-router-dom";

import {USER_DATA_KEY} from "../constants/session.keystorage";
import {mockLocalStorage} from "../helpers/TestHelper";
import {AbsenceRequestsPage, URL as VacationRequestsURL} from '../pages/absence-requests-page/AbsenceRequestsPage';
import {AssociatesPage, URL as AssociatesURL} from '../pages/associates-page/AssociatesPage';
import {CalendarPage, URL as CalendarURL} from '../pages/calendar-page/CalendarPage';
import {HistoryPage, URL as HistoryURL} from '../pages/history-page/HistoryPage';
import {HolidaysPage, URL as HolidaysURL} from '../pages/holidays-page/HolidaysPage';
import {ReportsPage, URL as ReportsURL} from '../pages/reports-page/ReportsPage';
import {URL as WorkersURL, WorkersPage} from '../pages/workers-page/WorkersPage';
import {MainContentRouting} from "./MainContentRouting";

jest.mock('../pages/absence-requests-page/AbsenceRequestsPage');
jest.mock('../pages/associates-page/AssociatesPage');
jest.mock('../pages/calendar-page/CalendarPage');
jest.mock('../pages/history-page/HistoryPage');
jest.mock('../pages/holidays-page/HolidaysPage');
jest.mock('../pages/reports-page/ReportsPage');
jest.mock('../pages/workers-page/WorkersPage');

describe("MainContentRouting", () => {
    const sessionStorageMock = mockLocalStorage()

    beforeEach(() => {
        sessionStorageMock.clear()
    })

    describe("when user is an admin", () => {
        const userRoles = ["ROLES_WORKER", "ROLES_ADMIN"]

        it('should render requests route', () => {
            expectRouteToBePresent(userRoles, VacationRequestsURL, AbsenceRequestsPage)
        })

        it('should render associates route', () => {
            expectRouteToBePresent(userRoles, AssociatesURL, AssociatesPage)
        })

        it('should render calendar route', () => {
            expectRouteToBePresent(userRoles, CalendarURL, CalendarPage)
        })

        it('should render history route', () => {
            expectRouteToBePresent(userRoles, HistoryURL, HistoryPage)
        })

        it('should render holidays route', () => {
            expectRouteToBePresent(userRoles, HolidaysURL, HolidaysPage)
        })

        it('should render reports route', () => {
            expectRouteToBePresent(userRoles, ReportsURL, ReportsPage)
        })

        it('should render workers route', () => {
            expectRouteToBePresent(userRoles, WorkersURL, WorkersPage)
        })
    })

    describe("when user is not an admin", () => {
        const userRoles = ["ROLES_WORKER"]

        it('should render requests route', () => {
            expectRouteToBePresent(userRoles, VacationRequestsURL, AbsenceRequestsPage)
        })

        it('should not render associates route', () => {
            expectRouteNotToBePresent(userRoles, AssociatesURL, AssociatesPage)
        })

        it('should render calendar route', () => {
            expectRouteToBePresent(userRoles, CalendarURL, CalendarPage)
        })

        it('should render history route', () => {
            expectRouteToBePresent(userRoles, HistoryURL, HistoryPage)
        })

        it('should not render holidays route', () => {
            expectRouteNotToBePresent(userRoles, HolidaysURL, HolidaysPage)
        })

        it('should not render reports route', () => {
            expectRouteNotToBePresent(userRoles, ReportsURL, ReportsPage)
        })

        it('should not render workers route', () => {
            expectRouteNotToBePresent(userRoles, WorkersURL, WorkersPage)
        })
    })

    const expectRouteToBePresent = (userRoles, url, component) => expectRoute(userRoles, url, component, true)
    const expectRouteNotToBePresent = (userRoles, url, component) => expectRoute(userRoles, url, component, false)

    const expectRoute = (userRoles, url, component, shouldBePresent) => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({userRoles}))
        component.mockImplementation(() => <div>{`${url}Mock`}</div>)

        // when
        render(
            <MemoryRouter initialEntries={[url]}>
                <MainContentRouting />
            </MemoryRouter>
        )

        // then
        if (shouldBePresent) {
            expect(screen.getByText(`${url}Mock`)).toBeInTheDocument()
        } else {
            expect(screen.queryByText(`${url}Mock`)).not.toBeInTheDocument()
        }
    }
})