define(function (require) {

    'use strict';

    var Backbone = require('backbone');
    var _ = require('underscore');
    var $ = require('jquery');

    var template = _.template(require('text!./templates/timeline-day-header-template.html'));

    var dateTimeService = require( '/resources/js/services/date-time-service.js' );

    return Backbone.View.extend({

        initialize: function (options) {
            this.render();
        },

        render: function () {
            var jmodel = this.model.toJSON();
            //console.log(jmodel);

            var data = _.extend({}, jmodel.dayColumn, {dateTimeService: dateTimeService});
            this.$el.html(template(data));

            if (jmodel.dayColumn.columnEvents.dayPassed) {
                this.$el.addClass('timeline-day-header-passed');
            }

            if (jmodel.dayColumn.columnEvents.today) {
                this.$el.addClass('timeline-day-header-today');
            }

            if (jmodel.dayColumn.columnEvents.lectureDay) {
                this.$el.addClass('timeline-day-header-lecture-day');
            }

            return this;
        }
    });
});
