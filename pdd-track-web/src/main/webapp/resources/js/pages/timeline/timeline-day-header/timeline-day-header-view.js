define(function (require) {

    'use strict';

    var Backbone = require('backbone');
    var _ = require('underscore');
    var $ = require('jquery');

    var template = _.template(require('text!./templates/timeline-day-header-template.html'));

    return Backbone.View.extend({

        initialize: function (options) {
            this.render();
        },

        render: function () {
            var jmodel = this.model.toJSON();
            //console.log(jmodel);

            var data = _.extend({}, jmodel.dayColumn, {});
            this.$el.html(template(data));

            /*if (jmodel.timelineDay.dayEvents.lecture) {
                this.$el.addClass('lecture');
            }

            if (jmodel.timelineDay.dayEvents.study) {
                this.$el.addClass('study');
            }*/

            return this;
        }
    });
});
