import {faUmbrellaBeach} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {addMonths, format, lastDayOfMonth, startOfMonth} from "date-fns";
import pl from "date-fns/locale/pl";
import {useEffect, useState} from "react";
import {ExclamationTriangleFill as ExclamationIcon} from "react-bootstrap-icons";
import Calendar from "react-date-range/dist/components/Calendar";
import Select from "react-select";

import useGetCalendarQuery from "../../api/queryHooks/queries/Calendar/useGetCalendarQuery";
import {getCurrentUser} from "../../api/services/session.service";
import {usePresence} from "../../contexts/presence-context/presenceContext";
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

    const firstDay = format(currentMonth, 'yyyy-MM-dd');
    const lastDayNextMonth = format(lastDayOfMonth(addMonths(currentMonth, 1)), 'yyyy-MM-dd');

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
        return calendarResponse && calendarResponse[format(day, 'yyyy-MM-dd')] && !calendarResponse[format(day, 'yyyy-MM-dd')].workingDay;
    };

    const customDayContent = (day) => {
        if (calendarResponse) {
            if (!calendarResponse[format(day, 'yyyy-MM-dd')] || !calendarResponse[format(day, 'yyyy-MM-dd')].workingDay) {
                return <span className={styles.mainNumber}>{format(day, "d")}</span>;
            }

            const thisDay = calendarResponse[format(day, 'yyyy-MM-dd')];
            let extra = null;

            if (thisDay?.currentUserInformation.absent) {
                extra = <FontAwesomeIcon icon={faUmbrellaBeach} className={styles.absenceIcon}/>;
            } else if (getCurrentUser().ec
                && !presenceState.myConfirmations.confirmations[format(day, 'yyyy-MM-dd')]
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
        <div className="d-flex flex-column">
            <div className={`${styles.filterSection} d-flex flex-row justify-content-center`}>
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

            <div className={` ${styles.calendarWrapper} d-flex flex-row justify-content-center`}>
                <Calendar
                    locale={pl}
                    onChange={(item) => handleDateChange(item)}
                    date={currentMonth}
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
                    date={format(selectedDate, 'yyyy-MM-dd')}
                    absentUsers={sortedUsers(filteredAbsentUsers(calendarResponse[format(selectedDate, 'yyyy-MM-dd')].absentUsers),
                        "userName")}
                />
            }
        </div>
    );
};
