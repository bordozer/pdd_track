define(function (require) {

    'use strict';

    var Backbone = require('backbone');

    return Backbone.Model.extend({

        defaults: {},

        initialize: function (options) {
            this.studentKey = options.options.studentKey;
            this.rulesSetKey = options.options.rulesSetKey;
        },

        url: function () {
            return '/student/timeline/' + this.studentKey + '/rules-set/' + this.rulesSetKey + '/';
        }
    });
});