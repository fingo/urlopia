import { Checkbox, FormControlLabel } from '@mui/material';
import PropTypes from "prop-types";
import {useEffect, useState} from 'react';
import {Button, Modal} from "react-bootstrap";
import Select from 'react-select';

import {formatReportFileName} from "../../../helpers/formatReportFileNameHelper";
import {getXlsxFromResponse, getZipFromResponse, sendGetRequest} from "../../../helpers/RequestHelper";
import {sortedUsers} from "../../../helpers/sorts/UsersSortHelper";
import styles from '../Reports.module.scss';


export const EvidenceReportModal = ({show, onHide}) => {

    const ALL_USERS_URL = "/api/v2/users";
    const GET_EVIDENCE_REPORT_URL = '/api/v2/reports/work-time-evidence';
    const currentYear = (new Date()).getFullYear();
    const availableYears = [currentYear, currentYear-1, currentYear-2, currentYear-3, currentYear-4];

    const [isLoading, setIsLoading] = useState(false);
    const [isChecked, setIsChecked] = useState(false);

    const [allUsers, setAllUsers] = useState([]);
    const [chosenYear, setChosenYear] = useState(null);
    const [chosenPerson, setChosenPerson] = useState(null);

    const handleModalHide = () => {
        setChosenPerson(null);
        setChosenYear(null);
        setIsLoading(false);
        onHide();
    }

    useEffect(() => {
        sendGetRequest(`${ALL_USERS_URL}`)
            .then(users => {
                setAllUsers(users)
            })
            .catch(error => error);
    }, [])

    const getModifiedYears = (yearsToModify) => {
        return yearsToModify.map(year => ({value: year, label: `${year}`}));
    }

    const getFormattedUsers = (users) => {
        const formattedUsers = users.map(user => ({value: user.userId, label: user.fullName}));
        return sortedUsers(formattedUsers, "label")
    }

    const handleChecking = () => {
        setChosenPerson(null);
        setIsChecked(!isChecked);
    }

    const handleAllUsersGenerating = async () => {
        setIsLoading(true);
        await getZipFromResponse(`${GET_EVIDENCE_REPORT_URL}?year=${chosenYear.value}&filter=active:true`,
            `EwidencjaCzasuPracy_${chosenYear.value}`)
            .then(() => {
                setIsLoading(false);
                handleModalHide();
            })
            .catch(error => error);
    }

    const handleSingleGenerating = async () => {
        setIsLoading(true);
        await getXlsxFromResponse(`${GET_EVIDENCE_REPORT_URL}/user/${chosenPerson.value}?year=${chosenYear.value}`,
            formatReportFileName(chosenPerson.label, chosenYear.value))
            .then(() => {
                setIsLoading(false);
            })
            .catch(error => error);
    }

    return (
        <Modal
            onHide={handleModalHide}
            show={show}
            size="sm"
            aria-labelledby="contained-modal-title-vcenter"
            data-testid={"evidenceModal"}
        >
            <Modal.Header closeButton>
                <Modal.Title id={"contained-modal-title-vcenter"}>
                    Ewidencja czasu pracy
                </Modal.Title>
            </Modal.Header>
            <Modal.Body data-testid={"evidenceModalBody"}>
                <div className={styles.inputContainer}>
                    <label>Rok</label>
                    <Select
                        options={getModifiedYears(availableYears)}
                        placeholder={"Wybierz rok..."}
                        onChange={(year) => setChosenYear(year)}
                        noOptionsMessage={() => 'Nie ma takiego roku!'}
                    />
                    <br/>
                    <label>Osoba</label>
                    <Select
                        value={chosenPerson}
                        isDisabled={isChecked}
                        options={getFormattedUsers(allUsers)}
                        placeholder={"Wybierz osobę..."}
                        onChange={(person) => setChosenPerson(person)}
                        noOptionsMessage={() => 'Nie ma takiej osoby!'}
                    />
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={isChecked}
                                data-testid={"checkbox"}
                                onChange={() => handleChecking()}
                                name="checkedB"
                                color="primary"
                            />
                        }
                        label="Wygeneruj dla wszystkich"
                    />
                </div>
            </Modal.Body>
            <Modal.Footer data-testid={"evidenceModalFooter"}>
                <Button
                    className={styles.btnClass}
                    disabled={(chosenPerson === null || chosenYear === null) && (!isChecked || chosenYear === null)}
                    onClick={isChecked ? () => handleAllUsersGenerating() : () => handleSingleGenerating()}
                >
                    {isLoading ? "..." : "Generuj"}
                </Button>
            </Modal.Footer>
        </Modal>
    );
}

EvidenceReportModal.propTypes = {
    show: PropTypes.bool.isRequired,
    onHide: PropTypes.func.isRequired,
}