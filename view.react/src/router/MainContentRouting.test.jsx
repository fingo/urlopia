import {render, screen} from "@testing-library/react";
import {MemoryRouter} from "react-router-dom";
import { vi } from 'vitest';

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

vi.mock('../pages/absence-requests-page/AbsenceRequestsPage');
vi.mock('../pages/associates-page/AssociatesPage');
vi.mock('../pages/calendar-page/CalendarPage');
vi.mock('../pages/history-page/HistoryPage');
vi.mock('../pages/holidays-page/HolidaysPage');
vi.mock('../pages/reports-page/ReportsPage');
vi.mock('../pages/workers-page/WorkersPage');

describe("MainContentRouting", () => {
    const sessionStorageMock = mockLocalStorage()

    beforeEach(() => {
        sessionStorageMock.clear()
    })

    describe("when user is an admin", () => {
        const roles = ["ROLES_WORKER", "ROLES_ADMIN"]

        it('should render requests route', () => {
            expectRouteToBePresent(roles, VacationRequestsURL, AbsenceRequestsPage)
        })

        it('should render associates route', () => {
            expectRouteToBePresent(roles, AssociatesURL, AssociatesPage)
        })

        it('should render calendar route', () => {
            expectRouteToBePresent(roles, CalendarURL, CalendarPage)
        })

        it('should render history route', () => {
            expectRouteToBePresent(roles, HistoryURL, HistoryPage)
        })

        it('should render holidays route', () => {
            expectRouteToBePresent(roles, HolidaysURL, HolidaysPage)
        })

        it('should render reports route', () => {
            expectRouteToBePresent(roles, ReportsURL, ReportsPage)
        })

        it('should render workers route', () => {
            expectRouteToBePresent(roles, WorkersURL, WorkersPage)
        })
    })

    describe("when user is not an admin", () => {
        const roles = ["ROLES_WORKER"]

        it('should render requests route', () => {
            expectRouteToBePresent(roles, VacationRequestsURL, AbsenceRequestsPage)
        })

        it('should not render associates route', () => {
            expectRouteNotToBePresent(roles, AssociatesURL, AssociatesPage)
        })

        it('should render calendar route', () => {
            expectRouteToBePresent(roles, CalendarURL, CalendarPage)
        })

        it('should render history route', () => {
            expectRouteToBePresent(roles, HistoryURL, HistoryPage)
        })

        it('should not render holidays route', () => {
            expectRouteNotToBePresent(roles, HolidaysURL, HolidaysPage)
        })

        it('should not render reports route', () => {
            expectRouteNotToBePresent(roles, ReportsURL, ReportsPage)
        })

        it('should not render workers route', () => {
            expectRouteNotToBePresent(roles, WorkersURL, WorkersPage)
        })
    })

    const expectRouteToBePresent = (roles, url, component) => expectRoute(roles, url, component, true)
    const expectRouteNotToBePresent = (roles, url, component) => expectRoute(roles, url, component, false)

    const expectRoute = (roles, url, component, shouldBePresent) => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({roles}))
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
