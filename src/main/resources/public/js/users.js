app.controller('WorkerCtrl', function ($scope, $translate, updater, API, Session, notifyService) {
    var isLeader = Session.data.userRoles.indexOf("ROLES_LEADER") !== -1;
    var workerRequestsTask = null;
    var leaderRequestsTask = null;

    // WORKER VIEW
    $scope.worker = {};
    $scope.worker.requests = [];
    $scope.worker.holidaysPool = API.setUrl('/api/history').get({userId: Session.data.userId});

    $scope.worker.cancelRequest = function (requestID) {
        var action = API.setUrl('/api/request/cancelRequest');
        action.save({requestID: requestID, action: "cancelRequest"}, function () {
            updater.load();
        });

        // request canceled notification
        notifyService.displayInfo($translate.instant('notify.request.cancel'));
    };
    workerRequestsTask = updater.addTask($scope.worker.requests, "/api/request/worker");

    // LEADER VIEW
    if (isLeader) {
        $scope.leader = {};
        $scope.leader.requests = [];
        $scope.leader.accept = function (acceptanceId) {
            var action = API.setUrl('/api/acceptance/action');
            action.save({acceptanceId: acceptanceId, action: "accept"}, function (item) {
                updater.load();
                if (item.value === false) {
                    // form request not enough days pool when accepting notification
                    notifyService.displayInfo($translate.instant('notify.request.notEnoughDaysPoolAccepting'));
                } else {
                    // granted holidays notification
                    notifyService.displaySuccess($translate.instant('notify.request.accept'));
                }
            });
        };
        $scope.leader.reject = function (acceptanceId) {
            var action = API.setUrl('/api/acceptance/action');
            action.save({acceptanceId: acceptanceId, action: "reject"}, function () {
                updater.load();
                // rejected holidays notification
                notifyService.displayDanger($translate.instant('notify.request.deny'));
            });
        };
        leaderRequestsTask = updater.addTask($scope.leader.requests, "/api/request/leader");
    }

    // ALL VIEWS
    updater.load();
    $scope.$on('$destroy', function () {
        updater.deleteTask(workerRequestsTask);
        updater.deleteTask(leaderRequestsTask);
    });
});

app.controller('RequestsCtrl', function ($scope, $translate, updater, API, Session, $filter, notifyService) {
    $scope.requests = [];
    var adminRequestsTask = updater.addTask($scope.requests, "/api/request/admin");
    updater.load();

    $scope.userId = Session.data.userId;

    $scope.accept = function (requestId) {
        var action = API.setUrl('/api/request/action');
        action.save({requestId: requestId, action: "accept"}, function (item) {
            updater.load();
            if (item.value === false) {
                // request not enough days pool when accepting notification
                notifyService.displayDanger($translate.instant('notify.request.notEnoughDaysPoolAccepting'));
            } else {
                // granted holidays notification
                notifyService.displayInfo($translate.instant('notify.request.accept'));
            }
        });
    };

    $scope.reject = function (requestId) {
        var action = API.setUrl('/api/request/action');
        action.save({requestId: requestId, action: "reject"}, function () {
            updater.load();
            notifyService.displayInfo($translate.instant('notify.request.deny'));
        });
    };

    $scope.cancel = function (requestId) {
        var action = API.setUrl('/api/request/action');
        action.save({requestId: requestId, action: "cancel"}, function () {
            updater.load();
            notifyService.displayInfo($translate.instant('notify.request.cancel'));
        });
    };

    $scope.$on('$destroy', function () {
        updater.deleteTask(adminRequestsTask);
    });
});

app.controller('WorkerHistoryCtrl', function ($scope, API, Session) {
    $scope.year = new Date().getFullYear();
    $scope.years = [];
    $scope.Math = window.Math;
    $scope.histories = API.setUrl('/api/workerHistory').get({userId: Session.data.userId, year: $scope.year});
    $scope.firstHistory = API.setUrl('/api/firstHistory').get({userId: Session.data.userId}, function (firstHistory) {

        var i;
        for (i = $scope.year; i >= firstHistory.year; i--) {
            $scope.years.push(i);
        }
    });
    $scope.obj = {pool: 0};

    $scope.selectedItem = $scope.year;
    $scope.dropBoxItemSelected = function (item) {
        $scope.selectedItem = item;
        $scope.histories = API.setUrl('/api/workerHistory').get({userId: Session.data.userId, year: item});
    }
});

