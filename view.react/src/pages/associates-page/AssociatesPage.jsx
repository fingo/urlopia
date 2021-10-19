import {WorkersTable} from "../../components/workers-table/WorkersTable";

export const URL = '/associates';

export const AssociatesPage = () => {
    return (
        <WorkersTable isEC={false}/>
    );
};