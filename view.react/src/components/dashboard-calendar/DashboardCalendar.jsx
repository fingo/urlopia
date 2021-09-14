import {faUmbrellaBeach} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {format, lastDayOfMonth, startOfMonth} from "date-fns";
import pl from "date-fns/locale/pl";
import {useEffect, useState} from "react";
import {Form} from "react-bootstrap";
import {ExclamationTriangleFill as ExclamationIcon} from "react-bootstrap-icons";
import Calendar from "react-date-range/dist/components/Calendar";
import Select from "react-select";

import {getCurrentUser} from "../../api/services/session.service";
import {usePresence} from "../../contexts/presence-context/presenceContext";
import {formatDate} from "../../helpers/DateFormatterHelper";
import {filterAbsentUsers} from "../../helpers/FilterAbsentUsersBySelectedTeamsHelper";
import {sendGetRequest} from "../../helpers/RequestHelper";
import {sortedTeams} from "../../helpers/sorts/TeamSortHelper"
import {sortedUsers} from "../../helpers/sorts/UsersSortHelper";
import {CalendarDayInfo} from "./calendar-day-info/CalendarDayInfo";
import styles from "./DashboardCalendar.module.scss";

const ENDPOINT_PREFIX_URL = '/api/v2';

const saveSelectedTeamsFilter = selectedTeams => {
    localStorage.setItem("dashboard.selectedTeams", JSON.stringify(selectedTeams))
}

const getSelectedTeamsFilter = () => {
    return JSON.parse(localStorage.getItem("dashboard.selectedTeams")) || []
}

const saveSelectedUsersFilter = selectedUsers => {
    localStorage.setItem("dashboard.selectedUsers", JSON.stringify(selectedUsers))
}

const getSelectedUsersFilter = () => {
    return JSON.parse(localStorage.getItem("dashboard.selectedUsers")) || []
}

export const DashboardCalendar = () => {
    const [presenceState] = usePresence();

    const [selectedDate, setSelectedDate] = useState(null);
    const [show, setShow] = useState(false);
    const [currentMonth, setCurrentMonth] = useState(startOfMonth(new Date()));

    const [teamsOptions, setTeamsOptions] = useState([]);
    const [selectedTeams, setSelectedTeams] = useState(() => getSelectedTeamsFilter());

    const [usersOptions, setUsersOptions] = useState([]);
    const [selectedUsers, setSelectedUsers] = useState(() => getSelectedUsersFilter());

    const [calendarResponse, setCalendarResponse] = useState(null);

    useEffect(() => {
        sendGetRequest(`${ENDPOINT_PREFIX_URL}/users`)
            .then(users => setUsersOptions(formatUsers(users)))
            .catch(error => error);
        sendGetRequest(`${ENDPOINT_PREFIX_URL}/teams`)
            .then(teams => setTeamsOptions(formatTeams(teams)))
            .catch(error => error);
    }, []);

    useEffect(() => {
        const formattedFirstDayOfCurrentMonth = formatDate(currentMonth);
        const formattedLastDayOfCurrentMonth = formatDate(lastDayOfMonth(currentMonth));
        sendGetRequest(`${ENDPOINT_PREFIX_URL}/calendar?startDate=${formattedFirstDayOfCurrentMonth}&endDate=${formattedLastDayOfCurrentMonth}`)
            .then(data => {
                setCalendarResponse(data.calendar);
            }).catch(error => error);
    }, [currentMonth]);

    const handleDateChange = (item) => {
        setSelectedDate(item);
        setShow(true);
    };

    const shouldBeDisabled = day => {
        return calendarResponse && calendarResponse[formatDate(day)] && !calendarResponse[formatDate(day)].workingDay;
    };

    const customDayContent = (day) => {
        if (calendarResponse) {
            if (!calendarResponse[formatDate(day)] || !calendarResponse[formatDate(day)].workingDay) {
                return <span className={styles.mainNumber}>{format(day, "d")}</span>;
            }

            const thisDay = calendarResponse[formatDate(day)];
            let extra = null;

            if (thisDay?.currentUserInformation.absent) {
                extra = <FontAwesomeIcon icon={faUmbrellaBeach} className={styles.absenceIcon}/>;
            } else if (getCurrentUser().ec
                && !presenceState.myConfirmations.confirmations[formatDate(day)]
                && day.getTime() <= new Date().getTime()
                && !thisDay?.currentUserInformation.presenceConfirmation.confirmed) {
                extra = <ExclamationIcon className={styles.exclamationIcon}/>;
            }

            let absentUsersCounter = thisDay?.absentUsers.length;
            if (selectedTeams.length || selectedUsers.length) {
                absentUsersCounter = filterAbsentUsers(thisDay?.absentUsers, selectedUsers, selectedTeams).length;
            }

            return (
                <>
                    {extra}
                    <span className={styles.mainNumber}>{format(day, "d")}</span>
                    <span className={styles.absentUsersLabel}>
                        <strong>{absentUsersCounter === 0 ? `-` : `${absentUsersCounter} NB`}</strong>
                    </span>
                </>
            )
        }
    }

    const filteredAbsentUsers = (absentUsers) => {
        if (selectedTeams.length || selectedUsers.length) {
            return filterAbsentUsers(absentUsers, selectedUsers, selectedTeams);
        }
        return absentUsers;
    }

    const formatUsers = (users) => {
        const formattedUsers = users.map(user => {
            return {
                value: user.fullName,
                label: user.fullName,
            }
        });
        return sortedUsers(formattedUsers)
    }

    const formatTeams = (teams) => {
        const formattedTeams = teams.map(team => {
            return {
                value: team.teamName,
                label: team.teamName,
            }
        }).concat({value: 'noTeams', label: '- brak zespołów -'});
        return sortedTeams(formattedTeams);

    }

    return (
        <>
            <div className={styles.filterSection}>
                <div className={styles.filter}>
                    <Form.Label className={styles.label}>Pracownicy:</Form.Label>
                    <Select
                        className={styles.selection}
                        placeholder='Wszyscy pracownicy...'
                        isMulti
                        name="users"
                        options={usersOptions}
                        value={selectedUsers}
                        onChange={(items) => {
                            setSelectedUsers(items)
                            saveSelectedUsersFilter(items)
                        }}
                        noOptionsMessage={() => 'Brak użytkowników do wyboru!'}
                    />
                </div>

                <div className={styles.filter}>
                    <Form.Label className={styles.label}>Zespoły:</Form.Label>
                    <Select
                        className={styles.selection}
                        placeholder='Wszystkie zespoły...'
                        isMulti
                        name="teams"
                        options={teamsOptions}
                        value={selectedTeams}
                        onChange={(items) => {
                            setSelectedTeams(items)
                            saveSelectedTeamsFilter(items)
                        }}
                        noOptionsMessage={() => 'Brak zespołów do wyboru!'}
                    />
                </div>
            </div>

            <div className={styles.calendarWrapper}>
                <Calendar
                    locale={pl}
                    onChange={(item) => handleDateChange(item)}
                    date={selectedDate}
                    className={styles.calendar}
                    dayContentRenderer={customDayContent}
                    color='deepskyblue'
                    showPreview={false}
                    showSelectionPreview={false}
                    disabledDay={day => shouldBeDisabled(day)}
                    onShownDateChange={item => setCurrentMonth(item)}
                />
            </div>

            {
                show &&
                <CalendarDayInfo
                    show={show}
                    onHide={() => setShow(false)}
                    date={formatDate(selectedDate)}
                    absentUsers={filteredAbsentUsers(calendarResponse[formatDate(selectedDate)].absentUsers)}
                />
            }
        </>
    );
};
