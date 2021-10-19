import {fireEvent, render, screen} from "@testing-library/react";

import {MonthPicker} from "./MonthPicker";

describe("MonthPicker", () => {

    const range = {
        min: { year: 2020, month: 7},
        max: { year: 2021, month: 8}
    };

    const chosenMonth = {year: 2021, month: 6}

    it("should not display monthButton when component is rendered", () => {

        render (<MonthPicker range={range} chosenMonth={chosenMonth} setChosenMonth={() => true}/>);
        const monthButton = screen.getByTestId("monthButton");

        expect(monthButton).toBeInTheDocument();
    })

    it("should display ReactMonthPicker when monthButton is clicked", () => {
        render (<MonthPicker range={range} chosenMonth={chosenMonth} setChosenMonth={() => true}/>);

        const monthButton = screen.getByTestId('monthButton');

        fireEvent.click(monthButton);

        expect(screen.getByText(`${chosenMonth.year}`)).toBeInTheDocument();
    })

    it("should display formatted month text on monthButton", () => {
        render (<MonthPicker range={range} chosenMonth={chosenMonth} setChosenMonth={() => true}/>);

        const monthButton = screen.getByText("CZE-2021");

        expect(monthButton).toBeInTheDocument();
    })
})