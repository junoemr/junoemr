'use strict';

if (!window.Juno) window.Juno = {};


if (!Juno.Common) Juno.Common = {};

Juno.Common.Util = {};

Juno.Common.Util.settings = {
	datetime_format: "YYYY-MM-DDTHH:mm:ssZ",
	datetime_no_timezone_format: "YYYY-MM-DDTHH:mm:ss",
	time_format: "h:mma",
	date_format: "YYYY-MM-DD",
	dayofweek_format: "dddd"
};

Juno.Common.Util.DisplaySettings = {
	dateFormat: "yyyy-MM-dd",
	timeFormat: "HH:mm a",
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
Juno.Common.Util.parseBoolean = function (str)
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

Juno.Common.Util.formatMomentDateTimeNoTimezone = function formatMomentDateTimeNoTimezone(d) {
	return d.format(Juno.Common.Util.settings.datetime_no_timezone_format);
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

Juno.Common.Util.getUserISOTimezoneOffset = function ()
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
	let dateOfBirth = moment({year: dobYear, month: dobMonth, day: dobDay});
	let currDate = moment();
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
	var parsed_string = parseInt(string);

	if (/^-?\d+$/.test(string.toString()))
	{
		return true;
	}

	return false;
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

// create a promise that resolves when the provided window is closed
Juno.Common.Util.windowClosedPromise = function (popup)
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