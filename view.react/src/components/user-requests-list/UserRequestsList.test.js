import {fireEvent, render, screen} from "@testing-library/react";

import {UserRequestsList} from "./UserRequestsList";

const sampleRequests = [
    {
        "id": 1,
        "type": "NORMAL",
        "requesterName": "Jan Kowalski",
        "status": "PENDING",
        "endDate": "2021-07-27",
        "startDate": "2021-07-20",
        "workingDays": 8,
        "acceptances": [],
    },
    {
        "id": 2,
        "type": "OCCASIONAL",
        "requesterName": "Jan Kowalski",
        "status": "ACCEPTED",
        "endDate": "2021-07-31",
        "startDate": "2021-07-30",
        "workingDays": 2,
        "acceptances": []
    },
    {
        "id": 3,
        "type": "OCCASIONAL",
        "requesterName": "Jan Kowalski",
        "status": "CANCELED",
        "endDate": "2021-08-22",
        "startDate": "2021-08-22",
        "workingDays": 1,
        "acceptances": []
    }
]

test('shows header of table', () => {
    render(<UserRequestsList requests={sampleRequests} cancelRequest={() => null}/>);
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
    render(<UserRequestsList requests={sampleRequests} cancelRequest={() => null}/>);
    const pendingRequest = screen.getByText('Oczekujący');
    const cancelBtn = screen.getByTitle('Anuluj wniosek');
    expect(pendingRequest).toBeInTheDocument();
    expect(cancelBtn).toBeInTheDocument();
});

test('shows filter inputs', () => {
    render(<UserRequestsList requests={sampleRequests} cancelRequest={() => null}/>);
    const inputs = screen.queryAllByPlaceholderText('Filtruj...');
    expect(inputs.length).toBe(2);
});

test('filter inputs should keep what the user enters', async () => {
    render(<UserRequestsList requests={sampleRequests} cancelRequest={() => null}/>);
    const [typeInput, statusInput] = screen.queryAllByPlaceholderText('Filtruj...');
    fireEvent.change(typeInput, {target: {value: 'Wypoczynkowy'}});
    fireEvent.change(statusInput, {target: {value: 'Zatwierdzony'}});
    expect(typeInput).toHaveValue('Wypoczynkowy');
    expect(statusInput).toHaveValue('Zatwierdzony');
});
