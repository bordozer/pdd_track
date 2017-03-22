define(function (require) {

    'use strict';

    var Backbone = require('backbone');
    var _ = require('underscore');
    var $ = require('jquery');

    var template = _.template(require('text!./templates/timeline-page-template.html'));

    var timelineDayView = require( 'js/pages/timeline/timeline-day/timeline-day' );
    var timelineDayHeaderView = require( 'js/pages/timeline/timeline-day-header/timeline-day-header' );

    return Backbone.View.extend({

        initialize: function (options) {
            this.model.on('sync', this.render, this);
            this.model.fetch({cache: false});
        },

        render: function () {
            var self = this;
            var jmodel = this.model.toJSON();
            console.log(jmodel);

            var data = _.extend({}, jmodel, {
                percentageClass: this._percentageClass,
                minTestForSuccess: 2 // TODO: read from settings
            });
            this.$el.html(template(data));

            _.each(jmodel.dayColumns, function(dayColumn) {
                var options = {
                    dayColumn: dayColumn,
                    startDate: jmodel.startDate,
                    endDate: jmodel.endDate
                };
                timelineDayHeaderView(self.$('.js-timeline-day-header-'+ dayColumn.dayIndex), options);
            });

            _.each(jmodel.items, function(item) {
                _.each(item.timelineDays, function (timelineDay) {
                    timelineDayView(self.$('.js-timeline-day-' + item.pddSection.number + '-' + timelineDay.dayIndex), timelineDay);
                });
            });

            return this;
        },

        _percentageClass: function(value) {
            return value > 90 ? 'text-success' : 'text-danger';
        }
    });
});