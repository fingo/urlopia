import moment from "moment/moment";
import {v4 as uuidv4} from "uuid";

export const formatLogs = (logs) => {
    let formattedLogs = [];

    for (let i=0; i<logs.length; i++) {
        formattedLogs.push(logs[i]);

        if (logs[i+1]) {
            let firstLogWorkTime = logs[i].userWorkTime;
            let secondLogWorkTime = logs[i+1].userWorkTime;

            if(firstLogWorkTime !== secondLogWorkTime) {
                const newWorkTime = `Zmiana etatu: ${logs[i].workTimeNumerator}/${logs[i].workTimeDenominator}`;
                const created = calculateWorkTimeLogCreationDateTime(logs[i+1].created)
                const workTimeChangeLog = createWorkTimeChangeLog(newWorkTime, created);
                formattedLogs.push(workTimeChangeLog);
            }
        }
    }
    const len = logs.length;
    if (len !== 0) {
        const startingWorkTime = `Etat: ${logs[len-1].workTimeNumerator}/${logs[len-1].workTimeDenominator}`
        const startingWorkTimeLog = createWorkTimeChangeLog(startingWorkTime);
        formattedLogs = [...formattedLogs, startingWorkTimeLog];
    }

    return formattedLogs;
}

const calculateWorkTimeLogCreationDateTime = (prevHistoryLogCreationDate) => {
    const date = moment(prevHistoryLogCreationDate).add(1, "seconds")
    return date.format("YYYY-MM-DD HH:mm:ss")
}

const createWorkTimeChangeLog = (changes, created) => {
    return {
        id: uuidv4(),
        hours: changes,
        created: created
    }
}