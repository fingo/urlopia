import {act, fireEvent, render, screen} from "@testing-library/react";

import {WorkersTable} from "./WorkersTable";
import {WorkersProvider} from "../../contexts/workers-context/workersContext";
import {mockSessionStorage} from "../../helpers/TestHelper";
import {USER_DATA_KEY} from "../../constants/session.keystorage";
import axios from "axios";

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
            await render(<WorkersProvider><WorkersTable/></WorkersProvider>);
            const fullNameHeader = screen.getByText('Imię i nazwisko');
            const emailHeader = screen.getByText('E-mail');
            const teamHeader = screen.getByText('Zespół');
            const workTimeHeader = screen.getByText('Etat');
            expect(fullNameHeader).toBeInTheDocument();
            expect(emailHeader).toBeInTheDocument();
            expect(teamHeader).toBeInTheDocument();
            expect(workTimeHeader).toBeInTheDocument();
        })
    })

    it('should show headers of table for associates', async () => {
        axios.get.mockResolvedValue({data: []});
        await act(async () => {
            await render(<WorkersProvider><WorkersTable isEC={false}/></WorkersProvider>);
            const fullNameHeader = screen.getByText('Imię i nazwisko');
            const emailHeader = screen.getByText('E-mail');
            const teamHeader = screen.getByText('Zespół');
            expect(fullNameHeader).toBeInTheDocument();
            expect(emailHeader).toBeInTheDocument();
            expect(teamHeader).toBeInTheDocument();
        })
    })

    it('should show title "Pracownicy" for employees', async () => {
        axios.get.mockResolvedValue({data: []});
        await act(async () => {
            await render(<WorkersProvider><WorkersTable/></WorkersProvider>);
            const title = screen.getByText('Pracownicy');
            expect(title).toBeInTheDocument();
        })
    })

    it('shows title "Współpracownicy" for associates', async () => {
        axios.get.mockResolvedValue({data: []});
        await act(async () => {
            await render(<WorkersProvider><WorkersTable isEC={false}/></WorkersProvider>);
            const title = screen.getByText('Współpracownicy');
            expect(title).toBeInTheDocument();
        })
    })

    it('should show filter inputs', async () => {
        axios.get.mockResolvedValue({data: []});
        await act(async () => {
            await render(<WorkersProvider><WorkersTable/></WorkersProvider>);
            const inputs = screen.queryAllByPlaceholderText('Filtruj...');
            expect(inputs.length).toBe(4);
        })
    })

    it('filter inputs should keep what the user enters', async () => {
        axios.get.mockResolvedValue({data: []});
        await act(async () => {
            await render(<WorkersProvider><WorkersTable/></WorkersProvider>);
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
})
