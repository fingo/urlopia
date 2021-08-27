import {PresenceConfirmationPanel} from "../../components/presence-confirmation-panel/PresenceConfirmationPanel";
import {PresenceProvider} from "../../contexts/presence-context/presenceContext";

export const URL = '/calendar';

export const CalendarPage = () => {
    return (
        <PresenceProvider>
            <PresenceConfirmationPanel/>
        </PresenceProvider>
    );
};