define(function (require) {

    'use strict';

    var Backbone = require('backbone');
    var _ = require('underscore');
    var $ = require('jquery');

    var template = _.template(require('text!./templates/timeline-page-template.html'));

    return Backbone.View.extend({

        initialize: function (options) {
            this.categoryId = options.options.categoryId;

            this.model.on('sync', this.render, this);
            this.model.fetch({cache: false});
        },

        render: function () {
            //var data = _.extend({}, this.model.toJSON(), {});
            console.log(this.$el);
            this.$el.html(template());
            return this;
        }
    });
});