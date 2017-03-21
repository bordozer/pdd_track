define(function (require) {

    'use strict';

    var _ = require('underscore');
    var $ = require('jquery');

    var userPageTemplate = _.template(require('text!./templates/user-base-page-template.html'));

    var PageView = require('js/components/base-view/base-page-view');

    return PageView.extend({

        renderBody: function () {
            this.$('.js-body-view-container').html(userPageTemplate());
            this.bodyView = this.bodyRenderer(this.$('.js-custom-view'), this.options).view();
        }
    });
});
