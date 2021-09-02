export const WORKERS_ENDPOINT = '/api/v2/users';
const WORKERS_ACTION_PREFIX = 'workers';

const REMAINING_DAYS_ACTION_PREFIX = 'remaining-days';

export const FETCH_WORKERS_ACTION_PREFIX = `${WORKERS_ACTION_PREFIX}/fetch/workers`;

export const FETCH_REMAINING_DAYS_URL_POSTFIX = `/vacation-days`;
export const FETCH_REMAINING_DAYS_ACTION_PREFIX = `${REMAINING_DAYS_ACTION_PREFIX}/fetch/user`;

export const CHANGE_WORK_TIME_URL_POSTFIX = '/work-time';
export const CHANGE_WORK_TIME_ACTION_PREFIX = `${WORKERS_ACTION_PREFIX}/change/work-time`;

export const CHANGE_SELECTED_USER_ACTION_PREFIX = `${WORKERS_ACTION_PREFIX}/change/selected-user`;

export const CHANGE_REMAINING_DAYS_ACTION_PREFIX = `${WORKERS_ACTION_PREFIX}/change/remaining-days`;
