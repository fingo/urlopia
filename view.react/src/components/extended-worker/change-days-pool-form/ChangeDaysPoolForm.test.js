import {render, screen} from "@testing-library/react";

import {ChangeDaysPoolForm} from "./ChangeDaysPoolForm";

test('shows "Zmień pulę urlopu (dni)" label', () => {
    render(<ChangeDaysPoolForm onSubmit={() => {}}/>)
    const label = screen.getByText('Zmień pulę urlopu (dni)');
    expect(label).toBeInTheDocument();
})