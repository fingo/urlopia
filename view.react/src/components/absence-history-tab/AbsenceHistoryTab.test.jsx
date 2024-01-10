import {render, screen} from "@testing-library/react";

import {USER_DATA_KEY} from "../../constants/session.keystorage";
import {mockLocalStorage} from "../../helpers/TestHelper";
import {AbsenceHistoryTab} from "./AbsenceHistoryTab";


    const absenceHistory = [
        {
            "id": 1,
            "comment": "2021-11-12 - 2021-11-12 (Narodziny dziecka)",
            "hours": 0.0,
            "userWorkTime": 8.0,
            "created": "2021-08-20 11:53:26",
            "hoursRemaining": 10.0,
            "workTimeNumerator": 1,
            "workTimeDenominator": 1,
            "deciders": []
        }
    ];
    const isHidden = false;
    const sessionStorageMock = mockLocalStorage()


test('should show all headers of table if isHidden equals false', () => {

        sessionStorageMock.setItem(USER_DATA_KEY, JSON.stringify({
            roles: ["ROLES_WORKER"],
            ec: true,
            admin: false
        }))
        render(<AbsenceHistoryTab logs={absenceHistory} isHidden={isHidden}/>)
        const createdHeader = screen.getByText('Utworzono');
        const decidersHeader = screen.getByText('Rozpatrujący');
        const hoursHeader = screen.getByText('Wartość zmiany');
        const hoursRemainingHeader = screen.getByText('Pozostały urlop');
        const commentHeader = screen.getByText('Komentarz');
        expect(createdHeader).toBeInTheDocument();
        expect(decidersHeader).toBeInTheDocument();
        expect(hoursHeader).toBeInTheDocument();
        expect(hoursRemainingHeader).toBeInTheDocument();
        expect(commentHeader).toBeInTheDocument();
    });

    test('should not show all headers if isHidden equals true', () => {
        render(<AbsenceHistoryTab logs={absenceHistory} isHidden={!isHidden}/>);
        const createdHeader = screen.getByText('Utworzono');
        const hoursHeader = screen.getByText('Wartość zmiany');
        const commentHeader = screen.getByText('Komentarz');

        expect(createdHeader).toBeInTheDocument()
        expect(() => screen.getByText('Rozpatrujący')).toThrow();
        expect(hoursHeader).toBeInTheDocument();
        expect(() => screen.getByText("Pozostały urlop")).toThrow();
        expect(commentHeader).toBeInTheDocument();
    });

    test('should display hours in color green if its value is positive', () => {
        const sampleAbsenceHistory = [
            {
            "id": 1,
            "comment": "2021-11-12 - 2021-11-12 (Narodziny dziecka)",
            "hours": 2.0,
            "userWorkTime": 8.0,
            "created": "2021-08-20 11:53:26",
            "hoursRemaining": 10.0,
            "workTimeNumerator": 1,
            "workTimeDenominator": 1,
            "deciders": []
            }
        ];
        render(<AbsenceHistoryTab logs={sampleAbsenceHistory} isHidden={isHidden}/>);
        const hours = screen.getByText("2h");
        expect(hours).toHaveStyle({color: "green"});
    });

    test('should display hours in color red if its value is negative', () => {
        const sampleAbsenceHistory = [
            {
                "id": 1,
                "comment": "2021-11-12 - 2021-11-12 (Narodziny dziecka)",
                "hours": -2.0,
                "userWorkTime": 8.0,
                "created": "2021-08-20 11:53:26",
                "hoursRemaining": 10.0,
                "workTimeNumerator": 1,
                "workTimeDenominator": 1,
                "deciders": []
            }
        ];
        render(<AbsenceHistoryTab logs={sampleAbsenceHistory} isHidden={isHidden}/>);
        const hours = screen.getByText("-2h");
        expect(hours).toHaveStyle({color: "red"});
    });

    test('should display hours in color orange if its value equals 0', () => {
        const sampleAbsenceHistory = [
            {
                "id": 1,
                "comment": "2021-11-12 - 2021-11-12 (Narodziny dziecka)",
                "hours": 0.0,
                "userWorkTime": 8.0,
                "created": "2021-08-20 11:53:26",
                "hoursRemaining": 10.0,
                "workTimeNumerator": 1,
                "workTimeDenominator": 1,
                "deciders": []
            }
        ];
        render(<AbsenceHistoryTab logs={sampleAbsenceHistory} isHidden={isHidden}/>);
        const hours = screen.getByText("0h");
        expect(hours).toHaveStyle({color: "orange"});
    });

    test("should display hours formatted to days if its absolute value > 23.0", () => {
        const sampleAbsenceHistory = [
            {
                "id": 1,
                "comment": "2021-11-12 - 2021-11-12 (Narodziny dziecka)",
                "hours": 26.0,
                "userWorkTime": 8.0,
                "created": "2021-08-20 11:53:26",
                "hoursRemaining": 10.0,
                "workTimeNumerator": 1,
                "workTimeDenominator": 1,
                "deciders": []
            }
        ];
        render(<AbsenceHistoryTab logs={sampleAbsenceHistory} isHidden={isHidden}/>);
        const formattedHours = screen.getByText("3d 2h");
        expect(formattedHours).toBeInTheDocument();
    });

    test("should not display hours formatted to days if its absolute value <= 23.0", () => {
        const sampleAbsenceHistory = [
            {
                "id": 1,
                "comment": "2021-11-12 - 2021-11-12 (Narodziny dziecka)",
                "hours": -6.0,
                "userWorkTime": 8.0,
                "created": "2021-08-20 11:53:26",
                "hoursRemaining": 10.0,
                "workTimeNumerator": 1,
                "workTimeDenominator": 1,
                "deciders": []
            }
        ];
        render(<AbsenceHistoryTab logs={sampleAbsenceHistory} isHidden={isHidden}/>);
        const notFormattedHours = screen.getByText("-6h");
        expect(notFormattedHours).toBeInTheDocument();
    });
