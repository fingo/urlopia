import {render, screen} from "@testing-library/react";

import {ConfirmationLabel} from "./ConfirmationLabel";

describe("ConfirmationLabel", () => {
    it("should not render any text when fetching is true", () => {
        render(<ConfirmationLabel fetching={true}/>)
        expect(screen.queryByText("W tym dniu nie zgłosiłeś jeszcze swojej obecności")).not.toBeInTheDocument()
    })

    it("should render correct label when confirmation is present", () => {
        const sampleConfirmation = {
            startTime: "08:00",
            endTime: "16:00"
        }
        render(<ConfirmationLabel fetching={false} confirmation={sampleConfirmation}/>)
        const expectedText = `W tym dniu zgłosiłeś swoją obecność w godzinach: ${sampleConfirmation.startTime} - ${sampleConfirmation.endTime}`
        expect(screen.queryByText(expectedText)).toBeInTheDocument()
    })

    it("should render correct label when confirmation is not present", () => {
        render(<ConfirmationLabel fetching={false}/>)
        expect(screen.queryByText("W tym dniu nie zgłosiłeś jeszcze swojej obecności")).toBeInTheDocument()
    })
})