import {render, screen} from "@testing-library/react";

import {CompanyRequestsListWrapper} from "../../components/company-requests-list/company-requests-list-wrapper/CompanyRequestsListWrapper";
import {CreateAbsenceRequestFormWrapper} from "../../components/create-absence-request-form/create-absence-request-form-wrapper/CreateAbsenceRequestFormWrapper";
import {TeamRequestsListWrapper} from "../../components/team-requests-list/team-requests-list-wrapper/TeamRequestsListWrapper";
import {UserRequestsListWrapper} from "../../components/user-requests-list/user-requests-list-wrapper/UserRequestsListWrapper";
import {USER_DATA_KEY} from "../../constants/session.keystorage";
import {mockLocalStorage} from "../../helpers/TestHelper";
import {AbsenceRequestsPage} from "./AbsenceRequestsPage";

jest.mock("../../components/user-requests-list/user-requests-list-wrapper/UserRequestsListWrapper")
jest.mock("../../components/team-requests-list/team-requests-list-wrapper/TeamRequestsListWrapper")
jest.mock("../../components/company-requests-list/company-requests-list-wrapper/CompanyRequestsListWrapper")
jest.mock("../../components/create-absence-request-form/create-absence-request-form-wrapper/CreateAbsenceRequestFormWrapper")

describe("AbsenceRequestPage", () => {
    const sessionStorageMock = mockLocalStorage()

    beforeEach(() => {
        sessionStorageMock.clear()
        UserRequestsListWrapper.mockImplementation(() => null)
        TeamRequestsListWrapper.mockImplementation(() => null)
        CompanyRequestsListWrapper.mockImplementation(() => null)
        CreateAbsenceRequestFormWrapper.mockImplementation(() => null)
    })

    it('should not render company requests when user is not an admin', () => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            userRoles: ["ROLES_WORKER"]
        }))
        CompanyRequestsListWrapper.mockImplementation(() => <div>CompanyRequestListMock</div>)

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
        TeamRequestsListWrapper.mockImplementation(() => <div>TeamRequestListMock</div>)

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
        CompanyRequestsListWrapper.mockImplementation(() => <div>CompanyRequestListMock</div>)

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
        TeamRequestsListWrapper.mockImplementation(() => <div>TeamRequestListMock</div>)

        // when
        render(<AbsenceRequestsPage />)

        // then
        expect(screen.queryByText("TeamRequestListMock")).toBeInTheDocument()
    })
})