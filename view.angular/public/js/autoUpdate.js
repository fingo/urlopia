var autoUpdate = angular.module("autoUpdate", []);
autoUpdate.constant("frequency", 3000); //ms
autoUpdate.value('updateTime', new Date().getTime());
autoUpdate.value('tasks', []);
autoUpdate.value('isWorking', false);
autoUpdate.value('isUpdating', {value: 0});

autoUpdate.service('updater', function ($interval, $rootScope, frequency, updateTime, tasks, isWorking, API, isUpdating, PROGRESS_BAR_EVENTS) { // NOSONAR

    // PRIVATE FUNCTIONS
    function update(time) {
        tasks.forEach(function (task) {
            isUpdating.value++;
            var newRequests = API.setUrl(task.api + ((time !== undefined) ? '?time=:oldTime' : ''), {
                oldTime: time
            });
            newRequests.query(function (response) {
                $rootScope.$broadcast(PROGRESS_BAR_EVENTS.stop_once);
                isUpdating.value--;
                if (response.length > 0) {
                    task.scope.length = 0;              // delete all items from the scope

                    response.forEach(function (resp) {   // copy new items to the scope
                        task.scope.push(resp);
                    });
                }
            });
        });
    }

    function scheduleUpdate() {
        if (isWorking) {
            var time = updateTime;
            updateTime = new Date().getTime();

            update(time);
        }
    }

    // PUBLIC FUNCTIONS
    this.addTask = function (scope, apiPath) {
        var newTask = {scope: scope, api: apiPath};
        tasks.push(newTask);
        return newTask;
    };

    this.deleteTask = function (task) {
        var index = tasks.indexOf(task);
        if (index > -1) {
            tasks.splice(index, 1);
        }
    };

    this.load = function () {
        $rootScope.$broadcast(PROGRESS_BAR_EVENTS.once);
        update();
    };

    this.start = function () {
        isWorking = true;
        return $interval(scheduleUpdate, frequency);
    };

    this.pause = function () {
        isWorking = false;
    };

    this.resume = function () {
        isWorking = true;
    };
});