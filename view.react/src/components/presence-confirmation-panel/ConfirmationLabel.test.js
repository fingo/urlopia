import {render, screen} from "@testing-library/react";

import {ConfirmationLabel} from "./ConfirmationLabel";

describe("ConfirmationLabel", () => {
    it("should not render any text when fetching is true", () => {
        render(<ConfirmationLabel fetching={true} isOwnPresence={true}/>)
        expect(screen.queryByText("W tym dniu nie zgłosiłeś jeszcze swojej obecności")).not.toBeInTheDocument()
    })

    it("should render correct label when confirmation is present and it's user presence", () => {
        const sampleConfirmation = {
            startTime: "08:00",
            endTime: "16:00"
        }
        render(<ConfirmationLabel fetching={false} confirmation={sampleConfirmation} isOwnPresence={true}/>)
        const expectedText = `W tym dniu zgłosiłeś swoją obecność w godzinach: ${sampleConfirmation.startTime} - ${sampleConfirmation.endTime}`
        expect(screen.queryByText(expectedText)).toBeInTheDocument()
    })

    it("should render correct label when confirmation is present and it's not user presence", () => {
        const sampleConfirmation = {
            startTime: "08:00",
            endTime: "16:00"
        }
        render(<ConfirmationLabel fetching={false} confirmation={sampleConfirmation} isOwnPresence={false}/>)
        const expectedText = `W tym dniu pracownik zgłosił swoją obecność w godzinach: ${sampleConfirmation.startTime} - ${sampleConfirmation.endTime}`
        expect(screen.queryByText(expectedText)).toBeInTheDocument()
    })

    it("should render correct label when confirmation is not present and it's user presence", () => {
        render(<ConfirmationLabel fetching={false}  isOwnPresence={true}/>)
        expect(screen.queryByText("W tym dniu nie zgłosiłeś jeszcze swojej obecności")).toBeInTheDocument()
    })

    it("should render correct label when confirmation is not present and it's not user presence", () => {
        render(<ConfirmationLabel fetching={false}  isOwnPresence={false}/>)
        expect(screen.queryByText("W tym dniu pracownik nie zgłosił swojej obecności")).toBeInTheDocument()
    })
})