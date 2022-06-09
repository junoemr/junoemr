'use strict';

import {JUNO_ALERT_MODES} from "../modals/junoAlert/junoAlertConstants";
import {JUNO_INPUT_MODAL_TYPE} from "../components/junoComponentConstants";

if (!window.Juno) window.Juno = {};


if (!Juno.Common) Juno.Common = {};

Juno.Common.Util = {};

Juno.Common.Util.settings = {
	datetime_format: "YYYY-MM-DDTHH:mm:ssZ",
	datetime_no_timezone_format: "YYYY-MM-DDTHH:mm:ss",
	time_format: "h:mma",
	date_format: "YYYY-MM-DD",
	dayofweek_format: "dddd",
	month_name_day_year: "LL",
	message_date_format: "h:mm A LL",
	message_date_long_format: "h:mm A MMM DD, YYYY",
	message_date_short_format: "h:mm A MMM DD",

	defaultTimeFormat: "HH:mm:ss"
};

Juno.Common.Util.DisplaySettings = {
	dateFormat: "yyyy-MM-dd",
	timeFormat: "HH:mm a",
	calendarDateFormat: 'dddd MMM Do',
	dateTimeFormat: "YYYY-MM-DD hh:mm a",
};

Juno.Common.Util.exists = function exists(object) {
	// not undefined and not null
	return angular.isDefined(object) && object !== null;
};

Juno.Common.Util.isBlank = function isBlank(object) {
	// undefined or null or empty string
	return !Juno.Common.Util.exists(object) || object === "";
};

// convert a string in to a boolean
Juno.Common.Util.parseBoolean = function parseBoolean(str)
{
	let trueValues = ["on", "yes", "true", "enabled"];
	let falseValues = ["off", "no", "false", "disabled"];

	if (str !== null && trueValues.includes(str))
	{
		return true
	}
	else if (str === null || falseValues.includes(str))
	{
		return false
	}
	else
	{
		throw "Invalid Argument, [" + str + "] not one of, " + trueValues + ", " + falseValues;
	}
};

Juno.Common.Util.toArray = function toArray(obj) { //convert single object to array
	if (obj instanceof Array) return obj;
	else if (obj == null) return [];
	else return [obj];
};

Juno.Common.Util.pad0 = function pad0(n) {
	var s = n.toString();
	if (s.length == 1) s = "0" + s;
	return s;
};

// if the date is a single digit add a zero in front. if it is 3 or more and
// has a leading zero remove it.
Juno.Common.Util.padDateWithZero = function padDateWithZero(dateNumber)
{
	let zeroPaddedDateString = Juno.Common.Util.pad0(dateNumber);
	if (zeroPaddedDateString.length > 2 && zeroPaddedDateString.charAt(0) === "0")
	{
		zeroPaddedDateString = zeroPaddedDateString.substring(1);
	}
	return zeroPaddedDateString;
};



Juno.Common.Util.toTrimmedString = function toTrimmedString(s) {
	if (s == null) s = "";
	if (s instanceof String) s = s.trim();
	return s;
};

Juno.Common.Util.formatDate = function formatDate(d) {
	d = Juno.Common.Util.toTrimmedString(d);
	if (d) {
		if (!(d instanceof Date)) d = new Date(d);
		d = d.getFullYear() + "-" + Juno.Common.Util.pad0(d.getMonth() + 1) + "-" + Juno.Common.Util.pad0(d.getDate());
	}
	return d;
};

Juno.Common.Util.formatTime = function formatTime(d) {
	d = Juno.Common.Util.toTrimmedString(d);
	if (d) {
		if (!(d instanceof Date)) d = new Date(d);
		d = Juno.Common.Util.pad0(d.getHours()) + ":" + Juno.Common.Util.pad0(d.getMinutes());
	}
	return d;
};

Juno.Common.Util.formatMomentDate = function formatMomentDate(d, format)
{
	if (!format)
	{
		format = Juno.Common.Util.settings.date_format;
	}
	return d.format(format);
};

Juno.Common.Util.formatMomentTime = function formatMomentTime(d, format) {
	if(!format) {
		format = Juno.Common.Util.settings.time_format;
	}
	return d.format(format);
};

Juno.Common.Util.formatMomentDateTime = function formatMomentDateTime(d, format) {
	if(!format) {
		format = Juno.Common.Util.settings.datetime_no_timezone_format;
	}
	return d.format(format);
};

Juno.Common.Util.formatMomentDateTimeNoTimezone = function formatMomentDateTimeNoTimezone(d)
{
	return d.format(Juno.Common.Util.settings.datetime_no_timezone_format);
};

