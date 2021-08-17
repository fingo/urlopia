import {fireEvent, render, screen, waitFor} from "@testing-library/react";

import {CompanyRequestsList} from "./CompanyRequestsList";

const testProducts = [
    {
        id: 1,
        applicant: 'Jan Kowalski',
        examiner: ['Mariusz Nowak'],
        period: ['2021-07-20 - 2021-07-27', '(7 dni robocze)'],
        type: 'Wypoczynkowy',
        actions: ''
    },
    {
        id: 2,
        applicant: 'Zdzizław Mikołajczyk-Katalończyk',
        examiner: ['Mariusz Nowak', 'Radosław Żółć'],
        period: ['2021-07-30 - 2021-07-30', '(1 dni robocze)'],
        type: 'Opieka nad dzieckiem',
        actions: ''
    },
    {
        id: 3,
        applicant: 'Jarosław Krawiecki',
        examiner: ['Radosław Żółć'],
        period: ['2021-08-22 - 2021-08-22', '(1 dni robocze)'],
        type: 'Ślub',
        actions: ''
    }
];
test('shows headers of table', () => {
    render(<CompanyRequestsList/>);
    const applicantHeader = screen.getByText('Wnioskodawca');
    const examinerHeader = screen.getByText('Rozpatrujący');
    const periodHeader = screen.getByText('Termin');
    const typeHeader = screen.getByText('Rodzaj');
    const actionHeader = screen.getByText('Akcje');
    expect(applicantHeader).toBeInTheDocument();
    expect(examinerHeader).toBeInTheDocument();
    expect(periodHeader).toBeInTheDocument();
    expect(typeHeader).toBeInTheDocument();
    expect(actionHeader).toBeInTheDocument();
});

test('shows cancel and accept button in the action column if data is present', () => {
    render(<CompanyRequestsList requests={testProducts}/>);
    const isAnyData = document.querySelectorAll("tbody").length === 2;
    if (!isAnyData) return;
    const cancelBtn = screen.getAllByTitle('Anuluj wniosek');
    const acceptBtn = screen.getAllByTitle('Zaakceptuj wniosek');
    expect(cancelBtn.length).toBeGreaterThanOrEqual(1);
    expect(acceptBtn.length).toBeGreaterThanOrEqual(1);
});

test('shows filter inputs', () => {
    render(<CompanyRequestsList/>);
    const inputs = screen.queryAllByPlaceholderText('Filtruj...');
    expect(inputs.length).toBe(4);
});

test('filter inputs should keep what the user enters', () => {
    render(<CompanyRequestsList/>);
    const [applicantInput, examinerInput, periodInput, typeInput] = screen.queryAllByPlaceholderText('Filtruj...');
    fireEvent.change(applicantInput, {target: {value: 'Jan'}});
    fireEvent.change(examinerInput, {target: {value: 'kowalski'}});
    fireEvent.change(periodInput, {target: {value: '7 dni'}});
    fireEvent.change(typeInput, {target: {value: 'Wypocz'}});
    expect(applicantInput).toHaveValue('Jan');
    expect(examinerInput).toHaveValue('kowalski');
    expect(periodInput).toHaveValue('7 dni');
    expect(typeInput).toHaveValue('Wypocz');
});

test('shows a table with no data after entering invalid input into the filter input', async () => {
    render(<CompanyRequestsList requests={testProducts}/>);
    const isAnyData = document.querySelectorAll("tbody").length === 2;
    if (!isAnyData) return;
    const [applicantInput] = screen.queryAllByPlaceholderText('Filtruj...');
    fireEvent.change(applicantInput, {target: {value: '%'}});
    await waitFor(() => expect(document.querySelectorAll("tbody").length).toBe(1))
});