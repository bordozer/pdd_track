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
            if (timeLineDayHintType == 'NEEDS_STUDY') {
                return {icon: 'fa fa-book', color: 'text-danger', hint: 'Not studied section'};
            }
            if (timeLineDayHintType == 'ADVICE_REFRESH_TESTS') {
                return {icon: 'fa fa-clock-o', color: 'text-muted', hint: 'Advice: refresh tests'};
            }
            if (timeLineDayHintType == 'RED_TESTS') {
                return {icon: 'fa fa-exclamation-triangle', color: 'text-danger', hint: 'Your tests results are red!'};
            }
        }
    });
});
