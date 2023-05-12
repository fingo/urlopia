import {faUmbrellaBeach} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {format, lastDayOfMonth, startOfMonth} from "date-fns";
import pl from "date-fns/locale/pl";
import {useEffect, useState} from "react";
import {ExclamationTriangleFill as ExclamationIcon} from "react-bootstrap-icons";
import Calendar from "react-date-range/dist/components/Calendar";
import Select from "react-select";

import useGetCalendarQuery from "../../api/queryHooks/queries/Calendar/useGetCalendarQuery";
import {getCurrentUser} from "../../api/services/session.service";
import {usePresence} from "../../contexts/presence-context/presenceContext";
import {formatDate} from "../../helpers/DateFormatterHelper";
import {filterAbsentUsers} from "../../helpers/FilterAbsentUsersBySelectedTeamsHelper";
import {sendGetRequest} from "../../helpers/RequestHelper";
import {sortedTeams} from "../../helpers/sorts/TeamSortHelper"
import {sortedUsers} from "../../helpers/sorts/UsersSortHelper";
import {CalendarDayInfo} from "./calendar-day-info/CalendarDayInfo";
import styles from "./DashboardCalendar.module.scss";

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

    const firstDay = formatDate(currentMonth);
    const lastDayNextMonth = formatDate(lastDayOfMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 1)));

    const { data, isFetched } = useGetCalendarQuery({startDate: firstDay, endDate: lastDayNextMonth})

    const calendarResponse = data?.calendar;

    useEffect(() => {
        sendGetRequest(`/users?filter=active:true`)
            .then(users => setUsersOptions(formatUsers(users)))
            .catch(error => error);
        sendGetRequest(`/teams`)
            .then(teams => setTeamsOptions(formatTeams(teams)))
            .catch(error => error);
    }, []);

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
                        <strong>{absentUsersCounter === 0 ? `-` : `${absentUsersCounter} nb`}</strong>
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
        return sortedUsers(formattedUsers, "label")
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

    const customStyles = {
        control: (defaultStyles) => ({
            ...defaultStyles,
            backgroundColor: "#F2EFEA",
            border: "none",
            borderBottom: "1px solid #002900",
            boxShadow: "none",
            borderRadius: "0",
            color: "#002900",
            padding: "13px 0 11px 16px",
            "&:hover": {
                borderBottom: "1px solid #78A612",
                backgroundColor: "#FFF",
                transition: "all 0.6s ease-in-out",
            }
        }),
        valueContainer: (provided, state) => ({
            ...provided,
            padding: '0',
            height: "20px"
        }),
        input: (provided, state) => ({
            ...provided,
            margin: '0px',
            padding: '0px'
        }),
        indicatorsContainer: (provided, state) => ({
            ...provided,
            height: '20px',
        }),
        dropdownIndicator: base => ({
            ...base,
            color: "#002900",
            "&:hover": {
                "pointer-events": "none",

            }
        }),
        placeholder: (defaultStyles) => ({
            ...defaultStyles,
            color: "#002900",
            fontSize: "14px",
            letterSpacing: "0.2px",
            margin: "0"
        }),
    };

    return (
        <>
            <div className={styles.filterSection}>
                <div className={styles.filter}>
                    <Select
                        className={styles.selection}
                        placeholder='PRACOWNICY'
                        isMulti
                        name="users"
                        options={usersOptions}
                        value={selectedUsers}
                        onChange={(items) => {
                            setSelectedUsers(items)
                            saveSelectedUsersFilter(items)
                        }}
                        noOptionsMessage={() => 'Brak użytkowników do wyboru!'}
                        styles={customStyles}
                    />
                </div>

                <div className={styles.filter}>
                    <Select
                        className={styles.selection}
                        placeholder='ZESPOŁY'
                        isMulti
                        name="teams"
                        options={teamsOptions}
                        value={selectedTeams}
                        onChange={(items) => {
                            setSelectedTeams(items)
                            saveSelectedTeamsFilter(items)
                        }}
                        noOptionsMessage={() => 'Brak zespołów do wyboru!'}
                        styles={customStyles}
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
                    color="transparent"
                    showPreview={false}
                    showSelectionPreview={false}
                    disabledDay={day => !isFetched || shouldBeDisabled(day)}
                    onShownDateChange={item => setCurrentMonth(item)}
                    months={2}
                    direction="horizontal"
                    showMonthAndYearPickers={false}
                    monthDisplayFormat="LLLL yyyy"
                    weekdayDisplayFormat="EEEEEE"
                />
            </div>

            {
                show &&
                <CalendarDayInfo
                    show={show}
                    onHide={() => setShow(false)}
                    date={formatDate(selectedDate)}
                    absentUsers={sortedUsers(filteredAbsentUsers(calendarResponse[formatDate(selectedDate)].absentUsers),
                        "userName")}
                />
            }
        </>
    );
};
