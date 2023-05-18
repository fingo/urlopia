import {act, render, screen} from '@testing-library/react';
import {BrowserRouter as Router} from "react-router-dom";

import {USER_DATA_KEY} from "../../constants/session.keystorage";
import {AppInfoProvider} from "../../contexts/app-info-context/appInfoContext";
import {VacationDaysProvider} from "../../contexts/vacation-days-context/vacationDaysContext";
import {mockLocalStorage} from "../../helpers/TestHelper";
import {Sidebar} from "./Sidebar";
import { vi } from 'vitest';

vi.mock("../../contexts/vacation-days-context/actions/fetchPendingDays", () => {
    const originalModule = vi.requireActual("../../contexts/vacation-days-context/actions/fetchPendingDays");

    return {
        __esModule: true,
        ...originalModule,
        fetchPendingDays: () => {}
    };
})

vi.mock("../../contexts/vacation-days-context/actions/fetchVacationDays", () => {
    const originalModule = vi.requireActual("../../contexts/vacation-days-context/actions/fetchVacationDays");

    return {
        __esModule: true,
        ...originalModule,
        fetchVacationDays: () => {}
    };
})


describe("Sidebar", () => {
    const sessionStorageMock = mockLocalStorage()

    beforeEach(() => {
        sessionStorageMock.clear()
    })

    it("should show correct links when user is not an admin and is EC", () => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            roles: ["ROLES_WORKER"],
            ec: true,
            admin: false
        }))


        // when
        act(() => {
            render(
                <Router>
                    <AppInfoProvider>
                        <VacationDaysProvider>
                            <Sidebar onClickLinkOrOutside={() => {}}/>
                        </VacationDaysProvider>
                    </AppInfoProvider>
                </Router>
            );
        })

        // then
        expect(screen.queryByText('Kalendarz')).toBeInTheDocument()
        expect(screen.queryByText('Wnioski urlopowe')).toBeInTheDocument()
        expect(screen.queryByText('Historia użytkownika')).toBeInTheDocument()
        expect(screen.queryByText('Pracownicy')).not.toBeInTheDocument()
        expect(screen.queryByText('Współpracownicy')).not.toBeInTheDocument()
        expect(screen.queryByText('Dni świąteczne')).not.toBeInTheDocument()
        expect(screen.queryByText('Raporty')).not.toBeInTheDocument()
    })

    it("should show correct links when user is an admin and is EC", () => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            roles: ["ROLES_WORKER", "ROLES_ADMIN"],
            ec: true,
            admin: true
        }))

        // when
        act(() => {
            render(
                <Router>
                    <AppInfoProvider>
                        <VacationDaysProvider>
                            <Sidebar onClickLinkOrOutside={() => {}}/>
                        </VacationDaysProvider>
                    </AppInfoProvider>
                </Router>
            );
        })

        // then
        expect(screen.queryByText('Kalendarz')).toBeInTheDocument()
        expect(screen.queryByText('Wnioski urlopowe')).toBeInTheDocument()
        expect(screen.queryByText('Historia użytkownika')).toBeInTheDocument()
        expect(screen.queryByText('Pracownicy')).toBeInTheDocument()
        expect(screen.queryByText('Współpracownicy')).toBeInTheDocument()
        expect(screen.queryByText('Dni świąteczne')).toBeInTheDocument()
        expect(screen.queryByText('Raporty')).toBeInTheDocument()
    })

    it("should render links with correct routes and is EC", () => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            roles: ["ROLES_WORKER", "ROLES_ADMIN"],
            ec: true,
            admin: true
        }))

        // when
        act(() => {
            render(
                <Router>
                    <AppInfoProvider>
                        <VacationDaysProvider>
                            <Sidebar onClickLinkOrOutside={() => {}}/>
                        </VacationDaysProvider>
                    </AppInfoProvider>
                </Router>
            );
        })
        // console.log(screen.debug());
        // then
        expect(screen.getByTestId('CalendarLink').href).toMatch(/calendar/)
        expect(screen.getByTestId('RequestsLink').href).toMatch(/requests/)
        expect(screen.getByTestId('UsersHistoryLink').href).toMatch(/history/)
        expect(screen.getByTestId('EmployeesLink').href).toMatch(/workers/)
        expect(screen.getByTestId('ContractorsLink').href).toMatch(/associates/)
        expect(screen.getByTestId('HolidaysLink').href).toMatch(/holidays/)
        expect(screen.getByTestId('ReportsLink').href).toMatch(/reports/)
    })
    it("should show correct links when user is not an admin and is not EC", () => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            roles: ["ROLES_WORKER"],
            ec: false,
            admin: false
        }))


        // when
        act(() => {
            render(
                <Router>
                    <AppInfoProvider>
                        <VacationDaysProvider>
                            <Sidebar onClickLinkOrOutside={() => {}}/>
                        </VacationDaysProvider>
                    </AppInfoProvider>
                </Router>
            );
        })

        // then
        expect(screen.queryByText('Kalendarz')).toBeInTheDocument()
        expect(screen.queryByText('Wnioski o przerwę')).toBeInTheDocument()
        expect(screen.queryByText('Historia użytkownika')).toBeInTheDocument()
        expect(screen.queryByText('Pracownicy')).not.toBeInTheDocument()
        expect(screen.queryByText('Współpracownicy')).not.toBeInTheDocument()
        expect(screen.queryByText('Dni świąteczne')).not.toBeInTheDocument()
        expect(screen.queryByText('Raporty')).not.toBeInTheDocument()
    })

    it("should show correct links when user is an admin and is not EC", () => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            roles: ["ROLES_WORKER", "ROLES_ADMIN"],
            ec: false,
            admin: true
        }))

        // when
        act(() => {
            render(
                <Router>
                    <AppInfoProvider>
                        <VacationDaysProvider>
                            <Sidebar onClickLinkOrOutside={() => {}}/>
                        </VacationDaysProvider>
                    </AppInfoProvider>
                </Router>
            );
        })

        // then
        expect(screen.queryByText('Kalendarz')).toBeInTheDocument()
        expect(screen.queryByText('Wnioski o przerwę')).toBeInTheDocument()
        expect(screen.queryByText('Historia użytkownika')).toBeInTheDocument()
        expect(screen.queryByText('Pracownicy')).toBeInTheDocument()
        expect(screen.queryByText('Współpracownicy')).toBeInTheDocument()
        expect(screen.queryByText('Dni świąteczne')).toBeInTheDocument()
        expect(screen.queryByText('Raporty')).toBeInTheDocument()
    })

    it("should render links with correct routes and is not EC", () => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            roles: ["ROLES_WORKER", "ROLES_ADMIN"],
            ec: false,
            admin: true
        }))

        // when
        act(() => {
            render(
                <Router>
                    <AppInfoProvider>
                        <VacationDaysProvider>
                            <Sidebar onClickLinkOrOutside={() => {}}/>
                        </VacationDaysProvider>
                    </AppInfoProvider>
                </Router>
            );
        })

        // then
        expect(screen.getByTestId('CalendarLink').href).toMatch(/calendar/)
        expect(screen.getByTestId('RequestsLink').href).toMatch(/requests/)
        expect(screen.getByTestId('UsersHistoryLink').href).toMatch(/history/)
        expect(screen.getByTestId('EmployeesLink').href).toMatch(/workers/)
        expect(screen.getByTestId('ContractorsLink').href).toMatch(/associates/)
        expect(screen.getByTestId('HolidaysLink').href).toMatch(/holidays/)
        expect(screen.getByTestId('ReportsLink').href).toMatch(/reports/)
    })
})

const linkWithText = (text) => (content, node) => {
    const nodeWithText = Array.from(node.children).find(child => child.textContent === text)
    return node.href && nodeWithText
}
