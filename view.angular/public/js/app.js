var app = angular.module("app", ["pascalprecht.translate", "ngRoute", "ui.bootstrap", "ngResource", "autoUpdate", "ngCookies", "ngProgress", "smart-table", "ngSanitize"]);

// i18n
app.config(function ($translateProvider) {
    $translateProvider.useStaticFilesLoader({
        prefix: "locale/",
        suffix: ".json?version=0.4.18"
    })
        .preferredLanguage("pl")
        .fallbackLanguage("en")
        .useSanitizeValueStrategy('escape');

}).controller('translateController', function ($translate, $scope, API, Session, PersistService) {
    $scope.changeLanguage = function (langKey) {
        $translate.use(langKey);

        // setting language to the session cookie
        Session.data.language = langKey;
        PersistService.setCookie();

        // setting language in the database
        API.setUrl('/api/users/language')
            .save({language: langKey});
    }
});

//directive for views dependent on permissions
app.directive('permission', function (AuthService) {
    return {
        restrict: 'A',
        scope: {
            permission: '='
        },

        link: function (scope, elem) {
            scope.$watch(
                function (scope) {
                    return AuthService.isAuthorized(scope.permission);
                },
                function (newValue) {
                    if (newValue) {
                        elem[0].style["display"] = "block";
                    } else {
                        elem[0].style["display"] = "none";
                    }
                });
        }
    }
});

app.directive('loggedOnly', function (AuthService) {
    return {
        link: function (scope, elem) {
            scope.$watch(
                function () {
                    return AuthService.isAuthenticated();
                },
                function (newValue) {
                    if (newValue) {
                        elem[0].style["display"] = "block";
                    } else {
                        elem[0].style["display"] = "none";
                    }
                });
        }
    }
});

app.filter('reverse', function() {
  return function(items) {
    return items.slice().reverse();
  };
});

app.controller('ApplicationController', function ($scope, $uibModal, $location, $route, $translate, updater, PersistService, AUTH_EVENTS, currentPrevented, modalPrevent, notifyService) {
    // session from cookies
    PersistService.getCookieData();

    var updater = updater.start();
    $scope.$on('$destroy', function () {
        $interval.cancel(updater);
    });

    // login dialog (available in the whole application)
    var showLogin = function () {
        if (!modalPrevent.isPrevented()) {
            modalPrevent.start();
            $uibModal.open(
                {
                    animation: true,
                    templateUrl: 'partials/login.html',
                    controller: 'LoginCtrl',
                    backdrop: 'static',
                    keyboard: false,
                    windowClass: 'large-Modal login-modal'
                });
        }
    };

    $scope.prevPage = function () {
        window.history.back();
    };

    $scope.$on(AUTH_EVENTS.refreshNeeded, function () {
        $route.reload();
        $location.url(currentPrevented.url == null ? "/" : currentPrevented.url); //redirects to url template, then the params are updated

        //reset prevented
        currentPrevented.url = null; //handled path
        currentPrevented.params = null;
    });

    // not authenticated event
    $scope.$on(AUTH_EVENTS.notAuthenticated, showLogin);

    // session timeout - logout & notification
    $scope.$on(AUTH_EVENTS.sessionTimeout, function () {
        notifyService.displayInfo($translate.instant('notify.auth.sessionTimeout'));
        showLogin();
    });

    // not authorized notification
    $scope.$on(AUTH_EVENTS.notAuthorized, function () {
        notifyService.displayInfo($translate.instant('notify.auth.notAuthorized'));
    });
});

String.prototype.format = function() {
  var text = this;
  for (var i = 0; i < arguments.length; i++) {
      var toReplace = "{" + i + "}";
      var replacement = arguments[i];
      text = text.split(toReplace).join(replacement);
  }
  return text;
};