import PropTypes from "prop-types";
import {useState} from "react";
import {Button, ButtonGroup, Form} from "react-bootstrap";

import {useWorkers} from "../../../contexts/workers-context/workersContext";
import {btnClass} from "../../../global-styles/btn.module.scss";
import {formatReportFileName} from "../../../helpers/formatReportFileNameHelper";
import {getXlsxFromResponse} from "../../../helpers/RequestHelper";
import styles from './ReportsSection.module.scss';

const GET_XLSX_REPORT_URL = '/api/v2/reports/work-time-evidence/user/';

export const ReportsSection = ({availableYears}) => {
    const [reportYear, setReportYear] = useState(new Date().getFullYear().toString());

    const [workersState] = useWorkers();
    const {userId, fullName} = workersState.selectedUser;

    const handleGenerateReport = e => {
        e.currentTarget.blur();
        getXlsxFromResponse(`${GET_XLSX_REPORT_URL}${userId}?year=${reportYear}`, formatReportFileName(fullName, reportYear));
    }

    const handleChangeReportYear = e => {
        const {value} = e.target;
        setReportYear(value);
    }

    return (
        <div className={styles.reportsSection}>
            <ButtonGroup>
                <Button className={btnClass}
                        onClick={e => handleGenerateReport(e)}
                >
                    Generuj raport
                </Button>

                <Form.Select defaultValue={reportYear}
                             className={styles.yearSelection}
                             onChange={e => handleChangeReportYear(e)}
                             aria-label="Year select"
                >
                    {
                        availableYears.map(year => {
                            return <option key={year} value={year}>{year}</option>
                        })
                    }
                </Form.Select>
            </ButtonGroup>
        </div>
    );
};

ReportsSection.propTypes = {
    availableYears: PropTypes.arrayOf(PropTypes.number),
}

ReportsSection.defaultProps = {
    availableYears: [],
}
