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

            if (jmodel.timelineDay.dayEvents.lecture) {
                this.$el.addClass('lecture');
            }

            if (jmodel.timelineDay.dayEvents.study) {
                this.$el.addClass('study');
            }

            return this;
        },

        _getHintIcon: function(timeLineDayHintType) {
            if (timeLineDayHintType == 'NEEDS_STUDY') {
                return 'fa fa-university';
            }
            if (timeLineDayHintType == 'NEEDS_RESTUDY') {
                return 'fa fa-graduation-cap';
            }
        }
    });
});
