import {CollapsableArea} from '../../components/collapsable-area/CollapsableArea';
import {CompanyRequestsList} from "../../components/company-requests-list/CompanyRequestsList";
import {CreateAbsenceRequestForm} from "../../components/create-absence-request-form/CreateAbsenceRequestForm";
import {TeamRequestsList} from "../../components/team-requests-list/TeamRequestsList";
import {UserRequestsList} from "../../components/user-requests-list/UserRequestsList";
import {ACCEPTED,CANCELED, PENDING} from "../../constants/statuses";
import styles from './AbsenceRequestsPage.module.scss';

export const URL = '/requests';

export const AbsenceRequestsPage = () => {
    return (
        <div className={styles.main}>
            <CollapsableArea title='Złóż wniosek'>
                <CreateAbsenceRequestForm/>
            </CollapsableArea>

            <CollapsableArea title='Moje wnioski'>
                <UserRequestsList requests={productsForUserRequestsList} />
            </CollapsableArea>

            <CollapsableArea title='Wnioski zespołu do rozpatrzenia'>
                <TeamRequestsList/>
            </CollapsableArea>

            <CollapsableArea title='Aktywne wnioski firmowe'>
                <CompanyRequestsList/>
            </CollapsableArea>
        </div>
    );
};

const productsForUserRequestsList = [
    {
        id: 1,
        period: '2021-07-20 - 2021-07-27 (7 dni robocze)',
        type: 'Wypoczynkowy',
        status: PENDING,
        actions: ''
    },
    {
        id: 2,
        period: '2021-07-30 - 2021-07-30 (1 dni robocze)',
        type: 'Opieka nad dzieckiem',
        status: ACCEPTED,
        actions: ''
    },
    {
        id: 3,
        period: '2021-08-22 - 2021-08-22 (1 dni robocze)',
        type: 'Ślub',
        status: CANCELED,
        actions: ''
    }
];