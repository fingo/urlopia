import {fireEvent, render, screen} from "@testing-library/react";

import {UserRequestsList} from "./UserRequestsList";

const testProducts = [
    {
        id: 1,
        period: '2021-07-20 - 2021-07-27 (7 dni robocze)',
        type: 'Wypoczynkowy',
        status: 'Oczekujący',
        actions: ''
    },
    {
        id: 2,
        period: '2021-07-30 - 2021-07-3 (1 dni robocze)',
        type: 'Opieka nad dzieckiem',
        status: 'Zatwierdzony',
        actions: ''
    },
    {
        id: 3,
        period: '2021-08-22 - 2021-08-2 (1 dni robocze)',
        type: 'Ślub',
        status: 'Anulowany',
        actions: ''
    }
];

test('shows header of table', () => {
    render(<UserRequestsList />);
    const periodHeader = screen.getByText('Termin');
    const typeHeader = screen.getByText('Rodzaj');
    const statusHeader = screen.getByText('Status');
    const actionHeader = screen.getByText('Akcje');
    expect(periodHeader).toBeInTheDocument();
    expect(typeHeader).toBeInTheDocument();
    expect(statusHeader).toBeInTheDocument();
    expect(actionHeader).toBeInTheDocument();
});

test('shows cancel button when status is pending', () => {
    render(<UserRequestsList requests={testProducts}/>);
    const pendingRequest = screen.getByText('Oczekujący');
    const cancelBtn = screen.getByTitle('Anuluj wniosek');
    expect(pendingRequest).toBeInTheDocument();
    expect(cancelBtn).toBeInTheDocument();
});

test('shows filter inputs', () => {
    render(<UserRequestsList />);
    const inputs = screen.queryAllByPlaceholderText('Filtruj...');
    expect(inputs.length).toBe(2);
});

test('filter inputs should keep what the user enters', async () => {
    render(<UserRequestsList />);
    const [typeInput, statusInput] = screen.queryAllByPlaceholderText('Filtruj...');
    fireEvent.change(typeInput, {target: {value: 'Wypoczynkowy'}});
    fireEvent.change(statusInput, {target: {value: 'Zatwierdzony'}});
    expect(typeInput).toHaveValue('Wypoczynkowy');
    expect(statusInput).toHaveValue('Zatwierdzony');
});