import {render, screen} from '@testing-library/react';
import {BrowserRouter as Router} from "react-router-dom";

import {USER_DATA_KEY} from "../../constants/session.keystorage";
import {mockSessionStorage} from "../../helpers/TestHelper";
import {Sidebar} from "./Sidebar";

describe("Sidebar", () => {
    const sessionStorageMock = mockSessionStorage()

    beforeEach(() => {
        sessionStorageMock.clear()
    })

    it("should show correct links when user is not an admin", () => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            userRoles: ["ROLES_WORKER"]
        }))

        // when
        render(<Router><Sidebar onClickLinkOrOutside={() => {}}/></Router>);

        // then
        expect(screen.queryByText('Kalendarz')).toBeInTheDocument()
        expect(screen.queryByText('Wnioski urlopowe')).toBeInTheDocument()
        expect(screen.queryByText('Historia użytkownika')).toBeInTheDocument()
        expect(screen.queryByText('Pracownicy')).not.toBeInTheDocument()
        expect(screen.queryByText('Współpracownicy')).not.toBeInTheDocument()
        expect(screen.queryByText('Dni świąteczne')).not.toBeInTheDocument()
        expect(screen.queryByText('Raporty')).not.toBeInTheDocument()
    })

    it("should show correct links when user is an admin", () => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            userRoles: ["ROLES_WORKER", "ROLES_ADMIN"]
        }))

        // when
        render(<Router><Sidebar onClickLinkOrOutside={() => {}}/></Router>);

        // then
        expect(screen.queryByText('Kalendarz')).toBeInTheDocument()
        expect(screen.queryByText('Wnioski urlopowe')).toBeInTheDocument()
        expect(screen.queryByText('Historia użytkownika')).toBeInTheDocument()
        expect(screen.queryByText('Pracownicy')).toBeInTheDocument()
        expect(screen.queryByText('Współpracownicy')).toBeInTheDocument()
        expect(screen.queryByText('Dni świąteczne')).toBeInTheDocument()
        expect(screen.queryByText('Raporty')).toBeInTheDocument()
    })

    it("should render links with correct routes", () => {
        // given
        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            userRoles: ["ROLES_WORKER", "ROLES_ADMIN"]
        }))

        // when
        render(<Router><Sidebar onClickLinkOrOutside={() => {}}/></Router>);

        // then
        expect(screen.queryByText('Kalendarz').href).toMatch(/calendar/)
        expect(screen.queryByText(linkWithText('Wnioski urlopowe')).href).toMatch(/requests/)
        expect(screen.queryByText('Historia użytkownika').href).toMatch(/history/)
        expect(screen.queryByText('Pracownicy').href).toMatch(/workers/)
        expect(screen.queryByText('Współpracownicy').href).toMatch(/associates/)
        expect(screen.queryByText('Dni świąteczne').href).toMatch(/holidays/)
        expect(screen.queryByText('Raporty').href).toMatch(/reports/)
    })
})

const linkWithText = (text) => (content, node) => {
    const nodeWithText = Array.from(node.children).find(child => child.textContent === text)
    return node.href && nodeWithText
}
