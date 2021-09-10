export const WORKERS_ENDPOINT = '/api/v2/users';
const WORKERS_ACTION_PREFIX = 'workers';

export const NO_ACTION_WORKERS_URL = `/api/v2/calendar/unspecified-absences`;
export const NO_ACTION_WORKERS_ACTION_PREFIX = `${WORKERS_ACTION_PREFIX}/calendar/fetch/unspecified-absences`;

const REMAINING_DAYS_ACTION_PREFIX = 'remaining-days';

export const FETCH_WORKERS_ACTION_PREFIX = `${WORKERS_ACTION_PREFIX}/fetch/workers`;

export const FETCH_ASSOCIATES_ACTION_PREFIX = `${WORKERS_ACTION_PREFIX}/fetch/associates`;

export const FETCH_REMAINING_DAYS_URL_POSTFIX = `/vacation-days`;
export const FETCH_REMAINING_DAYS_ACTION_PREFIX = `${REMAINING_DAYS_ACTION_PREFIX}/fetch/user`;

export const FETCH_UNSPECIFIED_USERS_ACTION_PREFIX = `${WORKERS_ACTION_PREFIX}/fetch/unspecified-users`;

export const CHANGE_WORK_TIME_URL_POSTFIX = '/work-time';
export const CHANGE_WORK_TIME_ACTION_PREFIX = `${WORKERS_ACTION_PREFIX}/change/work-time`;

export const CHANGE_SELECTED_USER_ACTION_PREFIX = `${WORKERS_ACTION_PREFIX}/change/selected-user`;

export const CHANGE_REMAINING_DAYS_ACTION_PREFIX = `${WORKERS_ACTION_PREFIX}/change/remaining-days`;

export const CHANGE_IS_EC_ACTION_PREFIX = `${WORKERS_ACTION_PREFIX}/change/is-ec`;

export const CHANGE_NO_ACTION_WORKERS_ACTION_PREFIX = `${WORKERS_ACTION_PREFIX}/change/unspecified-absences`;
