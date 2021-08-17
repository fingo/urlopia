import {CollapsableArea} from '../../components/collapsable-area/CollapsableArea';
import {CompanyRequestsList} from "../../components/company-requests-list/CompanyRequestsList";
import {CreateAbsenceRequestForm} from "../../components/create-absence-request-form/CreateAbsenceRequestForm";
import {TeamRequestsList} from "../../components/team-requests-list/TeamRequestsList";
import {UserRequestsList} from "../../components/user-requests-list/UserRequestsList";
import {RequestProvider} from "../../contexts/request-context/requestContext";
import styles from './AbsenceRequestsPage.module.scss';

export const URL = '/requests';

export const AbsenceRequestsPage = () => {
    return (
        <RequestProvider>
            <div className={styles.main}>
                <CollapsableArea title='Złóż wniosek'>
                    <CreateAbsenceRequestForm/>
                </CollapsableArea>

                <CollapsableArea title='Moje wnioski'>
                    <UserRequestsList/>
                </CollapsableArea>

                <CollapsableArea title='Wnioski zespołu do rozpatrzenia'>
                    <TeamRequestsList/>
                </CollapsableArea>

                <CollapsableArea title='Aktywne wnioski firmowe'>
                    <CompanyRequestsList/>
                </CollapsableArea>
            </div>
        </RequestProvider>
    );
};