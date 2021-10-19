import {render,screen} from '@testing-library/react';

import {WorkersProvider} from "../../../contexts/workers-context/workersContext";
import {ReportsSection} from "./ReportsSection";

test('shows "Ewidencja czasu pracy" button', () => {
    render(<WorkersProvider><ReportsSection/></WorkersProvider>);
    const button = screen.getByText(/Ewidencja czasu pracy/);
    expect(button.type).toBe('button');
    expect(button).toBeInTheDocument();
})

test('shows years selector', () => {
    render(<WorkersProvider><ReportsSection/></WorkersProvider>);
    const selector = screen.getByTestId('selector');
    expect(selector).toBeInTheDocument();
})

test('selector enabled by default', () => {
    render(<WorkersProvider><ReportsSection/></WorkersProvider>);
    const selector = screen.getByTestId('selector');
    expect(selector).not.toBeDisabled();
})