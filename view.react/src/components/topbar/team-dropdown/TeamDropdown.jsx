import PropTypes from 'prop-types';
import {Dropdown} from 'react-bootstrap';
import {PersonCircle} from "react-bootstrap-icons";
import {useState, useEffect} from "react";

import styles from './TeamDropdown.module.scss';
import {useVacationDays} from "../../../contexts/vacation-days-context/vacationDaysContext";
import {fetchPendingDays} from "../../../contexts/vacation-days-context/actions/fetchPendingDays";
import {fetchVacationDays} from "../../../contexts/vacation-days-context/actions/fetchVacationDays";
import {formatHoursToDays} from "../../../helpers/RemainingDaysFormatterHelper";

export const TeamDropdown = ({
    userName,
    teams,
}) => {

  const [vacationDays, setVacationDays] = useState(0);
  const [vacationHours, setVacationHours] = useState(0);
  const [pendingDays, setPendingDays] = useState(0);
  const [pendingHours, setPendingHours] = useState(0);
  const [workTime, setWorkTime] = useState(8);

  const [vacationDaysState, vacationDaysDispatch] = useVacationDays();

  useEffect(() => {
    fetchPendingDays(vacationDaysDispatch);
  }, [vacationDaysDispatch]);

  useEffect(() => {
    fetchVacationDays(vacationDaysDispatch);
  }, [vacationDaysDispatch]);

  useEffect(() => {
    const {pendingDays, pendingHours} = vacationDaysState.pendingDays;
    setPendingDays(pendingDays);
    setPendingHours(pendingHours);
  }, [vacationDaysState.pendingDays]);

  useEffect(() => {
    const {remainingDays, remainingHours, workTime} = vacationDaysState.vacationDays;
    setVacationDays(remainingDays);
    setVacationHours(remainingHours);
    setWorkTime(workTime);
  }, [vacationDaysState.vacationDays]);

  const remainingDays = vacationDays - pendingDays
  const remainingHours = vacationHours - pendingHours
  const remainingHoursAsDays = formatHoursToDays(remainingHours/workTime)

  return (
        <Dropdown align="end">
            <Dropdown.Toggle className={styles.dropdown} bsPrefix="p-0" variant="link">
                <span>
                    <PersonCircle className={styles.icon}/>
                </span>
                <p className="d-none d-lg-flex">
                  {userName}
                  ({workTime === 8 ? <span><strong>{remainingDays}d</strong> {remainingHours}h</span> :
                    <strong>{remainingHours}h ({remainingHoursAsDays}d)</strong>})
                </p>
            </Dropdown.Toggle>

            <Dropdown.Menu>
                <Dropdown.Header className="d-lg-none text-center">{userName}</Dropdown.Header>
                <Dropdown.Divider className="m-0 d-lg-none"/>

                {
                    teams?.length ?
                        teams.map(({name, leader}, i) =>
                            <div key={i}
                                 className={i === teams.length - 1 ? styles.teamInfo : styles.teamInfoWithDivider}
                            >
                                <p className={styles.team}>{name}</p>
                                <p className={styles.leader}>Lider: {leader}</p>
                            </div>
                        )

                        :

                        <Dropdown.Header className="text-center">Brak zespołów</Dropdown.Header>
                }
            </Dropdown.Menu>
        </Dropdown>
    );
}

TeamDropdown.propTypes = {
    userName: PropTypes.string.isRequired,
    teams: PropTypes.arrayOf(
        PropTypes.shape({
            name: PropTypes.string,
            leader: PropTypes.string,
        })).isRequired,
}
