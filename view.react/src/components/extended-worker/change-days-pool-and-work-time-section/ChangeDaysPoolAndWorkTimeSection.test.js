import {render, screen} from "@testing-library/react";

import {AbsenceHistoryProvider} from "../../../contexts/absence-history-context/absenceHistoryContext";
import {VacationDaysProvider} from "../../../contexts/vacation-days-context/vacationDaysContext";
import {WorkersProvider} from "../../../contexts/workers-context/workersContext";
import {ChangeDaysPoolAndWorkTimeSection} from "./ChangeDaysPoolAndWorkTimeSection";

test('shows "Zmień etat" label', () => {
    render(
        <VacationDaysProvider>
            <AbsenceHistoryProvider>
                <WorkersProvider>
                    <ChangeDaysPoolAndWorkTimeSection workTime='1/1'/>
                </WorkersProvider>
            </AbsenceHistoryProvider>
        </VacationDaysProvider>)
    const label = screen.getByText('Zmień etat');
    expect(label).toBeInTheDocument();
})

test('shows form selector', () => {
    render(
        <VacationDaysProvider>
            <AbsenceHistoryProvider>
                <WorkersProvider>
                    <ChangeDaysPoolAndWorkTimeSection workTime='1/1'/>
                </WorkersProvider>
            </AbsenceHistoryProvider>
        </VacationDaysProvider>)
    const selectors = screen.getByTestId('selector');
    expect(selectors).toBeInTheDocument();
});

test('shows select options label', () => {
    render(
        <VacationDaysProvider>
            <AbsenceHistoryProvider>
                <WorkersProvider>
                    <ChangeDaysPoolAndWorkTimeSection workTime='1/1'/>
                </WorkersProvider>
            </AbsenceHistoryProvider>
        </VacationDaysProvider>)
    const label = screen.getByText('Zmień etat');
    expect(label).toBeInTheDocument();
})

test('selector enabled by default', () => {
    render(
        <VacationDaysProvider>
            <AbsenceHistoryProvider>
                <WorkersProvider>
                    <ChangeDaysPoolAndWorkTimeSection workTime='1/1'/>
                </WorkersProvider>
            </AbsenceHistoryProvider>
        </VacationDaysProvider>)
    const selector = screen.getByTestId('selector');
    expect(selector).not.toBeDisabled();
})