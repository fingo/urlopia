import {act, fireEvent, render, screen} from "@testing-library/react";
import {BrowserRouter as Router} from "react-router-dom";

import {USER_DATA_KEY} from "../../constants/session.keystorage";
import {mockLocalStorage} from "../../helpers/TestHelper";
import {TopBar} from "./TopBar";

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
            userRoles: ["ROLES_WORKER"]
        }))
    })

    it('should show logo',() => {
        render(<Router><TopBar onHamburgerClick={() => null}/></Router>);
        const displayedImage = document.querySelector("img");
        expect((displayedImage.src)).toContain('logo.png');
    });

    it('should show correct user name', () => {
        render(<TopBar onHamburgerClick={() => null}/>);
        const userName = screen.getByText(sampleFullName);
        expect(userName).toBeInTheDocument();
    });

    it('should show teams dropdown after clicking on user name', async () => {
        render(<TopBar onHamburgerClick={() => null}/>);

        const userNameLabel = screen.getByText(sampleFullName);
        expect(userNameLabel).toBeInTheDocument();

        await act(async () => {
            fireEvent.click(userNameLabel);
        });

        const teamName = screen.getByText(sampleTeams[0].name);
        const teamLeader = screen.getByText(`Lider: ${sampleTeams[0].leader}`);

        expect(teamName).toBeInTheDocument();
        expect(teamLeader).toBeInTheDocument();
    });

    it('should show "Urlopia" banner', () => {
        render(<TopBar onHamburgerClick={() => null}/>);
        const urlopiaBanner = screen.getByText('Urlopia');
        expect(urlopiaBanner).toBeInTheDocument();
    });
})