app.controller('UserHistoryCtrl', function ($scope, $routeParams, API, Session) {
    $scope.year = new Date().getFullYear();
    $scope.years = [];
    $scope.Math = window.Math;
    $scope.userMail = $routeParams.mail;
    $scope.histories = API.setUrl('/api/workerHistory').get({mail: $scope.userMail, year: $scope.year});
    $scope.firstHistory = API.setUrl('/api/firstHistory').get({mail: $scope.userMail}, function (firstHistory) {
        var i;
        for (i = $scope.year; i >= firstHistory.year; i--) {
            $scope.years.push(i);
        }
    });
    $scope.obj = {pool: 0};

    $scope.selectedItem = $scope.year;

    $scope.dropBoxItemSelected = function (item) {
        $scope.selectedItem = item;
        $scope.histories = API.setUrl('/api/workerHistory').get({mail: $scope.userMail, year: item});
    }
});

app.controller('EmployeesCtrl', function ($scope, API, $filter, $translate, notifyService) {
    $scope.synchronize = function () {
        var action = API.setUrl('/api/user/synchronize');
        action.save(
            function () {
                notifyService.displaySuccess($translate.instant('notify.admin.synchronize.success'));
            },
            function () {
                notifyService.displayDanger($translate.instant('notify.admin.synchronize.failure'))
            });
    };

    $scope.users = API.setUrl('/api/request/employees').query();
    $scope.teams = API.setUrl('/api/request/teams').query();

    $scope.isCollapsed = true;
    $scope.teamFilter = "";
    $scope.selectedTeam = $filter('translate')('employees_view.all_teams');
    $scope.dropDownTeams = function (item) {
        $scope.selectedTeam = $scope.teamFilter = item.name;

    };

    $scope.allEmployees = function () {
        $scope.selectedTeam = $filter('translate')('employees_view.all_teams');
        $scope.teamFilter = "";
    };

    $scope.propertyName = 'mail';
    $scope.reverse = false;

    $scope.sortBy = function (propertyName) {
        $scope.reverse = ($scope.propertyName === propertyName) ? !$scope.reverse : false;
        $scope.propertyName = propertyName;
    };

    $scope.teamSearch = function (item) {
        if ($scope.teamFilter === "") {
            return true;
        }
        for (var i = 0; i < item.teams.length; i++) {
            if (item.teams[i].name === $scope.teamFilter) {
                return true;
            }
        }
        return false;
    };

    $scope.contractFilter = "";
    $scope.selectedContract = $filter('translate')('employees_view.all_employees');

    $scope.dropDownContracts = function (item) {
        $scope.contractFilter = item;
    };

    $scope.contractSearch = function (item) {
        if (!item.mail) { //dont show records without email
            return false;
        }
        if ($scope.contractFilter === "") {
            return true;
        }
        if ((item.b2B || item.urlopiaTeam) && $scope.contractFilter === 'others') {
            $scope.selectedContract = $filter('translate')('employees_view.others');
            return true;
        } else if (item.ec && $scope.contractFilter === 'ec') {
            $scope.selectedContract = $filter('translate')('employees_view.EC_employees');
            return true;
        }
        return false;
    };

    $scope.leaderSearch = function (item) {
        return !$scope.leaderSearch.leader || $scope.leaderSearch.leader === item.leader;
    };

    $scope.allContracts = function () {
        $scope.selectedContract = $filter('translate')('employees_view.all_employees');
        $scope.contractFilter = "";
    };
});

