app.service('notifyService', function () {

    this.displaySuccess = function (_message) {
        display(_message, 'success', 'glyphicon glyphicon-ok-sign');
    };

    this.displayInfo = function (_message) {
        display(_message, 'info', 'glyphicon glyphicon-info-sign');
    };

    this.displayDanger = function (_message) {
        display(_message, 'danger', 'glyphicon glyphicon-exclamation-sign');
    };

    var display = function (_message, _type, _icon) {
        $.notify({
            // options
            message: _message,
            icon: _icon
        }, {
            // settings
            type: _type,
            delay: 5000,
            animate: {
                enter: 'animated fadeInRight',
                exit: 'animated fadeOutRight'
            },
            placement: {
                from: 'bottom',
                align: 'right'
            },
            offset: {
                x: 20,
                y: 80
            }
        });
    };
});