import {useState} from "react";

import {CollapsableArea} from '../../components/collapsable-area/CollapsableArea';
import {CompanyRequestsList} from "../../components/company-requests-list/CompanyRequestsList";
import {CreateAbsenceRequestFormWrapper} from "../../components/create-absence-request-form/CreateAbsenceRequestFormWrapper";
import {TeamRequestsList} from "../../components/team-requests-list/TeamRequestsList";
import {UserRequestsList} from "../../components/user-requests-list/UserRequestsList";
import {HolidaysProvider} from "../../contexts/holidays-context/holidaysContext";
import {AttentionIcon, TextWithIcon} from "../../helpers/icons/Icons";
import styles from './AbsenceRequestsPage.module.scss';

export const URL = '/requests';

export const AbsenceRequestsPage = ({newAcceptancesPresent, setNewAcceptancesPresent}) => {
    const [shouldFetchHolidays, setShouldFetchHolidays] = useState(false)
    const [shouldFetchUserRequests, setShouldFetchUserRequests] = useState(false)
    const [shouldFetchCompanyRequests, setShouldFetchCompanyRequests] = useState(false)

    const teamRequestsListTitle = <TextWithIcon
        text={"Wnioski zespołu do rozpatrzenia"}
        icon={<AttentionIcon/>}
        showIcon={newAcceptancesPresent}
    />

    const handleAbsenceRequestFormWrapperOpen = () => setShouldFetchHolidays(true)
    const handleUserRequestsListOpen = () => setShouldFetchUserRequests(true)
    const handleTeamRequestsListOpen = () => setNewAcceptancesPresent(false)
    const handleCompanyRequestsListOpen = () => setShouldFetchCompanyRequests(true)

    return (
        <div className={styles.main}>
            <CollapsableArea title='Złóż wniosek' onOpen={handleAbsenceRequestFormWrapperOpen}>
                <HolidaysProvider>
                    <CreateAbsenceRequestFormWrapper shouldFetchHolidays={shouldFetchHolidays}/>
                </HolidaysProvider>
            </CollapsableArea>

            <CollapsableArea title='Moje wnioski' onOpen={handleUserRequestsListOpen}>
                <UserRequestsList shouldFetchUserRequests={shouldFetchUserRequests}/>
            </CollapsableArea>

            <CollapsableArea title={teamRequestsListTitle} onOpen={handleTeamRequestsListOpen}>
                <TeamRequestsList/>
            </CollapsableArea>

            <CollapsableArea title='Aktywne wnioski firmowe' onOpen={handleCompanyRequestsListOpen}>
                <CompanyRequestsList shouldFetchCompanyRequests={shouldFetchCompanyRequests}/>
            </CollapsableArea>
        </div>
    );
};