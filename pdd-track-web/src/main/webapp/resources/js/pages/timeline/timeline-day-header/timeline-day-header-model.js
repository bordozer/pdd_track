define(function (require) {

    'use strict';

    var Backbone = require('backbone');

    return Backbone.Model.extend({

        defaults: {},

        initialize: function (options) {
            this.set('dayColumn', options.options.dayColumn);
            this.set('startDate', options.options.startDate);
            this.set('endDate', options.options.endDate);
        }
    });
});