app.controller('HolidaysCtrl', function ($scope, $route, $location, $uibModal, $translate, notifyService, API) {
    $scope.ctrl = $scope; //to bind datepicker value with $scope.currentDate

    //change datepicker options
    var setOptions = function () {
        $scope.currentOptions = {
            minDate: new Date($scope.currentYear, 0, 1),
            maxDate: new Date($scope.currentYear, 11, 31),
            startingDay: 1
        };

        $scope.newDateOptions = {
            minDate: new Date($scope.currentYear, 0, 1),
            maxDate: new Date($scope.currentYear, 11, 31),
            startingDay: 1
        };
    };

    var resetNewDate = function () {
        var date = new Date();
        date.setFullYear($scope.currentYear);
        $scope.newDate = {
            name: null,
            date: date
        };
    };

    $scope.$watch('currentDate', function () {
        if ($scope.holidays.$resolved) {
            $scope.holidays[$scope.currentIndex].date = $scope.currentDate.getTime();
        }
    });
    $scope.$watch('currentName', function () {
        if ($scope.holidays.$resolved)
            $scope.holidays[$scope.currentIndex].name = $scope.currentName;
    });

    $scope.calendarVisible = false;
    $scope.currentDate = null;
    $scope.currentYear = new Date().getFullYear();
    $scope.years = [$scope.currentYear, $scope.currentYear + 1];
    resetNewDate();

    var sort = function (holidays) {
        holidays.sort(function (a, b) {
            return a.date - b.date;
        });
    };

    var prepare_data = function () {
        sortHolidays();

        var year = new Date().getFullYear();
        var split_index = 0;
        //split index init
        for (i = 0; i < holidays.length; i++) {
            if (new Date(holidays[i].date).getFullYear() > year) {
                split_index = i;
                break;
            }
        }
        $scope.holidays = holidays.slice(0, split_index);
        $scope.holidays_year = year;
        $scope.holidays_next = holidays.slice(split_index, holidays.length);
        $scope.holidays_next_year = year + 1;

        $scope.currentYear = $scope.holidays_year;
        $scope.currentHolidays = $scope.holidays;
        $scope.years = [$scope.holidays_year, $scope.holidays_next_year];
    };

    $scope.holidays = API.setUrl('/api/holiday').query({year: $scope.currentYear}, function () {
        sort($scope.holidays);
        setOptions();
    });

    $scope.setYear = function (year) {
        $scope.currentYear = year;
        $scope.saveHolidays();
        setOptions();

        resetNewDate();
        $scope.holidays = API.setUrl('/api/holiday').query({year: year}, function () {
            sort($scope.holidays);
        });
        $scope.calendarVisible = false;
        //scroll to the details section
        $('html, body').animate({
            scrollTop: $("#holiday-details").offset().top
        }, 1000);
    };

    $scope.getDate = function (milis) {
        return new Date(milis).toLocaleDateString();
    };

    $scope.showWarning = function (holiday) {
        var day = new Date(holiday.date).getDay();
        return day === 6;
    };

    $scope.setCurrentHoliday = function (holiday) {
        $scope.calendarVisible = true;
        $scope.currentDate = new Date(holiday.date);
        $scope.currentName = holiday.name;
        $scope.currentIndex = $scope.holidays.indexOf(holiday);

        //scrolling to the "details" section
        $('html, body').animate({
            scrollTop: $("#holiday-details").offset().top
        }, 1000);
    };

    $scope.reset = function () {
        $uibModal.open(
            {
                animation: true,
                templateUrl: 'partials/confirm_reset.html',
                controller: 'ConfirmResetCtrl'
            }).result.then(function () {
            $scope.holidays = API.setUrl('/api/holiday').query({year: $scope.currentYear}, function () {
                sort($scope.holidays);
                setOptions();
            });
        });
    };

    //delete current holiday (splice on the current index)
    $scope.delete = function () {
        $scope.holidays.splice($scope.currentIndex, 1);
        $scope.calendarVisible = false;
    };

    $scope.saveNewDate = function () {
        if ($scope.newDate.date.getFullYear() === $scope.currentYear) {
            if ($scope.newDate.name !== null && $scope.newDate.name.trim().length != 0) {
                $scope.newDate.date = $scope.newDate.date.getTime();
                $scope.holidays.push($scope.newDate);
                sort($scope.holidays);
                $scope.calendarVisible = false;
                resetNewDate();
            }
            else
                notifyService.displayDanger($translate.instant('notify.holidays.missingName'));
        }
    };

    $scope.saveHolidays = function () {
        API.setUrl('api/holiday/save').save($scope.holidays,
            function () {
                notifyService.displaySuccess($translate.instant('notify.holidays.save.success'));
            },
            function () {
                notifyService.displayDanger($translate.instant('notify.holidays.save.failure'));
            }
        );
    };

    //restore default uses generate holidays from holidayService
    $scope.default = function () {
        $uibModal.open(
            {
                animation: true,
                templateUrl: 'partials/confirm_reset.html',
                controller: 'ConfirmResetCtrl'
            }).result.then(function () {
            $scope.holidays = API.setUrl('/api/holiday/default').query({year: $scope.currentYear}, function () {
                sort($scope.holidays);
                $scope.saveHolidays();
            })
        });
    };
});

app.controller('ConfirmResetCtrl', function ($scope, $uibModalInstance) {
    $scope.confirm = function () {
        $uibModalInstance.close();
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss();
    };
});

//employees details and modal confirmation data binding value
app.value('confirmData', {employee: null, daysToAdd: null, mail: null, comment: ""});

//Controller of displayed data

