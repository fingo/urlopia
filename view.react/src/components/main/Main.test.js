import {act, render, screen} from "@testing-library/react";

import {USER_DATA_KEY} from "../../constants/session.keystorage";
import {AppInfoProvider} from "../../contexts/app-info-context/appInfoContext";
import {mockLocalStorage} from "../../helpers/TestHelper";
import {MainContentRouting} from "../../router/MainContentRouting";
import {AcceptanceLoader} from "../acceptance-loader/AcceptanceLoader";
import {Sidebar} from "../sidebar/Sidebar";
import {TopBar} from "../topbar/TopBar";
import {Main} from "./Main";

jest.mock("../acceptance-loader/AcceptanceLoader")
jest.mock("../../router/MainContentRouting")
jest.mock("../sidebar/Sidebar")
jest.mock("../topbar/TopBar")

describe("Main", () => {
    const sessionStorageMock = mockLocalStorage()

    beforeEach(() => {
        sessionStorageMock.clear()
        AcceptanceLoader.mockImplementation(() => <div>AcceptanceLoaderMock</div>)
        MainContentRouting.mockImplementation(() => null)
        Sidebar.mockImplementation(() => null)
        TopBar.mockImplementation(() => null)
    })

    it ('should not render AcceptanceLoader when user is not a leader', () => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            roles: ["ROLES_WORKER"]
        }))

        // when
        act(() => {
            render(<AppInfoProvider><Main/></AppInfoProvider>)
        })

        // then
        expect(screen.queryByText("AcceptanceLoaderMock")).not.toBeInTheDocument()
    })

    it('should render AcceptanceLoader when user is a leader', () => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            roles: ["ROLES_WORKER", "ROLES_LEADER"]
        }))

        // when
        act(() => {
            render(<AppInfoProvider><Main/></AppInfoProvider>)
        })

        // then
        expect(screen.queryByText("AcceptanceLoaderMock")).toBeInTheDocument()
    })
})