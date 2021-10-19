import PropTypes from 'prop-types';
import {useState} from 'react';
import {Button, Modal} from "react-bootstrap";

import {getPdfFromResponse} from "../../../helpers/RequestHelper";
import {MonthPicker} from '../MonthPicker/MonthPicker';
import styles from '../Reports.module.scss';

export const PresenceListModal = ({show, onHide}) => {

    const GET_PDF_LIST_URL = "/api/v2/reports/monthly-presence";

    const today = new Date();
    const currentYear = today.getFullYear();
    const currentMonth = today.getMonth();

    const range = {
        min: { year: 2020, month: 7},
        max: { year: currentYear, month: currentMonth + 1}
    };

    const [isLoading, setIsLoading] = useState(false);
    const [chosenMonth, setChosenMonth] = useState({year: currentYear, month: currentMonth + 1});

    const handleGeneratingPdf = async () => {
        setIsLoading(true);
        await getPdfFromResponse(`${GET_PDF_LIST_URL}/${chosenMonth.year}/${chosenMonth.month}`,
            `ListaObecnosci_${chosenMonth.month}-${chosenMonth.year}`)
            .then(()=>{
                setIsLoading(false);
            })
            .catch(error => error);
        onHide();
    }

    return (
        <Modal
            onHide={onHide}
            show={show}
            size="sm"
            aria-labelledby="contained-modal-title-vcenter"
            data-testid={"presenceModal"}
        >
            <Modal.Header closeButton data-testid={'presenceModalHeader'}>
                <Modal.Title id={"contained-modal-title-vcenter"}>
                    Lista obecności
                </Modal.Title>
            </Modal.Header>
            <Modal.Body>
                    <label>
                        Miesiąc
                    </label>
                    <MonthPicker
                        range={range}
                        chosenMonth={chosenMonth}
                        setChosenMonth={setChosenMonth}
                        data-testid={'monthPicker'}
                    />
            </Modal.Body>
            <Modal.Footer>
                <Button
                    className={styles.btnClass}
                    onClick={() => handleGeneratingPdf()}
                    data-testid={"generateButton"}
                >
                    {isLoading ? "..." : 'Generuj'}
                </Button>
            </Modal.Footer>
        </Modal>
    );
}

PresenceListModal.propTypes = {
    show: PropTypes.bool.isRequired,
    onHide: PropTypes.func.isRequired
}