define(function (require) {

    'use strict';

    var Backbone = require('backbone');

    return Backbone.Model.extend({

        defaults: {},

        initialize: function (options) {
            this.studentKey = options.options.studentKey;
        },

        url: function () {
            return '/student/timeline/' + this.studentKey + '/';
        }
    });
});