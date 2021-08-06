import {act, fireEvent, render, screen} from "@testing-library/react";

import {TeamDropdown} from "./TeamDropdown";

const testUserName = 'Kacper Bartek';
const testTeams = [
    {name: 'ABC', leader: 'Piotr Nowak'},
];

test('shows user name', async () => {
    render(<TeamDropdown userName={testUserName} teams={testTeams}/>);

    const userNameLabel = screen.getByText(testUserName);
    expect(userNameLabel).toBeInTheDocument();
});

test('not shows teams before clicking on user name', async () => {
    render(<TeamDropdown userName={testUserName} teams={testTeams}/>);

    expect(screen.queryByText(testTeams[0].name)).not.toBeInTheDocument();
    expect(screen.queryByText(`Lider: ${testTeams[0].leader}`)).not.toBeInTheDocument();
});

test('shows teams after clicking on user name', async () => {
    render(<TeamDropdown userName={testUserName} teams={testTeams}/>);

    const userNameLabel = screen.getByText(testUserName);
    expect(userNameLabel).toBeInTheDocument();

    await act(async () => {
        await fireEvent.click(userNameLabel);
    });

    expect(screen.queryByText(testTeams[0].name)).toBeInTheDocument();
    expect(screen.queryByText(`Lider: ${testTeams[0].leader}`)).toBeInTheDocument();
});

test('hide teams after clicking user name when teams component is displayed', async () => {
    render(<TeamDropdown userName={testUserName} teams={testTeams}/>);

    const userNameLabel = screen.getByText(testUserName);
    expect(userNameLabel).toBeInTheDocument();

    await act(async () => {
        await fireEvent.click(userNameLabel);
        await fireEvent.click(userNameLabel);
    });

    expect(screen.getByText(testTeams[0].name)).not.toBeVisible();
    expect(screen.getByText(`Lider: ${testTeams[0].leader}`)).not.toBeVisible();
});
