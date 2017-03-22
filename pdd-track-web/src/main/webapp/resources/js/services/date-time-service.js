define(function (require) {

    'use strict';

    var DATE_FORMAT = 'YYYY-MM-DD';
    var TIME_FORMAT = 'HH:mm';

    var DATE_DISPLAY_FORMAT = 'D MMM YYYY';
    var TIME_DISPLAY_FORMAT = 'HH:mm';

    var LOCALE = 'en';

    var moment = require('moment');

    return {

        formatDateFullDisplay: function (time) {
            return moment(this.parseDate(time)).format('D MMM YYYY');
        },

        formatDateDayOfWeek: function (time) {
            return moment(this.parseDate(time)).format('dddd');
        },

        parseDate: function (time) {
            return moment(time, this.getDateTimeFormat()).toDate();
        },

        getDateTimeFormat: function () {
            return DATE_FORMAT + ' ' + TIME_FORMAT;
        },

        fromNow: function (time) {
            return moment.duration(moment(time, this.getDateTimeFormat()).diff(moment(new Date(), this.getDateTimeFormat()))).humanize({
                suffix: true,
                precise: true
            });
        }
    }
});
