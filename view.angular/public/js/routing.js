app.config(function ($routeProvider, $locationProvider, USER_ROLES) {
    $routeProvider
        .when("/", {
            templateUrl: "partials/worker.html",
            controller: "WorkerCtrl",
            data: {
                authorizedRoles: USER_ROLES.worker
            }
        })
        .when("/logout", {
            template: "",
            data: {
                authorizedRoles: USER_ROLES.all
            }
        })
        .when("/history", {
            templateUrl: "partials/workerHistory.html",
            controller: "WorkerHistoryCtrl",
            data: {
                authorizedRoles: USER_ROLES.worker
            }
        })
        .when("/admin", {
            templateUrl: "partials/admin.html",
            data: {
                authorizedRoles: USER_ROLES.admin
            }
        })
        .when("/admin/requests", {
            templateUrl: "partials/admin_requests.html",
            controller: "RequestsCtrl",
            data: {
                authorizedRoles: USER_ROLES.admin
            }
        })
        .when("/admin/holidays", {
            templateUrl: "partials/admin_holidays.html",
            controller: "HolidaysCtrl",
            data: {
                authorizedRoles: USER_ROLES.admin
            }
        })
        .when("/admin/employees", {
            templateUrl: "partials/admin_employees.html",
            controller: "EmployeesCtrl",
            data: {
                authorizedRoles: USER_ROLES.admin
            }
        })
        .when("/form", {
            templateUrl: "partials/form.html",
            data: {
                authorizedRoles: USER_ROLES.all
            }
        })
        .when("/admin/employees/user", {
            templateUrl: "partials/userHistory.html",
            controller: "UserHistoryCtrl",
            data: {
                authorizedRoles: USER_ROLES.admin
            }
        })
        .otherwise({
            templateUrl: "partials/error.html",
            data: {
                authorizedRoles: USER_ROLES.all
            }
        });

    // use the HTML5 History API
    $locationProvider.html5Mode(true);
});