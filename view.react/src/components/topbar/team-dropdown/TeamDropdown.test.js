import {fireEvent, render, screen, waitFor} from "@testing-library/react";

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

    expect(screen.queryByTestId('team-dropdown')).not.toBeInTheDocument();
    expect(screen.queryByText(`Lider: ${testTeams[0].leader}`)).not.toBeInTheDocument();
});

test('shows teams after clicking on user name', async () => {
    render(<TeamDropdown userName={testUserName} teams={testTeams}/>);

    const userNameLabel = screen.getByText(testUserName);
    expect(userNameLabel).toBeInTheDocument();

    fireEvent.click(userNameLabel);

    await waitFor(() => {
        expect(screen.queryByTestId('team-dropdown')).toHaveClass('show');
    })
    expect(screen.getByText(`Lider: ${testTeams[0].leader}`)).toBeInTheDocument();
});

test('hide teams after clicking user name when teams component is displayed', async () => {
    render(<TeamDropdown userName={testUserName} teams={testTeams}/>);

    const userNameLabel = screen.getByText(testUserName);
    expect(userNameLabel).toBeInTheDocument();

    fireEvent.click(userNameLabel);
    await waitFor(() => {
        expect(screen.queryByTestId('team-dropdown')).toHaveClass('show');
    })

    fireEvent.click(userNameLabel);
    await waitFor(() => {
        expect(screen.queryByTestId('team-dropdown')).not.toHaveClass('show');
    })
});
