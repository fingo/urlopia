import {useState} from "react";

import {getCurrentUser} from "../../api/services/session.service";
import {CollapsableArea} from '../../components/collapsable-area/CollapsableArea';
import {CompanyRequestsListWrapper} from "../../components/company-requests-list/company-requests-list-wrapper/CompanyRequestsListWrapper";
import {CreateAbsenceRequestFormWrapper} from "../../components/create-absence-request-form/create-absence-request-form-wrapper/CreateAbsenceRequestFormWrapper";
import {TeamRequestsListWrapper} from "../../components/team-requests-list/team-requests-list-wrapper/TeamRequestsListWrapper";
import {UserRequestsListWrapper} from "../../components/user-requests-list/user-requests-list-wrapper/UserRequestsListWrapper";
import {AttentionIcon, TextWithIcon} from "../../helpers/icons/Icons";
import styles from './AbsenceRequestsPage.module.scss';

export const URL = '/requests';

export const AbsenceRequestsPage = ({newAcceptancesPresent, setNewAcceptancesPresent}) => {
    const {isAdmin: isUserAnAdmin, isLeader: isUserALeader} = getCurrentUser()
    const [shouldFetchHolidays, setShouldFetchHolidays] = useState(false)
    const [shouldFetchUserRequests, setShouldFetchUserRequests] = useState(true)
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

                <CreateAbsenceRequestFormWrapper shouldFetchHolidays={shouldFetchHolidays}/>

            </CollapsableArea>

            <CollapsableArea title='Moje wnioski' onOpen={handleUserRequestsListOpen} shouldBeCollapsed={false}>
                <UserRequestsListWrapper shouldFetchUserRequests={shouldFetchUserRequests}/>
            </CollapsableArea>

            {isUserALeader && (
                <CollapsableArea title={teamRequestsListTitle} onOpen={handleTeamRequestsListOpen}>
                    <TeamRequestsListWrapper/>
                </CollapsableArea>
            )}

            {isUserAnAdmin && (
                <CollapsableArea title='Aktywne wnioski firmowe' onOpen={handleCompanyRequestsListOpen}>
                    <CompanyRequestsListWrapper shouldFetchCompanyRequests={shouldFetchCompanyRequests}/>
                </CollapsableArea>
            )}
        </div>
    );
};