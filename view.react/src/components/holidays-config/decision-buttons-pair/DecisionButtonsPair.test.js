import {render, screen} from "@testing-library/react";

import {DecisionButtonsPair} from "./DecisionButtonsPair";

test("DecisionButtonsPair", () => {
    render(<DecisionButtonsPair/>);

    const rejectBtn = screen.getByTestId('reject-btn');
    const acceptBtn = screen.getByTestId('accept-btn');

    expect(rejectBtn).toBeInTheDocument();
    expect(acceptBtn).toBeInTheDocument();
})