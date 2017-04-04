define(function (require) {

    'use strict';

    var Backbone = require('backbone');
    var _ = require('underscore');
    var $ = require('jquery');

    var template = _.template(require('text!./templates/timeline-day-template.html'));

    return Backbone.View.extend({

        initialize: function (options) {
            this.render();
        },

        render: function () {
            var jmodel = this.model.toJSON();

            var data = _.extend({}, jmodel.timelineDay, {
                getHintIcon: this._getHintIcon
            });
            this.$el.html(template(data));

            if (jmodel.timelineDay.weekend) {
                this.$el.addClass('timeline-day-weekend');
            }
            if (jmodel.timelineDay.today) {
                this.$el.addClass('timeline-day-today');
            }
            if (jmodel.timelineDay.dayEvents.lecture) {
                this.$el.addClass('timeline-day-lecture');
            }
            if (jmodel.timelineDay.dayEvents.study) {
                this.$el.addClass('timeline-day-study');
            }

            return this;
        },

        _getHintIcon: function(dayHint) {
            var timeLineDayHintType = dayHint.dayHintType;
            if (timeLineDayHintType === 'LECTURE_WITHOUT_STUDY') {
                return {icon: 'fa fa-eye-slash', color: 'text-danger', hint: 'Section is not studied for ' + dayHint.ageInDays + ' day(s)'};
            }
            if (timeLineDayHintType === 'NEEDS_RESTUDY') {
                return {icon: 'fa fa-book', color: 'text-warning', hint: 'Theory is not repeated for ' + dayHint.ageInDays + ' day(s)'};
            }
            if (timeLineDayHintType === 'STUDY_WITHOUT_TESTING') {
                return {icon: 'fa fa-dot-circle-o', color: 'text-danger', hint: 'Studied section is ' + dayHint.ageInDays + ' day(s) without any testing'};
            }
            if (timeLineDayHintType === 'ADVICE_REFRESH_TESTS') {
                return {icon: 'fa fa-battery-quarter', color: 'text-muted', hint: 'Last testing was ' + dayHint.ageInDays + ' day(s) ago'};
            }
            if (timeLineDayHintType === 'LAST_TESTING_IS_RED') {
                return {icon: 'fa fa-exclamation-triangle', color: 'text-danger', hint: 'Last testing is red for ' + dayHint.ageInDays + ' day(s)'};
            }
            if (timeLineDayHintType === 'AVERAGE_TESTS_PERCENTAGE_IS_RED') {
                return {icon: 'fa fa-bug', color: 'text-danger', hint: 'Average testing percentage is red for ' + dayHint.ageInDays + ' day(s)'};
            }
            if (timeLineDayHintType === 'NEEDS_MORE_TESTING') {
                return {icon: 'fa fa-thermometer-empty', color: 'text-danger', hint: 'Needs more testing'};
            }
        }
    });
});
