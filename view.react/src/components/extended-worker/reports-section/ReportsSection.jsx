import classNames from "classnames";
import PropTypes from "prop-types";
import {useState} from "react";
import {Button, ButtonGroup, Form} from "react-bootstrap";
import {BeatLoader} from "react-spinners";

import {useWorkers} from "../../../contexts/workers-context/workersContext";
import {btnClass} from "../../../global-styles/btn.module.scss";
import {formatReportFileName} from "../../../helpers/formatReportFileNameHelper";
import {getXlsxFromResponse} from "../../../helpers/RequestHelper";
import styles from './ReportsSection.module.scss';

const GET_XLSX_REPORT_URL = '/api/v2/reports/work-time-evidence/user/';

export const ReportsSection = ({availableYears}) => {
    const [reportYear, setReportYear] = useState(new Date().getFullYear().toString());
    const [isDownloading, setIsDownloading] = useState(false);

    const [workersState] = useWorkers();
    const {isEC} = workersState;
    const {userId, fullName} = isEC ? workersState.workers.selectedWorker : workersState.associates.selectedAssociate;

    const handleGenerateReport = async (e) => {
        e.currentTarget.blur();
        setIsDownloading(true);
        await getXlsxFromResponse(`${GET_XLSX_REPORT_URL}${userId}?year=${reportYear}`, formatReportFileName(fullName, reportYear))
            .then(() => {
                setIsDownloading(false);
            })
    }

    const handleChangeReportYear = e => {
        const {value} = e.target;
        setReportYear(value);
    }

    const generateReportBtnClass = classNames(btnClass, styles.generateReportBtn);
    return (
        <div className={styles.reportsSection}>
            <ButtonGroup>
                <Button className={generateReportBtnClass}
                        onClick={e => handleGenerateReport(e)}
                >
                    {
                        isDownloading ?
                            <>
                                <BeatLoader color='white' size={10}/>
                            </>
                            :
                            <>
                                Ewidencja czasu pracy
                            </>
                    }
                </Button>

                <Form.Select defaultValue={reportYear}
                             className={styles.yearSelection}
                             onChange={e => handleChangeReportYear(e)}
                             aria-label="Year select"
                             data-testid='selector'
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
