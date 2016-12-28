app.service('modalPrevent', function () {

    this.start = function () {
        this.prevent = true;
    };
    this.stop = function () {
        this.prevent = false;
    };
    this.isPrevented = function () {
        return this.prevent;
    }
});

app.controller('LoginCtrl', function ($rootScope, $scope, $location, $route, $uibModalInstance, AuthService, AUTH_EVENTS, updater, modalPrevent, $http) {

    updater.pause();

    $scope.appVersion = '0.0.0';
    $http.get("/version").success(function(response) {
        $scope.appVersion = response;
    });

    //login() function
    $scope.login = function (credentials) {

        AuthService.login(credentials);

        var closeDialog = function () {
            modalPrevent.stop();
            $scope.loginError = false;
            $uibModalInstance.close();
            updater.resume();
            $rootScope.$broadcast(AUTH_EVENTS.refreshNeeded);
        };

        var showError = function () {
            $scope.loginError = true;
        };

        $scope.$on(AUTH_EVENTS.loginSuccess, function () {
            closeDialog();
            $location.path("/");
            $route.reload();
        });

        $scope.$on(AUTH_EVENTS.badRequest, showError);
    }
});

//LOGOUT
app.controller('logoutCtrl', function ($scope, $location, $route, $translate, AuthService, updater, notifyService, modalPrevent) {

    $scope.logout = function () {
        $location.path('/logout');
        $route.reload();
        modalPrevent.stop();
        AuthService.logout();
        updater.pause();

        // logout success notify
        notifyService.displaySuccess($translate.instant('notify.auth.logoutSuccess'));
    };
});