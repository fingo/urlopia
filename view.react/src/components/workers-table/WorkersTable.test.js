import {act, fireEvent, render, screen, waitFor} from "@testing-library/react";
import axios from "axios";

import {USER_DATA_KEY} from "../../constants/session.keystorage";
import {WorkersProvider} from "../../contexts/workers-context/workersContext";
import {mockSessionStorage} from "../../helpers/TestHelper";
import {WorkersTable} from "./WorkersTable";
import {PresenceProvider} from "../../contexts/presence-context/presenceContext";

jest.mock('axios');

describe('WorkersTable', () => {
    const sessionStorageMock = mockSessionStorage()

    beforeAll(() => {
        sessionStorageMock.clear()
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            userId: 1,
            userRoles: []
        }))
    })

    it('should show headers of table for employees', async () => {
        axios.get.mockResolvedValue({data: []})
        await act(async () => {
            await render(<PresenceProvider><WorkersProvider><WorkersTable/></WorkersProvider></PresenceProvider>);

            await waitFor(() => {
                const fullNameHeader = screen.getByText('Imię i nazwisko');
                const emailHeader = screen.getByText('E-mail');
                const teamHeader = screen.getByText('Zespoły');
                const workTimeHeader = screen.getByText('Etat');
                expect(fullNameHeader).toBeInTheDocument();
                expect(emailHeader).toBeInTheDocument();
                expect(teamHeader).toBeInTheDocument();
                expect(workTimeHeader).toBeInTheDocument();
            })

        })
    })

    it('should show headers of table for associates', async () => {
        axios.get.mockResolvedValue({data: []});
        await act(async () => {
            await render(<PresenceProvider><WorkersProvider><WorkersTable isEC={false}/></WorkersProvider></PresenceProvider>);
            const fullNameHeader = screen.getByText('Imię i nazwisko');
            const emailHeader = screen.getByText('E-mail');
            const teamHeader = screen.getByText('Zespoły');
            expect(fullNameHeader).toBeInTheDocument();
            expect(emailHeader).toBeInTheDocument();
            expect(teamHeader).toBeInTheDocument();
        })
    })

    it('should show title "Pracownicy" for employees', async () => {
        axios.get.mockResolvedValue({data: []});
        await act(async () => {
            await render(<PresenceProvider><WorkersProvider><WorkersTable/></WorkersProvider></PresenceProvider>);
            const title = screen.getByText('Pracownicy');
            expect(title).toBeInTheDocument();
        })
    })

    it('shows title "Współpracownicy" for associates', async () => {
        axios.get.mockResolvedValue({data: []});
        await act(async () => {
            await render(<PresenceProvider><WorkersProvider><WorkersTable isEC={false}/></WorkersProvider></PresenceProvider>);
            const title = screen.getByText('Współpracownicy');
            expect(title).toBeInTheDocument();
        })
    })

    it('should show filter inputs for workers', async () => {
        axios.get.mockResolvedValue({data: []});
        await act(async () => {
            await render(<PresenceProvider><WorkersProvider><WorkersTable isEC={true}/></WorkersProvider></PresenceProvider>);
            await waitFor(() => expect(screen.queryAllByPlaceholderText('Filtruj...').length).toBe(4));
        })
    })

    it('should show filter inputs for associates', async () => {
        axios.get.mockResolvedValue({data: []});
        await act(async () => {
            await render(<PresenceProvider><WorkersProvider><WorkersTable isEC={false}/></WorkersProvider></PresenceProvider>);
            await waitFor(() => expect(screen.queryAllByPlaceholderText('Filtruj...').length).toBe(3));
        })
    })

    it('filter inputs should keep what the user enters', async () => {
        axios.get.mockResolvedValue({data: []});
        await act(async () => {
            await render(<PresenceProvider><WorkersProvider><WorkersTable/></WorkersProvider></PresenceProvider>);
        })
        const [fullNameInput, emailInput, teamInput, workTimeInput] = screen.queryAllByPlaceholderText('Filtruj...');
        fireEvent.change(fullNameInput, {target: {value: 'Jan Kowalski'}});
        fireEvent.change(emailInput, {target: {value: 'abc@email.com'}});
        fireEvent.change(teamInput, {target: {value: 'dl'}});
        fireEvent.change(workTimeInput, {target: {value: '1/1'}});
        expect(fullNameInput).toHaveValue('Jan Kowalski');
        expect(emailInput).toHaveValue('abc@email.com');
        expect(teamInput).toHaveValue('dl');
        expect(workTimeInput).toHaveValue('1/1');
    });
});
