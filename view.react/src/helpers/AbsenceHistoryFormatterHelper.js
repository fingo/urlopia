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
                const workTimeChangeLog = createWorkTimeChangeLog(newWorkTime);
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

const createWorkTimeChangeLog = (changes) => {
    return {
        id: uuidv4(),
        hours: changes
    }
}