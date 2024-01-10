import {fireEvent, render, screen, waitFor} from "@testing-library/react";

import {CompanyRequestsList} from "./CompanyRequestsList";

const sampleRequests = [
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
    render(<CompanyRequestsList requests={sampleRequests} rejectRequest={() => null} acceptRequest={() => null}/>);
    const applicantHeader = screen.getByText('Wnioskodawca');
    const examinerHeader = screen.getByText('Rozpatrujący');
    const periodHeader = screen.getByText('Termin');
    const actionHeader = screen.getByText('Akcje');
    expect(applicantHeader).toBeInTheDocument();
    expect(examinerHeader).toBeInTheDocument();
    expect(periodHeader).toBeInTheDocument();
    expect(actionHeader).toBeInTheDocument();
});

test('shows cancel and accept button in the action column if data is present', () => {
    render(<CompanyRequestsList requests={sampleRequests} rejectRequest={() => null} acceptRequest={() => null}/>);
    const isAnyData = document.querySelectorAll("tbody").length === 2;
    if (!isAnyData) return;
    const cancelBtn = screen.getAllByTitle('Odrzuć wniosek');
    const acceptBtn = screen.getAllByTitle('Zaakceptuj wniosek');
    expect(cancelBtn.length).toBeGreaterThanOrEqual(1);
    expect(acceptBtn.length).toBeGreaterThanOrEqual(1);
});

test('shows filter inputs', () => {
    render(<CompanyRequestsList requests={sampleRequests} rejectRequest={() => null} acceptRequest={() => null}/>);
    const inputs = screen.queryAllByPlaceholderText('Filtruj...');
    expect(inputs.length).toBe(2);
});

test('filter inputs should keep what the user enters', () => {
    render(<CompanyRequestsList requests={sampleRequests} rejectRequest={() => null} acceptRequest={() => null}/>);
    const [applicantInput, examinerInput] = screen.queryAllByPlaceholderText('Filtruj...');
    fireEvent.change(applicantInput, {target: {value: 'Jan'}});
    fireEvent.change(examinerInput, {target: {value: 'kowalski'}});
    expect(applicantInput).toHaveValue('Jan');
    expect(examinerInput).toHaveValue('kowalski');
});

test('shows a table with no data after entering invalid input into the filter input', async () => {
    render(<CompanyRequestsList requests={sampleRequests} rejectRequest={() => null} acceptRequest={() => null}/>);
    const isAnyData = document.querySelectorAll("tbody").length === 2;
    if (!isAnyData) return;
    const [applicantInput] = screen.queryAllByPlaceholderText('Filtruj...');
    fireEvent.change(applicantInput, {target: {value: '%'}});
    await waitFor(() => expect(document.querySelectorAll("tbody").length).toBe(1))
});
