define(function (require) {

    'use strict';

    var Model = require('./timeline-day-model');
    var View = require('./timeline-day-view');

    function init(container, timelineDay) {
        var model = new Model({timelineDay: timelineDay});
        var view = new View({model: model, el: container});
    }

    return init;
});
