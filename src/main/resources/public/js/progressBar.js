app.service('progressBar', function ($rootScope, ngProgressFactory, isUpdating, PROGRESS_BAR_EVENTS) {
    var parallelStart = false;
    $rootScope.$on(PROGRESS_BAR_EVENTS.once, function () {
        parallelStart = true;
        progressBar.start();
    });
    $rootScope.$on(PROGRESS_BAR_EVENTS.stop_once, function () {
        if(parallelStart)
            progressBar.complete();
        parallelStart = false;
    });

    var progressBar = ngProgressFactory.createInstance();
    var setup = function () {
        progressBar.reset();
        progressBar.setColor('#72a200');
    };
    var inProgress = 0;
    var isShowingProgress = false;

    setup();

    this.start = function () {
        if (isUpdating.value === 0) {
            inProgress++;
            if (!isShowingProgress) {
                isShowingProgress = true;
                progressBar.start();
            }
        }
    };

    this.stop = function () {
        inProgress--;
        if (inProgress <= 0 && isUpdating.value === 0) {
            isShowingProgress = false;
            progressBar.complete();
        }
        else {

        }
    };
});

app.factory('progressBarInterceptor', function (progressBar) {
    return {
        'response': function (response) {
            progressBar.stop();
            var result = response.data;
            if (!result) result = {};
            result["$status"] = response.status;
            result["$statusText"] = response.statusText;
            return result;
        },
        'responseError': function (response) {
            progressBar.stop();
            var result = response.data;
            if (!result) result = {};
            result["$status"] = response.status;
            result["$statusText"] = response.statusText;
            return result;
        },
        start: function () {
            progressBar.start();
        }
    }
});