import {render, screen} from '@testing-library/react';

import {EvidenceReportModal} from "./EvidenceReportModal";

describe("EvidenceReportModal", () => {

    it("should render modal", ()=> {
        render(<EvidenceReportModal show={true} onHide={() => false} />)

        const evidenceModal = screen.getByTestId("evidenceModal");
        const evidenceModalTitle = screen.getByText("Ewidencja czasu pracy");
        const evidenceModalBody = screen.getByTestId("evidenceModalBody");
        const yearLabel = screen.getByText("Rok");
        const yearSelect = screen.getByText("Wybierz rok...");
        const personLabel = screen.getByText("Osoba");
        const personSelect = screen.getByText("Wybierz osobę...");
        const checkBox = screen.getByTestId("checkbox");
        const evidenceModalFooter = screen.getByTestId("evidenceModalFooter");
        const generateButton = screen.getByText('Generuj', { selector: 'button'});

        expect(evidenceModal).toBeInTheDocument();
        expect(evidenceModalTitle).toBeInTheDocument();
        expect(evidenceModalBody).toBeInTheDocument();
        expect(yearLabel).toBeInTheDocument();
        expect(yearSelect).toBeInTheDocument();
        expect(personLabel).toBeInTheDocument();
        expect(personSelect).toBeInTheDocument();
        expect(checkBox).toBeInTheDocument();
        expect(evidenceModalFooter).toBeInTheDocument();
        expect(generateButton).toBeInTheDocument();
    })

    it("doesnt allow to generate if year is not chosen", () => {
        render(<EvidenceReportModal show={true} onHide={() => false} />)

        const yearSelect = screen.getByText("Wybierz rok...");
        const generateButton = screen.getByText('Generuj', { selector: 'button'})

        expect(yearSelect).toBeInTheDocument();
        expect(generateButton).toBeDisabled();
    })

    it("doesnt allow to generate if there is no info about person", () => {
        render(<EvidenceReportModal show={true} onHide={() => false} />)

        const personSelect = screen.getByText("Wybierz osobę...");
        const checkBox = screen.getByTestId("checkbox");
        const generateBtn = screen.getByText('Generuj', { selector: 'button'});

        expect(personSelect).toBeInTheDocument();
        expect(checkBox).not.toBeChecked();
        expect(generateBtn).toBeDisabled();
    })
})