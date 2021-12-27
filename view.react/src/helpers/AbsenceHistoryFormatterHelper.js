import moment from "moment/moment";
import {v4 as uuidv4} from "uuid";

export const formatLogs = (logs) => {
    let formattedLogs = [];

    for (let i = 0; i < logs.length - 1; i++) {
        formattedLogs.push(logs[i]);

        let prevWorkTime = logs[i].userWorkTime;
        let nextWorkTime = logs[i + 1].userWorkTime;

        if (prevWorkTime !== nextWorkTime) {
            const newWorkTime = `Zmiana etatu: ${workTimeChangeTextRepresentationFor(logs[i], logs[i + 1])}`;
            const created = calculateWorkTimeLogCreationDateTime(logs[i + 1].created);
            const workTimeChangeLog = createWorkTimeChangeLog(newWorkTime, created);

            formattedLogs.push(workTimeChangeLog);
        }
    }

    const len = logs.length;
    if (len > 0) {
        formattedLogs.push(logs[len - 1]);
    }

    return formattedLogs;
}

const workTimeChangeTextRepresentationFor = (prevLog, nextLog) => {
    return `${workTimeTextRepresentationFor(prevLog)} -> ${workTimeTextRepresentationFor(nextLog)}`
}

const workTimeTextRepresentationFor = (log) => {
    const {workTimeNumerator, workTimeDenominator} = log;
    return `${workTimeNumerator}/${workTimeDenominator}`;
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