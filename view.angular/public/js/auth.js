// "The door to the API World"
app.factory('API', function ($resource, Session, progressBarInterceptor) {
    //object with setUrl function
    return {
        setUrl: function (url, params) {
            progressBarInterceptor.start();

            return $resource(':url', params, {

                query: {
                    method: 'GET',
                    url: url,
                    isArray: true,
                    headers: {
                        'Authorization': Session.data.token
                    },
                    interceptor: progressBarInterceptor
                },

                get: {
                    method: 'GET',
                    url: url,
                    isArray: false,
                    headers: {
                        'Authorization': Session.data.token
                    },
                    interceptor: progressBarInterceptor
                },

                save: {
                    method: 'POST',
                    url: url,
                    headers: {
                        'Authorization': Session.data.token
                    },
                    interceptor: progressBarInterceptor
                },

                remove: {
                    method: 'DELETE',
                    url: url,
                    headers: {
                        'Authorization': Session.data.token
                    },
                    interceptor: progressBarInterceptor
                },

                delete: {
                    method: 'DELETE',
                    url: url,
                    headers: {
                        'Authorization': Session.data.token
                    },
                    interceptor: progressBarInterceptor
                }
            })
        }
    }
});

// Authentication service
app.factory('Login', function ($resource) {
    return {
        prepare: function () {
            var url = '/api/session';
            return $resource(url, {}, {
                save: {
                    method: 'POST'
                }
            })
        }
    }
});

app.factory('AuthService', function ($rootScope, $translate, Login, Session, PersistService, USER_ROLES, AUTH_EVENTS, notifyService) {
    var authService = {};

    // login function
    authService.login = function (credentials) {
        var login = Login.prepare();

        login.save({}, credentials, function (response) {
            if (!angular.isUndefined(response.token)) {
                // setting user language
                $translate.use(response.language);

                // creating session
                Session.create(response);
                PersistService.setCookie();
                $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);

                // login success notification
                notifyService.displaySuccess($translate.instant('notify.auth.loginSuccess'));
            }
            else
                $rootScope.$broadcast(AUTH_EVENTS.badRequest);
        });
    };

    // logout function
    authService.logout = function () {
        Session.destroy();
        PersistService.clearCookieData();
        $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
    };

    authService.isAuthenticated = function () {
        return !(angular.isUndefined(Session.data) || Session.data === null || !Session.data.token);
    };

    authService.isAuthorized = function (authorizedRoles) {
        //intersection of two arrays helper function
        var intersection = function (a, b) {
            if (b.length > a.length) {
                var t;
                t = b;
                b = a;
                a = t;
            }

            return a.filter(function (e) {
                if (b.indexOf(e) !== -1) {
                    return true;
                }
            })
        };

        if (!angular.isArray(authorizedRoles)) {
            authorizedRoles = [authorizedRoles];
        }

        return (authService.isAuthenticated() && (intersection(Session.data.userRoles, authorizedRoles).length > 0));
    };

    return authService;
});

// Session singleton
app.service('Session', function () {
    this.create = function (session) {
        this.data = session;
        this.data.createdAt = Date.now();
    };
    this.destroy = function () {
        this.data = null;
    };
});

app.value('currentPrevented', {'url': null, params: null});

// Route change listener
app.run(function ($rootScope, $location, AUTH_EVENTS, AuthService, currentPrevented) {
    $rootScope.$on('$routeChangeStart', function (event, next) {
        var authorizedRoles;

        // error when path undefined
        if (angular.isUndefined(next.data)) {
            authorizedRoles = ""; // don't allow any access
        }
        else {
            authorizedRoles = next.data.authorizedRoles;
        }

        if (!AuthService.isAuthorized(authorizedRoles)) {
            event.preventDefault();

            if (AuthService.isAuthenticated()) {
                // user is not allowed
                $rootScope.$broadcast(AUTH_EVENTS.notAuthorized);
            } else {
                // user is not logged in
                if (!angular.isUndefined(next.$$route)) {
                    currentPrevented.url = next.$$route.originalPath;
                    currentPrevented.params = next.pathParams;
                }
                $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
            }
        }
    });
});

// adding interceptors to provider
app.config(function ($httpProvider) {
    $httpProvider.interceptors.push([
        '$injector',
        function ($injector) {
            return $injector.get('AuthInterceptor');
        }
    ]);
});

app.factory('AuthInterceptor', function ($rootScope, $q, AUTH_EVENTS) {
    return {
        responseError: function (response) {
            $rootScope.$broadcast({
                401: AUTH_EVENTS.notAuthenticated,
                403: AUTH_EVENTS.notAuthorized,
                419: AUTH_EVENTS.sessionTimeout,
                440: AUTH_EVENTS.sessionTimeout,
                400: AUTH_EVENTS.badRequest
            }[response.status], response);
            return $q.reject(response);
        }
    };
});

app.factory('PersistService', function ($rootScope, $cookies, AUTH_EVENTS, Session, COOKIE_EXP_TIME, $translate) {
    return {
        setCookie: function () {
            $cookies.put("session", JSON.stringify(Session.data));
        },

        getCookieData: function () {
            var sessionCookie = $cookies.get("session");

            if (!angular.isUndefined(sessionCookie)) {
                var session = JSON.parse($cookies.get("session"));

                if (Date.now() - session.createdAt < COOKIE_EXP_TIME) {
                    // setting user language
                    $translate.use(session.language);
                    $translate.refresh(session.language);

                    // creating session
                    Session.create(session);
                }
                else {
                    this.clearCookieData();
                    Session.destroy();
                    $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
                }

            }
            else
                $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
            return sessionCookie;
        },

        clearCookieData: function () {
            $cookies.remove("session");
        }
    }
});
