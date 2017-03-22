define(function (require) {

    'use strict';

    var Backbone = require('backbone');
    var _ = require('underscore');
    var $ = require('jquery');

    var template = _.template(require('text!./templates/timeline-page-template.html'));

    var timelineDayView = require( 'js/pages/timeline/timeline-day/timeline-day' );

    return Backbone.View.extend({

        initialize: function (options) {
            this.model.on('sync', this.render, this);
            this.model.fetch({cache: false});
        },

        render: function () {
            var jmodel = this.model.toJSON();
            var data = _.extend({}, jmodel, {});
            console.log(data);
            this.$el.html(template(data));

            var self = this;
            _.each(jmodel.items, function(item) {
                _.each(item.timelineDays, function (timelineDay) {
                    timelineDayView(self.$('.js-timeline-day-' + item.pddSection.number + '-' + timelineDay.dayIndex), timelineDay);
                });
            });

            return this;
        }
    });
});