Juno.Common.Util.formatZonedMomentDateTime = function formatZonedMomentDateTime(d)
{
	return d.format(Juno.Common.Util.settings.datetime_format);
};


Juno.Common.Util.getDateMoment = function getDateMoment(date_string)
{
	return moment.utc(date_string, Juno.Common.Util.settings.date_format, true);
};

Juno.Common.Util.getTimeMoment = function getTimeMoment(time_string)
{
	return moment.utc(time_string, Juno.Common.Util.settings.time_format, true);
};

Juno.Common.Util.getDateMomentFromComponents = function getDateMomentFromComponents(year_string, month_string, day_string)
{
	return moment.utc({year: year_string, month: (Number(month_string)-1), day: day_string});
};

Juno.Common.Util.getDateAndTimeMoment = function getCombinedMoment(dateString, timeString)
{
	return moment.utc(dateString + " " + timeString,
		Juno.Common.Util.settings.date_format + " " +
		Juno.Common.Util.settings.time_format, true);
};

Juno.Common.Util.getDatetimeNoTimezoneMoment = function getDatetimeNoTimezoneMoment(datetime_string)
{
	return moment.utc(datetime_string,
		Juno.Common.Util.settings.datetime_no_timezone_format, true);
};

Juno.Common.Util.getUserISOTimezoneOffset = function getUserISOTimezoneOffset()
{
	let sign = "-";
	let offsetRaw = (new Date()).getTimezoneOffset();
	if (offsetRaw < 0)
	{
		sign = "+";
		offsetRaw *= -1;
	}
	let offsetHour = offsetRaw / 60;
	let offsetMin = offsetRaw % 60;
	return sign + offsetHour.toString().padStart(2,"0") + ":" + offsetMin.toString().padStart(2, "0");
};

Juno.Common.Util.validateDateString = function validateDateString(
	dateString, displayErrors, field, fieldDisplayName, required)
{
	if(Juno.Common.Util.exists(dateString))
	{
		var moment = Juno.Common.Util.getDateMoment(dateString);
		if(!moment.isValid())
		{
			displayErrors.add_field_error(field, fieldDisplayName + ' is invalid');
		}
	}
	else if(required)
	{
		displayErrors.add_field_error(field, fieldDisplayName + 'is required');
	}
};

Juno.Common.Util.validateTimeString = function validateTimeString(
	timeString, displayErrors, field, fieldDisplayName, required)
{
	if (Juno.Common.Util.exists(timeString))
	{
		var moment = Juno.Common.Util.getTimeMoment(timeString);
		if (!moment.isValid())
		{
			displayErrors.add_field_error(field, fieldDisplayName + 'is invalid');
		}
	}
	else if (required)
	{
		displayErrors.add_field_error(field, fieldDisplayName + 'is required');
	}
};
Juno.Common.Util.validateIntegerString = function validateInputString(
	inputString, displayErrors, field, fieldDisplayName, required, nonNegative, nonZero)
{
	if (!Juno.Common.Util.isBlank(inputString))
	{
		if (!Juno.Common.Util.isIntegerString(inputString) ||
			(nonNegative && Number(inputString) < 0) ||
			(nonZero && Number(inputString) === 0))
		{
			displayErrors.add_field_error(field, fieldDisplayName + 'is invalid');
		}
	}
	else if (required)
	{
		displayErrors.add_field_error(field, fieldDisplayName + 'is required');
	}
};

Juno.Common.Util.addNewLine = function addNewLine(line, mssg) {
	if (line == null || line.trim() == "") return mssg;

	if (mssg == null || mssg.trim() == "") mssg = line.trim();
	else mssg += "\n" + line.trim();

	return mssg;
};

Juno.Common.Util.calcAge = function calcAge(dobYear, dobMonth, dobDay)
{
	//the month of this moment method is 0 indexed
	var dateOfBirth = moment({year: dobYear, month: dobMonth - 1, day: dobDay});
	var currDate = moment();
	return currDate.diff(dateOfBirth, 'years');

};

Juno.Common.Util.isInArray = function isInArray(value, array) 
{
	return array.indexOf(value) > -1;
};

/**
 * recursively merges two js hashes. the baseHash will be modified
 * @param baseHash
 * @param toMerge
 */
Juno.Common.Util.mergeHash = function mergeHash(baseHash, toMerge)
{
	if (toMerge === 'undefined') {
		return false;
	}
	$.extend(true, baseHash, toMerge);
	return true;
};

Juno.Common.Util.noNull = function noNull(val)
{
    if (typeof val === 'string')
    {
        val =  val.trim();
    }
    else if ( val === null)
    {
		val = "";
    }

    return val;
};

