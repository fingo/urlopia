import {fireEvent,render, screen} from "@testing-library/react";

import {PresenceListModal} from "./PresenceListModal";

describe("PresenceListModal", () => {

    it("should display PresenceListModal", () => {
        render(<PresenceListModal show={true} onHide={() => false}/>);

        const modal = screen.getByTestId("presenceModal");
        const modalTitle = screen.getByText('Lista obecności');
        const bodyLabel = screen.getByText('Miesiąc');
        const generateButton = screen.getByTestId('generateButton');

        expect(modal).toBeInTheDocument();
        expect(modalTitle).toBeInTheDocument();
        expect(bodyLabel).toBeInTheDocument();
        expect(generateButton).toBeInTheDocument();
    })

    it('should display Generuj on button after render', () => {
        render(<PresenceListModal show={true} onHide={() => false}/>);

        expect(screen.getByText('Generuj', { selector: 'button' })).toBeInTheDocument()
    })

    it('should display ... when button ic clicked', () => {
        render(<PresenceListModal show={true} onHide={() => false}/>);

        fireEvent.click(screen.getByTestId('generateButton'));
        expect(screen.getByText('...', { selector: 'button'})).toBeInTheDocument();
    })
    }
)