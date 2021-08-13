import {act, fireEvent, render, screen} from "@testing-library/react";
import {BrowserRouter as Router} from "react-router-dom";

import {TopBar} from "./TopBar";

const testUserName = 'Kacper Bartek';
const testTeams = [
    {name: 'ABC', leader: 'Piotr Nowak'},
];


test('shows logo when render',() => {
    render(<Router><TopBar userName='' teams={[]} onHamburgerClick={() => null}/></Router>);
    const displayedImage = document.querySelector("img");
    expect((displayedImage.src)).toContain('logo.png');
});

test('shows correct user name from props', () => {
    render(<TopBar userName='Piotr Nowak' teams={[]} onHamburgerClick={() => null}/>);
    const userName = screen.getByText('Piotr Nowak');
    expect(userName).toBeInTheDocument();
});

test('shows teams dropdown after clicking on user name', async () => {
    render(<TopBar userName={testUserName} teams={testTeams} onHamburgerClick={() => null}/>);

    const userNameLabel = screen.getByText(testUserName);
    expect(userNameLabel).toBeInTheDocument();

    await act(async () => {
        await fireEvent.click(userNameLabel);
    });

    const teamName = screen.getByText(testTeams[0].name);
    const teamLeader = screen.getByText(`Lider: ${testTeams[0].leader}`);

    expect(teamName).toBeInTheDocument();
    expect(teamLeader).toBeInTheDocument();
});

test('shows "Urlopia" banner', () => {
    render(<TopBar userName='' teams={[]} onHamburgerClick={() => null}/>);
    const urlopiaBanner = screen.getByText('Urlopia');
    expect(urlopiaBanner).toBeInTheDocument();
});