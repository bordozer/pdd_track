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
            var data = _.extend({}, jmodel.timelineDay, {});
            this.$el.html(template(data));
            return this;
        }
    });
});
