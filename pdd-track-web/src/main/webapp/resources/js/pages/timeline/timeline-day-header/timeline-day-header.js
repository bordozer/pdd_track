define(function (require) {

    'use strict';

    var Model = require('./timeline-day-header-model');
    var View = require('./timeline-day-header-view');

    function init(container, dayColumn) {
        var model = new Model({dayColumn: dayColumn});
        var view = new View({model: model, el: container});
    }

    return init;
});
