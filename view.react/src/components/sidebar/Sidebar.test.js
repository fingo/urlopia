import {render, screen} from '@testing-library/react';
import {BrowserRouter as Router} from "react-router-dom";

import {Sidebar} from "./Sidebar";

test('on desktop show the sidebar', () => {
    render(<Router><Sidebar onClickOutside={() => null}/></Router>);
    const calendar = screen.getByText('Kalendarz');
    const requests = screen.getByText('Wnioski urlopowe');
    const history = screen.getByText('Historia nieobecności');
    const workers = screen.getByText('Pracownicy');
    const coworkers = screen.getByText('Współpracownicy');
    const holidays = screen.getByText('Dni świąteczne');
    const raports = screen.getByText('Raporty');
    expect(calendar).toBeInTheDocument();
    expect(requests).toBeInTheDocument();
    expect(history).toBeInTheDocument();
    expect(workers).toBeInTheDocument();
    expect(coworkers).toBeInTheDocument();
    expect(holidays).toBeInTheDocument();
    expect(raports).toBeInTheDocument();
});

test('check the correctness of the links', () => {
    render(<Router><Sidebar onClickOutside={() => null}/></Router>);
    const calendar = screen.getByText(/Kalendarz/);
    const requests = screen.getByText(/Wnioski urlopowe/);
    const history = screen.getByText(/Historia nieobecności/);
    const workers = screen.getByText(/Pracownicy/);
    const coworkers = screen.getByText(/Współpracownicy/);
    const holidays = screen.getByText(/Dni świąteczne/);
    const raports = screen.getByText(/Raport/);

    expect(calendar.href).toMatch(/calendar/);
    expect(requests.href).toMatch(/requests/);
    expect(history.href).toMatch(/history/);
    expect(workers.href).toMatch(/workers/);
    expect(coworkers.href).toMatch(/associates/);
    expect(holidays.href).toMatch(/holidays/);
    expect(raports.href).toMatch(/reports/);
});
