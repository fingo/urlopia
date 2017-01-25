// User roles
app.constant('USER_ROLES', {
    all: ['ROLES_ADMIN', 'ROLES_LEADER', 'ROLES_WORKER'],
    admin: 'ROLES_ADMIN',
    worker: 'ROLES_WORKER',
    leader: 'ROLES_LEADER'
});

// Authentication events
app.constant('AUTH_EVENTS', {
    loginSuccess: 'auth-login-success',
    badRequest: 'auth-bad-request',
    logoutSuccess: 'auth-logout-success',
    sessionCreated: 'auth-session-created',
    sessionTimeout: 'auth-session-timeout',
    notAuthenticated: 'auth-not-authenticated',
    notAuthorized: 'auth-not-authorized',
    refreshNeeded: 'app-refresh-needed'
});

app.constant('PROGRESS_BAR_EVENTS', {
    stop: "progress_bar_stop",
    start: "progress_bar_start",
    once: "progress_bar_once",
    stop_once: "progress_bar_stop_once"
});

// Cookie expiration time
app.constant('COOKIE_EXP_TIME', 86400000); //set to 1 day

app.constant('WORK_TIMES', ['1', '7/8', '4/5', '3/4', '3/5', '1/2', '2/5', '1/4', '1/5', '1/8', '1/16']);


