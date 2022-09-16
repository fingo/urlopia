import {act, render, screen} from "@testing-library/react";

import {USER_DATA_KEY} from "../../constants/session.keystorage";
import {AppInfoProvider} from "../../contexts/app-info-context/appInfoContext";
import {mockLocalStorage} from "../../helpers/TestHelper";
import {MainContentRouting} from "../../router/MainContentRouting";
import {AcceptanceLoader} from "../acceptance-loader/AcceptanceLoader";
import {Sidebar} from "../sidebar/Sidebar";
import {TopBar} from "../topbar/TopBar";
import {App} from "./App";

jest.mock("../acceptance-loader/AcceptanceLoader")
jest.mock("../../router/MainContentRouting")
jest.mock("../sidebar/Sidebar")
jest.mock("../topbar/TopBar")

describe("App", () => {
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
            roles: ["ROLES_WORKER"] //console.log(spojrzec)
        }))

        // when
        act(() => {
            render(<AppInfoProvider><App/></AppInfoProvider>)
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
            render(<AppInfoProvider><App/></AppInfoProvider>)
        })

        // then
        expect(screen.queryByText("AcceptanceLoaderMock")).toBeInTheDocument()
    })
})