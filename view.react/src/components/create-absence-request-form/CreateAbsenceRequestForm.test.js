import {render, screen} from "@testing-library/react";

import {CreateAbsenceRequestForm} from "./CreateAbsenceRequestForm";

test('shows label "Typ wniosku"', () => {
    render(<CreateAbsenceRequestForm createRequest={() => null} holidays={[]}/>);
    const label = screen.getByText(/Typ wniosku/);
    expect(label).toBeInTheDocument();
});

test('shows two form selectors', () => {
    render(<CreateAbsenceRequestForm createRequest={() => null} holidays={[]}/>);
    const selectors = screen.getAllByTestId('selector');
    expect(selectors.length).toBe(2);
});

test('shows both form placeholders', () => {
    render(<CreateAbsenceRequestForm createRequest={() => null} holidays={[]}/>);
    const fstSelector = screen.getByText(/Wybierz typ wniosku/);
    const sndSelector = screen.getByText(/Wybierz okoliczność/);
    expect(fstSelector).toBeInTheDocument();
    expect(sndSelector).toBeInTheDocument();
});

test('"Wybierz typ wniosku" selector enabled by default', () => {
    render(<CreateAbsenceRequestForm createRequest={() => null} holidays={[]}/>);
    const selector = screen.getByText(/Wybierz typ wniosku/);
    expect(selector).not.toBeDisabled();
});

test('"Wybierz okoliczność" selector disabled by default', () => {
    render(<CreateAbsenceRequestForm createRequest={() => null} holidays={[]}/>);
    const selector = screen.getByText(/Wybierz okoliczność/);
    expect(selector).toBeDisabled();
});

test('Calendar disabled by default', () => {
    render(<CreateAbsenceRequestForm createRequest={() => null} holidays={[]}/>);
    const overlay = screen.getByTestId('overlay');
    const infoHeader = screen.getByTestId('infoHeader');
    expect(overlay).toBeInTheDocument();
    expect(overlay).toBeVisible();
    expect(infoHeader).toBeVisible();
    expect(infoHeader).toBeVisible();
});

test('shows "Złóż wniosek" button', () => {
    render(<CreateAbsenceRequestForm createRequest={() => null} holidays={[]}/>);
    const btn = screen.getByText(/Złóż wniosek/);
    expect(btn).toBeInTheDocument();
    expect(btn).toBeVisible();
    expect(btn.type).toBe('button');
});

test('shows "Liczba dni roboczych" label', () => {
    render(<CreateAbsenceRequestForm createRequest={() => null} holidays={[]}/>);
    const label = screen.getByText(/Liczba dni roboczych/);
    expect(label).toBeInTheDocument();
    expect(label).toBeVisible();
});
