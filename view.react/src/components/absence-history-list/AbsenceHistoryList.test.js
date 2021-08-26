import {render, screen} from "@testing-library/react";

import {AbsenceHistoryProvider} from "../../contexts/absence-history-context/absenceHistoryContext";
import {AbsenceHistoryList} from "./AbsenceHistoryList";

describe("AbsenceHistoryList", () => {

    const currentYear = (new Date()).getFullYear();

    it("should show title, dropdown and table", () => {
       render(<AbsenceHistoryProvider>
           <AbsenceHistoryList/>
       </AbsenceHistoryProvider>);
       const title = screen.getByText("Historia nieobecności");
       const dropdown = screen.getByText(currentYear.toString());
       const table = screen.getByText("Utworzono")
       expect(title).toBeInTheDocument();
       expect(dropdown).toBeInTheDocument();
       expect(table).toBeInTheDocument();
    });


})