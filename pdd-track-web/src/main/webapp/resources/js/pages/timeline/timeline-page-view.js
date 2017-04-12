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
            // console.log(jmodel);

            var data = _.extend({}, jmodel, {
                percentageClass: this._percentageClass,
                pddSectionSummaryStatusClass: this._pddSectionSummaryStatusClass,
                minTestForSuccess: 3 // TODO: read from settings
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
            var today = this.$('.timeline-day-today')[0];
            var todayX = $(today).position().left;
            this.$('.row-fluid').animate({
                scrollLeft: todayX - 126
            }, 200);

            return this;
        },

        // TODO: duplicated in timeline-day-view.js
        _percentageClass: function(value) {
            if (value === 100) {
                return 'timeline-day-testing-excellent'
            }

            if (value >= 96) { // TODO: constant COOL_TEST_PERCENTAGE
                return 'timeline-day-testing-cool'
            }
            return value > 90 ? 'text-success' : 'text-danger';
        },

        _pddSectionSummaryStatusClass: function(status) {
            if (status === 'NO_LECTURE_YET') {
                return {icon: 'fa fa-hourglass-o', color: 'text-muted', hint: 'No lecture yet'}
            }
            if (status === 'COMPLETELY_READY') {
                return {icon: 'fa fa-check-square', color: 'pdd-section-ready', hint: 'The section is ready'}
            }
            if (status === 'READY_WITH_RISK') {
                return {icon: 'fa fa-check-square-o', color: 'pdd-section-ready-with-risks', hint: 'The section is ready but under a risk - repeat testing, please'}
            }
            if (status === 'NEED_MORE_TESTING') {
                return {icon: 'fa fa-thermometer-empty', color: 'pdd-section-warning', hint: 'The section needs more testing'}
            }
            if (status === 'TESTS_ARE_RED') {
                return {icon: 'fa fa-bug', color: 'pdd-section-danger', hint: 'The section\' tests are red'}
            }
            if (status === 'TO_STUDY') {
                return {icon: 'fa fa-eye-slash', color: 'pdd-section-to-study', hint: 'The section is NOT studied'}
            }
            return {icon: '', color: '', hint: ''}
        }
    });
});