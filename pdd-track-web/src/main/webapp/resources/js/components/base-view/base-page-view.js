define(function (require) {

    'use strict';

    var Backbone = require('backbone');
    var _ = require('underscore');
    var $ = require('jquery');

    var template = _.template(require('text!./templates/base-page-template.html'));

    return Backbone.View.extend({

        constructor: function (options) {
            this.events = _.extend(this.builtinEvents, this.events);

            this.options = options.options;

            this.breadcrumbs = options.breadcrumbs;
            this.bodyRenderer = options.bodyRenderer;

            Backbone.View.apply(this, [options]);
        },

        render: function () {
            this.$el.html(template({}));
            this.renderBody();
            return this;
        },

        renderBody: function () {
            this.bodyView = this.bodyRenderer(this.$('.js-body-view-container'), this.options).view();
        }
    });
});
