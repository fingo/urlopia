import {getCurrentUser} from "../../api/services/session.service";
import {DashboardCalendar} from "../../components/dashboard-calendar/DashboardCalendar";
import {PresenceConfirmationPanel} from "../../components/presence-confirmation-panel/PresenceConfirmationPanel";

export const URL = '/calendar';

export const CalendarPage = () => {
    return (
        <>
            {getCurrentUser().ec && <PresenceConfirmationPanel/>}
            <DashboardCalendar />
        </>
    );
};