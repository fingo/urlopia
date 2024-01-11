import {act, fireEvent, render, screen} from "@testing-library/react";
import {BrowserRouter as Router} from "react-router-dom";

import {USER_DATA_KEY} from "../../constants/session.keystorage";
import {UserPreferencesProvider} from "../../contexts/user-preferences-context/userPreferencesContext";
import {mockLocalStorage} from "../../helpers/TestHelper";
import {TopBar} from "./TopBar";
import {vi} from "vitest";
import {VacationDaysProvider} from "../../contexts/vacation-days-context/vacationDaysContext";


vi.mock("../../contexts/user-preferences-context/actions/fetchWorkingHoursPreferences", async () => {
    const originalModule = await vi.importActual("../../contexts/user-preferences-context/actions/fetchWorkingHoursPreferences");

    return {
        __esModule: true,
        ...originalModule,
        fetchWorkingHoursPreferences: vi.fn()
    };
})

describe("TopBar", () => {
    const sessionStorageMock = mockLocalStorage()
    const sampleFullName = 'Kacper Bartek'
    const sampleTeams = [
        {name: 'ABC', leader: 'Piotr Nowak'},
    ]

    beforeAll(() => {
        sessionStorageMock.clear()
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            name: 'Kacper',
            surname: 'Bartek',
            teams: sampleTeams,
            roles: ["ROLES_WORKER"]
        }))
    })

    it('should show logo',() => {
        render(<Router><UserPreferencesProvider><VacationDaysProvider><TopBar /></VacationDaysProvider></UserPreferencesProvider></Router>);
        const displayedImage = document.querySelector("img");
        expect((displayedImage.src)).toContain('logo.svg');
    });

    it('should show correct user name', () => {
        render(<UserPreferencesProvider><VacationDaysProvider><TopBar /></VacationDaysProvider></UserPreferencesProvider>);
        const userName = screen.getByText(sampleFullName, { exact: false });
        expect(userName).toBeInTheDocument();
    });

    it('should show teams dropdown after clicking on user name', async () => {
        render(<UserPreferencesProvider><VacationDaysProvider><TopBar /></VacationDaysProvider></UserPreferencesProvider>);
        const userNameLabel = screen.getByText(sampleFullName, { exact: false });
        expect(userNameLabel).toBeInTheDocument();

        await act(async () => {
            fireEvent.click(userNameLabel);
        });
        const teamName = screen.getByText(sampleTeams[0].name, { exact: false });

        const teamLeader = screen.getByText(`Lider: ${sampleTeams[0].leader}`);

        expect(teamName).toBeInTheDocument();
        expect(teamLeader).toBeInTheDocument();
    });


})
