import {render, screen} from "@testing-library/react";

import {Page404} from "./Page404";

test('renders Page404', () => {
    render(<Page404/>);
    const img = screen.getByAltText('404');
    const info = screen.getByText('Wygląda na to, że znajdujesz się w niewłaściwym miejscu...');
    const returnBtn = screen.getByTestId('return-btn');

    expect(img).toBeInTheDocument();
    expect(info).toBeInTheDocument();
    expect(returnBtn).toBeInTheDocument();
})

