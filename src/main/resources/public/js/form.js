app.controller('formCtrl', function ($scope, $resource, $uibModalInstance, $translate, API, updater, Session, $filter, notifyService) {

    $scope.day = 24 * 60 * 60 * 1000;   // 24 hours
    $scope.today = new Date();
    $scope.tomorrow = new Date($scope.today.getTime() + $scope.day);
    $scope.requester = Session.data.name + " " + Session.data.surname;
    $scope.requesterMail = Session.data.mail;
    $scope.teams = Session.data.teams;
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
            dropBoxItemSelected(reasons[0])
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

        API.setUrl('/api/modal', {}).save({
                startDate: $scope.startDate,
                endDate: $scope.endDate,
                leader: $scope.leader,
                requester: $scope.requester,
                requesterMail: $scope.requesterMail,
                teams: $scope.teams,
                type: $scope.type

            }, function (error_code) {
                updater.load();
                if (error_code === "SUCCESS") {
                    notifyService.displaySuccess($translate.instant('notify.form.success'));
                } else if (error_code === "NOT_ENOUGH_DAYS") {
                    notifyService.displayDanger($translate.instant('notify.form.notEnoughDaysPool'));
                } else if (error_code === "REQUEST_OVERLAPPING") {
                    notifyService.displayDanger($translate.instant('notify.form.requestOverlapping'));
                } else {
                    notifyService.displayDanger($translate.instant('notify.form.fail'));
                }
            }
        );

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