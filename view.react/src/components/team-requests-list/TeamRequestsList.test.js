import {fireEvent, render, screen, waitFor} from "@testing-library/react";

import {TeamRequestsList} from "./TeamRequestsList";

describe("TeamRequestsList", () => {
    const sampleRequests = [
        {
            id: 1,
            requester: "Jan Kowalski",
            period: "2021-08-26 - 2021-08-27 (2 dni robocze)",
        },
        {
            id: 2,
            requester: "Adam Nowak",
            period: "2021-08-26 - 2021-08-27 (2 dni robocze)",
        },
        {
            id: 3,
            requester: "Radek Marek",
            period: "2021-08-26 - 2021-08-27 (2 dni robocze)",
        }
    ]

    it('should show headers of table', () => {
        render(<TeamRequestsList requests={sampleRequests} rejectRequest={() => null} acceptRequest={() => null}/>)
        const requesterHeader = screen.getByText('Wnioskodawca');
        const periodHeader = screen.getByText('Termin');
        const actionHeader = screen.getByText('Akcje');
        expect(requesterHeader).toBeInTheDocument();
        expect(periodHeader).toBeInTheDocument();
        expect(actionHeader).toBeInTheDocument();
    });

    it('should show "Tabela jest pusta..." label if requests array is empty', () => {
        render(<TeamRequestsList requests={[]} rejectRequest={() => null} acceptRequest={() => null}/>);
        const label = screen.getByText('Tabela jest pusta...');
        expect(label).toBeInTheDocument();
    });

    it('should show reject and accept button in the action column if data is present', () => {
        render(<TeamRequestsList requests={sampleRequests} rejectRequest={() => null} acceptRequest={() => null}/>);
        const isAnyData = document.querySelectorAll("tbody").length === 2;
        if (!isAnyData) return;
        const cancelBtn = screen.getAllByTitle('Odrzuć wniosek');
        const acceptBtn = screen.getAllByTitle('Zaakceptuj wniosek');
        expect(cancelBtn.length).toBeGreaterThanOrEqual(1);
        expect(acceptBtn.length).toBeGreaterThanOrEqual(1);
    });

    it('should show filter inputs', () => {
        render(<TeamRequestsList requests={sampleRequests} rejectRequest={() => null} acceptRequest={() => null}/>);
        const inputs = screen.queryAllByPlaceholderText('Filtruj...');
        expect(inputs.length).toBe(1);
    });

    it('should filter inputs and should keep what the user enters', () => {
        render(<TeamRequestsList requests={sampleRequests} rejectRequest={() => null} acceptRequest={() => null}/>);
        const [requesterInput] = screen.queryAllByPlaceholderText('Filtruj...');
        fireEvent.change(requesterInput, {target: {value: 'Jan'}});
        expect(requesterInput).toHaveValue('Jan');
    });

    it('should show a table with no data after entering invanulllid input into the filter input', async () => {
        render(<TeamRequestsList requests={sampleRequests} rejectRequest={() => null} acceptRequest={() => null}/>);
        const isAnyData = document.querySelectorAll("tbody").length === 2;
        if (!isAnyData) return;
        const [requesterInput] = screen.queryAllByPlaceholderText('Filtruj...');
        fireEvent.change(requesterInput, {target: {value: '%'}});
        await waitFor(() => expect(document.querySelectorAll("tbody").length).toBe(1))
    });
})