app.controller('UserDetailsCtrl', function ($scope, $route, $uibModal, $translate, API, confirmData, notifyService, WORK_TIMES) {
    var maxHours = 8000;

    //tooltpis activation
    $('[data-toggle="tooltip"]').tooltip()
        .tooltip('hide')
        .attr('data-original-title', $translate.instant('employees_view.hourly_tooltip'))
        .tooltip('fixTitle');

    //Gathering data
    var getHours = function () {
        if ($scope.userHours === undefined) {
            API.setUrl("/api/history").get({mail: $scope.user.principalName}, function (response) {
                $scope.userHours = response.pool;
                $scope.workTime = response.workTime;
                $scope.days = response.days;
                $scope.hours = response.hours;
                $scope.workTimeA = response.workTimeA;
                $scope.workTimeB = response.workTimeB;
                $scope.selectedWorkTime = ($scope.workTimeA !== $scope.workTimeB) ? $scope.workTimeA + "/" + $scope.workTimeB : "1";
                $scope.newWorkTime = response.workTime;
            });
        }
    };
    $scope.userHours = getHours();

    // LAST CHANGES
    $scope.userHistory = API.setUrl("/api/userHistory/recent").get({userMail: $scope.user.principalName});

    // DAYS POOL
    $scope.daysPoolChange = '';

    var parseDaysPool = function() {
        var regEx = /^ *[+-]?( *\d+d)?( *\d+h)?( *\d+m)? *$/;
        if(!regEx.test($scope.daysPoolChange)) {
            return false;
        }

        // parse days
        var parsedPart = new RegExp("(\\d+)d").exec($scope.daysPoolChange);
        var hours = parsedPart ? parseInt(parsedPart[1]) * 8 : 0;

        // parse hours
        parsedPart = new RegExp("(\\d+)h").exec($scope.daysPoolChange);
        hours += parsedPart ? parseInt(parsedPart[1]) : 0;

        // parse minutes
        parsedPart = new RegExp("(\\d+)m").exec($scope.daysPoolChange);
        hours += parsedPart ? parseInt(parsedPart[1]) / 60 : 0;

        // determine the sign
        if(new RegExp("-").test($scope.daysPoolChange)) {
            hours *= -1;
        }

        return hours;
    };

    $scope.changeDaysPool = function () {
        var hours = parseDaysPool();

        // check format
        if(!hours) {
            notifyService.displayDanger($translate.instant('notify.admin.employees.parsingProblem'));
            return false;
        }

        // check if user pool will not be less than 0
        if ($scope.userHours < -hours) {
            notifyService.displayDanger($translate.instant('notify.admin.employees.subtractDaysFail'));
            return false;
        }

        // check if user pool will not be less than MAX
        if (hours + $scope.userHours > maxHours) {
            notifyService.displayDanger($translate.instant('notify.admin.employees.addDaysFail'));
            return false;
        }

        $uibModal.open({
            animation: true,
            templateUrl: 'partials/confirm_days.html',
            controller: 'ConfirmPoolChangeCtrl',
            resolve: {
                values: function() {
                    return {hours: hours,
                        userMail: $scope.user.principalName,
                        comment: $scope.comment,
                        userName: $scope.user.surname + " " + $scope.user.name};
                }
            }
        }).closed.then(function () {
            $scope.$parent.$parent.isCollapsed = true;

            /* TODO: make view changes without collapsing
            $scope.userHours += hours;
            $scope.daysPoolChange = '';
            $scope.comment = '';
            */
        });
    };

    // WORK TIME
    $scope.workTimes = WORK_TIMES;

    $scope.changeWorkTime = function () {
        API.setUrl("/api/history/setWorkTime")
            .save({mail: $scope.user.principalName, workTime: $scope.selectedWorkTime}, function () {
                notifyService.displaySuccess($translate.instant('notify.admin.employees.changeWorkTime'));
            })
    };

    $scope.getWorkTimeTranslation = function(time) {
        if(time === '1') {
            return $translate.instant('employees_view.full_time');
        } else {
            return $translate.instant('employees_view.work_time', {time: time});
        }
    };

//TODO authentication before accessing resource
    $scope.report = function () {
        var url = '/report?mail=' + $scope.user.principalName;
        window.location.assign(url);
    };
})
;

app.controller('ConfirmPoolChangeCtrl', function ($scope, $uibModalInstance, $translate, confirmData, API, notifyService, values) {
    $scope.isNegative = values.hours < 0;
    $scope.isHourly = true;
    $scope.daysToAdd = Math.abs(values.hours);
    $scope.employee = values.userName;

    $scope.confirm = function () {
        API.setUrl("/api/history").save({
            mail: values.userMail,
            hours: values.hours,
            comment: values.comment
        }, function() {
            notifyService.displaySuccess($translate.instant('notify.admin.employees.changeDaysPool'));
        });

        $uibModalInstance.close();
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss();
    };
});

app.controller('UserInfoCtrl', function ($scope, Session, AUTH_EVENTS) {

    $scope.session = Session.data;
    $scope.$on(AUTH_EVENTS.loginSuccess, function () {
        $scope.session = Session.data;
    });
});

