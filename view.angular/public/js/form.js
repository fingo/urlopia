app.controller('formCtrl', function ($scope, $resource, $uibModalInstance, $translate, API, updater, Session, $filter, notifyService, $sce) {

    function uniqueFilter(value, index, self) {
        return self.indexOf(value) === index;
    }

    function htmlDecode(input){
        var e = document.createElement('div');
        e.innerHTML = input;
        return e.childNodes[0].nodeValue;
    }

    $scope.day = 24 * 60 * 60 * 1000;   // 24 hours
    $scope.today = new Date();
    $scope.tomorrow = new Date($scope.today.getTime() + $scope.day);
    $scope.requester = Session.data.name + " " + Session.data.surname;
    $scope.requesterMail = Session.data.mail;
    $scope.teams = Session.data.teams;
    $scope.leadersNames = $scope.teams.map(function (team) {
        return team.leader;
    }).filter(uniqueFilter);
    $scope.startDate = new Date($scope.today.getTime());
    $scope.endDate = new Date($scope.today.getTime());
    $scope.format = 'yyyy-MM-dd';
    $scope.isOccasional = false;
    $scope.type = 0;

    $scope.startDateOptions = {
        minDate: new Date($scope.today.getTime() - $scope.day * 30),
        startingDay: 1,
        showWeeks: false
    };

    //Dropdown
    $scope.info = htmlDecode($translate.instant('occasional.info'));

    $scope.reasons = [$translate.instant('occasional.birth2'), $translate.instant('occasional.funeral2'), $translate.instant('occasional.wedding2'), $translate.instant('occasional.funeral1'), $translate.instant('occasional.wedding1')];
    $scope.status = {
        isopen: false
    };

    $scope.selectedItem = $scope.reasons[0];
    $scope.dropBoxItemSelected = function (item) {
        $scope.selectedItem = item;
        $scope.type = $scope.reasons.indexOf(item) + 1;
        $scope.startDateChange();

    };
    $scope.changeOccasional = function () {

        if ($scope.isOccasional) {
            $scope.type = 0;
            $scope.isOccasional = false;
        } else {
            $scope.type = 1;
            $scope.isOccasional = true;
            $scope.startDateChange();
            $scope.dropBoxItemSelected($scope.reasons[0])
        }
    };

    $scope.startDatePopup = {
        opened: false
    };

    $scope.openStartDate = function () {
        $scope.startDatePopup.opened = true
    };

    // on start date change
    $scope.startDateChange = function () {
        $scope.endDateOptions.minDate = $scope.startDate;
        //If occasion requires two days
        if ($scope.type > 0 && $scope.type < 4) {
            //if day is a friday monday is also free
            if ($scope.startDate.getDay() == 5) {
                $scope.endDate = new Date($scope.startDate.getTime() + ($scope.day * 3));
            } else {
                $scope.endDate = new Date($scope.startDate.getTime() + $scope.day);
            }

        }
        if ($scope.type >= 4) {
            $scope.endDate = new Date($scope.startDate.getTime());
        }
        if ($scope.endDate < $scope.startDate)
            $scope.endDate = $scope.startDate
    };

    $scope.endDateOptions = {
        minDate: $scope.startDate,
        startingDay: 1,
        showWeeks: false
    };

    $scope.endDatePopup = {
        opened: false
    };

    $scope.openEndDate = function () {
        $scope.endDatePopup.opened = true
    };

    $scope.sendData = function () {
        var type = null;
        switch ($scope.type) {
          case 1: type = "D2_BIRTH"; break;
          case 2: type = "D2_FUNERAL"; break;
          case 3: type = "D2_WEDDING"; break;
          case 4: type = "D1_FUNERAL"; break;
          case 5: type = "D1_WEDDING";
        }
        API.setUrl('/api/users/' + Session.data.userId + '/requests', {}).save({
          startDate: $scope.startDate,
          endDate: $scope.endDate,
          type: ($scope.isOccasional) ? "OCCASIONAL" : "NORMAL",
          occasionalType: type
        }).$promise.then(function(response) {
            if(response.$status >= 200 && response.$status < 300) {
              notifyService.displaySuccess($translate.instant('notify.form.success'));
            } else {
              notifyService.displayDanger($translate.instant('notify.form.fail'));
            }
        }).catch(function() {
            notifyService.displayDanger($translate.instant('notify.form.fail'));
        });

        $uibModalInstance.close();
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});

app.controller('modalCtrl', function ($scope, $uibModal, Session, AUTH_EVENTS) {
    if (angular.isUndefined(Session.data))
        $scope.showRequestButton = false;
    else
        $scope.showRequestButton = !(Session.data.teams === null);

    $scope.$on(AUTH_EVENTS.loginSuccess, function () {
        $scope.showRequestButton = !(Session.data.teams === null);
    });

    $scope.open = function () {
        $uibModal.open(
            {
                animation: true,
                templateUrl: 'partials/request.html',
                controller: 'formCtrl',
                size: 'sm'
            });
    };
});