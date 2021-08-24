import {fireEvent, render, screen, waitFor} from "@testing-library/react";

import {TeamRequestsTab} from "./TeamRequestsTab";

describe("TeamRequestsTab", () => {
    const requests = [
        {
            id: 1,
            requester: "Jan Kowalski",
            period: ["2021-08-18 - 2021-08-25", "(5 dni roboczych)"]
        },
        {
            id: 2,
            requester: "Adam Nowak",
            period: ["2021-08-19 - 2021-08-20", "(2 dni robocze)"]
        },
        {
            id: 3,
            requester: "Radek Marek",
            period: ["2021-08-28 - 2021-09-11", "(14 dni roboczych)"]
        }
    ]

    const renderTeamRequestsTab = ({requests, acceptRequest = () => {}, rejectRequest = () => {}}) => {
        render(<TeamRequestsTab requests={requests} rejectRequest={rejectRequest} acceptRequest={acceptRequest} />)
    }

    it('should show headers of table', () => {
        renderTeamRequestsTab({})
        const requesterHeader = screen.getByText('Wnioskodawca');
        const periodHeader = screen.getByText('Termin');
        const actionHeader = screen.getByText('Akcje');
        expect(requesterHeader).toBeInTheDocument();
        expect(periodHeader).toBeInTheDocument();
        expect(actionHeader).toBeInTheDocument();
    });

    it('should show reject and accept button in the action column if data is present', () => {
        renderTeamRequestsTab({requests})
        const isAnyData = document.querySelectorAll("tbody").length === 2;
        if (!isAnyData) return;
        const cancelBtn = screen.getAllByTitle('Odrzuć wniosek');
        const acceptBtn = screen.getAllByTitle('Zaakceptuj wniosek');
        expect(cancelBtn.length).toBeGreaterThanOrEqual(1);
        expect(acceptBtn.length).toBeGreaterThanOrEqual(1);
    });

    it('should show filter inputs', () => {
        renderTeamRequestsTab({})
        const inputs = screen.queryAllByPlaceholderText('Filtruj...');
        expect(inputs.length).toBe(1);
    });

    it('should filter inputs and should keep what the user enters', () => {
        renderTeamRequestsTab({})
        const [requesterInput] = screen.queryAllByPlaceholderText('Filtruj...');
        fireEvent.change(requesterInput, {target: {value: 'Jan'}});
        expect(requesterInput).toHaveValue('Jan');
    });

    it('should show a table with no data after entering invalid input into the filter input', async () => {
        renderTeamRequestsTab({requests})
        const isAnyData = document.querySelectorAll("tbody").length === 2;
        if (!isAnyData) return;
        const [requesterInput] = screen.queryAllByPlaceholderText('Filtruj...');
        fireEvent.change(requesterInput, {target: {value: '%'}});
        await waitFor(() => expect(document.querySelectorAll("tbody").length).toBe(1))
    });
})
