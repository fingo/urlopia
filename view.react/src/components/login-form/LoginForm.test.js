import {act, fireEvent, render, screen, waitFor} from "@testing-library/react";

import {LoginForm} from "./LoginForm";

test('shows Urlopia banner', () => {
    render(<LoginForm />);
    const urlopiaBanner = screen.getByText('Urlopia');
    expect(urlopiaBanner).toBeInTheDocument();
});

test('shows input forms for email and password', () => {
    render(<LoginForm />);
    const emailInput = screen.queryByPlaceholderText('Email');
    const passwordInput = screen.queryByPlaceholderText('Hasło');
    expect(emailInput).toBeInTheDocument();
    expect(passwordInput).toBeInTheDocument();
});

test('Email input should keep what the user enters', () => {
    render(<LoginForm />);
    const emailInput = screen.queryByPlaceholderText('Email');
    fireEvent.change(emailInput, {target: {value: 'test@abc.pl'}});
    expect(emailInput).toHaveValue('test@abc.pl');
});

test('Password input should keep what the user enters', () => {
    render(<LoginForm />);
    const passwordInput = screen.queryByPlaceholderText('Hasło');
    fireEvent.change(passwordInput, {target: {value: 'password'}});
    expect(passwordInput).toHaveValue('password');
});

test('show "Brak odpowiedzi" after clicking "Zaloguj się" button' , async () => {
    render(<LoginForm />);
    const emailInput = screen.queryByPlaceholderText('Email');
    const passwordInput = screen.queryByPlaceholderText('Hasło');
    const loginBtn = screen.queryByTestId('login-btn');
    const errorMsg = screen.queryByTestId('error-msg');

    fireEvent.change(emailInput, {target: {value: 'test@abc.pl'}});
    fireEvent.change(passwordInput, {target: {value: 'password'}});

    await act(async () => {
        await fireEvent.click(loginBtn);
        await waitFor(() => expect(errorMsg.textContent).toBe("Brak odpowiedzi"));
    });
});

test('show "..." after clicking the "Zaloguj się" button' , async () => {
    render(<LoginForm />);
    const loginBtn = screen.queryByTestId('login-btn');

    expect(loginBtn.textContent).toEqual('Zaloguj się');

    await act(async () => {
        await fireEvent.click(loginBtn);
        expect(loginBtn.textContent).toEqual('...');
    });
});
