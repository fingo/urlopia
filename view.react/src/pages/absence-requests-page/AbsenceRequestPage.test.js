import {render, screen} from "@testing-library/react";

import {CompanyRequestsList} from "../../components/company-requests-list/CompanyRequestsList";
import {CreateAbsenceRequestFormWrapper} from "../../components/create-absence-request-form/CreateAbsenceRequestFormWrapper";
import {TeamRequestsList} from "../../components/team-requests-list/TeamRequestsList";
import {UserRequestsList} from "../../components/user-requests-list/UserRequestsList";
import {USER_DATA_KEY} from "../../constants/session.keystorage";
import {mockSessionStorage} from "../../helpers/TestHelper";
import {AbsenceRequestsPage} from "./AbsenceRequestsPage";

jest.mock("../../components/company-requests-list/CompanyRequestsList")
jest.mock("../../components/team-requests-list/TeamRequestsList")
jest.mock("../../components/user-requests-list/UserRequestsList")
jest.mock("../../components/create-absence-request-form/CreateAbsenceRequestFormWrapper")

describe("AbsenceRequestPage", () => {
    const sessionStorageMock = mockSessionStorage()

    beforeEach(() => {
        sessionStorageMock.clear()
        UserRequestsList.mockImplementation(() => null)
        CreateAbsenceRequestFormWrapper.mockImplementation(() => null)
    })

    it('should not render company requests when user is not an admin', () => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            userRoles: ["ROLES_WORKER"]
        }))
        CompanyRequestsList.mockImplementation(() => <div>CompanyRequestListMock</div>)

        // when
        render(<AbsenceRequestsPage />)

        // then
        expect(screen.queryByText("CompanyRequestListMock")).not.toBeInTheDocument()
    })

    it('should not render team requests when user is not a leader', () => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            userRoles: ["ROLES_WORKER"]
        }))
        TeamRequestsList.mockImplementation(() => <div>TeamRequestListMock</div>)

        // when
        render(<AbsenceRequestsPage />)

        // then
        expect(screen.queryByText("TeamRequestListMock")).not.toBeInTheDocument()
    })

    it('should render company requests when user is an admin', () => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            userRoles: ["ROLES_ADMIN"]
        }))
        CompanyRequestsList.mockImplementation(() => <div>CompanyRequestListMock</div>)

        // when
        render(<AbsenceRequestsPage />)

        // then
        expect(screen.queryByText("CompanyRequestListMock")).toBeInTheDocument()
    })

    it('should render team requests when user is a leader', () => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            userRoles: ["ROLES_LEADER"]
        }))
        TeamRequestsList.mockImplementation(() => <div>TeamRequestListMock</div>)

        // when
        render(<AbsenceRequestsPage />)

        // then
        expect(screen.queryByText("TeamRequestListMock")).toBeInTheDocument()
    })
})