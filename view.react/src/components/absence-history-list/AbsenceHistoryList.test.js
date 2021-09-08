import {render, screen} from "@testing-library/react";
import {BrowserRouter} from "react-router-dom";

import {AbsenceHistoryProvider} from "../../contexts/absence-history-context/absenceHistoryContext";
import {AbsenceHistoryList} from "./AbsenceHistoryList";

describe("AbsenceHistoryList", () => {

    const currentYear = (new Date()).getFullYear();

    it("should show title, dropdown and table", () => {
        render(
            <BrowserRouter>
                <AbsenceHistoryProvider>
                    <AbsenceHistoryList fetchHistoryLogs={() => {}}/>
                </AbsenceHistoryProvider>
            </BrowserRouter>);
        const title = screen.getByText("Historia użytkownika");
        const dropdown = screen.getByText(currentYear.toString());
        const table = screen.getByText("Utworzono")
        expect(title).toBeInTheDocument();
        expect(dropdown).toBeInTheDocument();
        expect(table).toBeInTheDocument();
    });


})