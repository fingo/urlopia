import {act, render, screen} from "@testing-library/react";
import axios from "axios";

import {USER_DATA_KEY} from "../../constants/session.keystorage";
import {PresenceProvider} from "../../contexts/presence-context/presenceContext";
import {mockSessionStorage} from "../../helpers/TestHelper";
import {PresenceConfirmationPanel} from "./PresenceConfirmationPanel";

jest.mock('axios')

describe('PresenceConfirmationPanel', () => {
    const sessionStorageMock = mockSessionStorage()

    beforeAll(() => {
        sessionStorageMock.clear()
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            userId: 1,
            userRoles: []
        }))
    })

    it('should fetch presence confirmations on initial render', async () => {
        axios.get.mockResolvedValue({data: []})
        await act(async () => {
            await render(<PresenceProvider><PresenceConfirmationPanel /></PresenceProvider>)
            expect(axios.get).toBeCalled()
        })
    })

    it('should render 3 inputs', async () => {
        axios.get.mockResolvedValue({data: []})
        await act(async () => {
            await render(<PresenceProvider><PresenceConfirmationPanel /></PresenceProvider>)
        })
        const datePicker = screen.getByText("Data")
        const timePeriodPickers = screen.getByText("Godziny pracy")
        expect(datePicker).toBeInTheDocument()
        expect(timePeriodPickers).toBeInTheDocument()
    })

    it('should render a button', async () => {
        axios.get.mockResolvedValue({data: []})
        await act(async () => {
            await render(<PresenceProvider><PresenceConfirmationPanel /></PresenceProvider>)
        })
        const button = screen.getByText("Zgłoś obecność")
        expect(button).toBeInTheDocument()
    })
})
