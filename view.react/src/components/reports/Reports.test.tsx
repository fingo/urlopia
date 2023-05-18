import {fireEvent, render, screen} from '@testing-library/react';

import {Reports} from "./Reports";

describe("Reports", () => {

    it("should show two report's buttons", ()=> {
        render(<Reports/>)

        const evidenceButton = screen.getByText("Ewidencja czasu pracy");
        const presenceButton = screen.getByText("Miesięczna lista obecności");

        expect(evidenceButton).toBeInTheDocument();
        expect(presenceButton).toBeInTheDocument();
    })

    it("should show EvidenceReportModal after clicking evidenceButton", () => {
        render(<Reports/>)

        const evidenceButton = screen.getByText("Ewidencja czasu pracy");

        fireEvent.click(evidenceButton);

        expect(screen.getByTestId("evidenceModal")).toBeInTheDocument();
    })

    it("should show PresenceListModal after clicking presenceButton", () => {
        render(<Reports/>)

        const presenceButton = screen.getByText("Miesięczna lista obecności");

        fireEvent.click(presenceButton);

        expect(screen.getByTestId("presenceModal")).toBeInTheDocument();
    })
})