Juno.Common.Util.isUndefinedOrNull = function isUndefinedOrNull(val)
{
	return angular.isUndefined(val) || val === null;
};

Juno.Common.Util.isDefinedAndNotNull = function isDefinedAndNotNull(val)
{
	return (angular.isDefined(val) && val !== null);
};

Juno.Common.Util.objectArrayIndexOf = function objectArrayIndexOf(array, searchTerm, property)
{
	for(var i = 0, len = array.length; i < len; i++) {
		if (array[i][property] === searchTerm) return i;
	}
	return -1;
};

Juno.Common.Util.isIntegerString = function isIntegerString(string)
{
	if (string === undefined || string === null)
	{
		return false;
	}
	return /^-?\d+$/.test(string.toString());
};

Juno.Common.Util.isNumber = function isNumber(object)
{
	return typeof object === "number";
};

Juno.Common.Util.escapeHtml = function escapeHtml(str)
{
	var entityMap = {
		"&": "&amp;",
		"<": "&lt;",
		">": "&gt;",
		'"': '&quot;',
		"'": '&#39;',
		"/": '&#x2F;'
	};

	return String(str).replace(/[&<>"'\/]/g, function (s)
	{
		return entityMap[s];
	});
};

Juno.Common.Util.formatName = function formatName(firstName, lastName)
{
	if(!lastName && !firstName)
	{
		return null;
	}
	else if(!firstName)
	{
		return lastName;
	}
	else if(!lastName)
	{
		return firstName;
	}

	return lastName + ', ' + firstName;
};

Juno.Common.Util.trimToLength = function trimToLength(string, maxLength)
{
	var shortString = string;
	if(shortString.length > maxLength)
	{
		shortString = shortString.substring(0, maxLength);
	}
	return shortString;
};

Juno.Common.Util.trimToNull = function trimToNull(str)
{
	str.trim();
	if (str === "")
	{
		return null;
	}
	return str;
};

// create a promise that resolves when the provided window is closed
Juno.Common.Util.windowClosedPromise = function windowClosedPromise(popup)
{
	return new Promise(function (resolve, reject)
	{
		let interId = window.setInterval(function()
		{
			if (popup.closed)
			{
				resolve(true);
				window.clearInterval(interId);
			}
		}, 500);
	});
};

// show a success alert box similar to the browsers built in alert functionality
Juno.Common.Util.successAlert = function successAlert(uibModal, title, message)
{
	uibModal.open(
		{
			component: 'junoAlertComponent',
			backdrop: 'static',
			windowClass: "juno-alert",
			resolve: {
				title: function(){return title},
				message: function(){return message},
				mode: function(){return JUNO_ALERT_MODES.SUCCESS}
			}
		}
	);
};

// show a error alert box similar to the browsers built in alert functionality
Juno.Common.Util.errorAlert = function errorAlert(uibModal, title, message)
{
	uibModal.open(
			{
				component: 'junoAlertComponent',
				backdrop: 'static',
				windowClass: "juno-alert",
				resolve: {
					title: function(){return title},
					message: function(){return message},
					mode: function(){return JUNO_ALERT_MODES.ERROR}
				}
			}
	);
};

// show a confirmation box. returns a promise that will resolve to true / false based on user selection.
Juno.Common.Util.confirmationDialog = function confirmationDialog(uibModal, title, message, style)
{
	return uibModal.open(
			{
				component: 'junoAlertComponent',
				backdrop: 'static',
				windowClass: "juno-alert",
				resolve: {
					title: function(){return title},
					message: function(){return message},
					mode: function(){return JUNO_ALERT_MODES.CONFIRM},
					style: () => style,
				}
			}
	).result;
};

/**
 * display a input modal
 * @param uibModal - uibModal instance
 * @param title - title of the modal.
 * @param message - message to display to the user
 * @param style - the style of the modal
 * @param okText - alternate text for the ok button. omit if you want default ("Ok").
 * @param placeholder - the input placeholder text
 * @param characterLimit - limit the number of characters that can be entered in to the input box. omit for unlimited.
 * @returns {*} - user selection
 */
Juno.Common.Util.openInputDialog = function openInputDialog(uibModal, title, message, style, okText, placeholder, characterLimit)
{
	return uibModal.open(
			{
				component: 'junoInputModal',
				backdrop: 'static',
				windowClass: "juno-simple-modal-window",
				resolve: {
					title: () => title,
					message: () => message,
					style: () => style,
					okText: () => okText,
					placeholder: () => placeholder,
					characterLimit: () => characterLimit,
				}
			}
	).result;
};

/**
 * display a select dialog to the user
 * @param uibModal - the uib modal instance
 * @param title - title of the modal
 * @param message - message inside the modal
 * @param options - the select menu options
 * @param style - style of the modal
 * @param okText - the text to display on the "ok" button. Leave blank for "Ok"
 * @param placeholder - the input placeholder text
 * @returns {*} - user selection
 */
Juno.Common.Util.openSelectDialog = function openSelectDialog(uibModal, title, message, options, style, okText, placeholder)
{
	return uibModal.open(
			{
				component: 'junoInputModal',
				backdrop: 'static',
				windowClass: "juno-simple-modal-window",
				resolve: {
					title: () => title,
					message: () => message,
					style: () => style,
					okText: () => okText,
					placeholder: () => placeholder,
					options: () => options,
					type: () => JUNO_INPUT_MODAL_TYPE.SELECT,
				}
			}
	).result;
};

/**
 * display a typeahead dialog to the user
 * @param uibModal - the uib modal instance
 * @param title - title of the modal
 * @param message - message inside the modal
 * @param options - an array of predefined options, or the callback function to call when searching for typeahead results
 * @param style - style of the modal
 * @param okText - the text to display on the "ok" button. Leave blank for "Ok"
 * @param placeholder - the input placeholder text
 * @param typeaheadMinLength - the minimum typeahead search length
 * @returns {*} - user selection
 */
Juno.Common.Util.openTypeaheadDialog = function openTypeaheadDialog(uibModal, title, message, options, style, okText, placeholder, typeaheadMinLength)
{
	return uibModal.open(
		{
			component: 'junoInputModal',
			backdrop: 'static',
			windowClass: "juno-simple-modal-window",
			resolve: {
				title: () => title,
				message: () => message,
				style: () => style,
				okText: () => okText,
				placeholder: () => placeholder,
				options: () => options,
				type: () => JUNO_INPUT_MODAL_TYPE.TYPEAHEAD,
				typeaheadMinLength: () => typeaheadMinLength,
			}
		}
	).result;
};

Juno.Common.Util.showProgressBar = function showProgressBar($uibModal, $q, deferral, title, style)
{
	let deferred = $q.defer();

	$uibModal.open(
		{
			component: 'junoProgressModalComponent',
			backdrop: 'static',
			windowClass: "juno-progress-modal",
			resolve: {
				title: () => title,
				deferral: () => deferral,
				style: () => style,
			}
		}
	).result.then((result) =>
	{
		deferred.resolve(result);
	}).catch((reason) =>
	{
		deferred.reject(reason);
	});
	return deferred.promise;
};

/**
 * open a telehealth window for the specified appointment
 * @param demographicNo - the demographic who the appointment is for
 * @param appointmentNo - the appointmentNo
 * @param site - the site of the appointment or null
 */
Juno.Common.Util.openTelehealthWindow = function openTelehealthWindow(demographicNo, appointmentNo, site)
{
	window.open("../integrations/myhealthaccess.do?method=connect"
			            + "&demographicNo=" + encodeURIComponent(demographicNo)
			            + "&siteName=" + encodeURIComponent(site)
			            + "&appt=" + encodeURIComponent(appointmentNo), "_blank");
};

/**
 * lookup typeahead object form options list based on value
 * @param value - the value to look up
 * @param options - the options list from which to lookup the object
 * @returns - the matching typeahead object or the value if no match found.
 */
Juno.Common.Util.typeaheadValueLookup = function typeaheadValueLookup(value, options)
{
	if (value && options && options.length > 0)
	{
		let res = options.find((el) => el.value === value);
		if (res)
		{
			return res;
		}
	}

	return value;
};

/**
 * returns the name of the day, given the ISO weekday index 1-7.
 * Where 1 = Sunday, and 7 = Saturday
 */
Juno.Common.Util.ISODayString = function ISODayString(weekday)
{
	switch (weekday)
	{
		case 1: return "Sunday";
		case 2: return "Monday";
		case 3: return "Tuesday";
		case 4: return "Wednesday";
		case 5: return "Thursday";
		case 6: return "Friday";
		case 7: return "Saturday";
		default: throw "Invalid Weekday index '" + weekday + "' (must be in range of 1-7)";
	}
}

/**
 * filter the provided array using the specified property such that only distinct elements remain
 * @param array - the array to filter
 * @param property - the property to filter on
 * @return array with all duplicates removed
 */
Juno.Common.Util.arrayDistinct = function arrayDistinct(array, property)
{
	const map = new Map();

	array.forEach((item) =>
	{
		if (!map.has(item[property]))
		{
			map.set(item[property], item);
		}
	})

	return Array.from(map.values());
}