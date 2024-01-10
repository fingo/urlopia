import {render, screen} from "@testing-library/react";

import {AbsenceHistoryProvider} from "../../../contexts/absence-history-context/absenceHistoryContext";
import {WorkersProvider} from "../../../contexts/workers-context/workersContext";
import {AbsenceHistorySection} from "./AbsenceHistorySection";

test('shows "Historia użytkownika" label', () => {
    render(<AbsenceHistoryProvider><WorkersProvider><AbsenceHistorySection/></WorkersProvider></AbsenceHistoryProvider>)
    const label = screen.getByText('Historia użytkownika');
    expect(label).toBeInTheDocument();
})

test('shows "Pokaż więcej" button', () => {
    render(<AbsenceHistoryProvider><WorkersProvider><AbsenceHistorySection/></WorkersProvider></AbsenceHistoryProvider>)
    const button = screen.getByText('Pokaż więcej');
    expect(button.type).toBe('button');
    expect(button).toBeInTheDocument();
})