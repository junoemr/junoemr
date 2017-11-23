'use strict';

if (!window.Oscar) window.Oscar = {};
if (!Oscar.Util) Oscar.Util = {};

Oscar.Util.Appointment = {};

// single digit formatters will work with either one or two digits
Oscar.Util.Appointment.TimeFormat = "H:m";
Oscar.Util.Appointment.TimeFormatAlt = "Hm";

/**
 * Returns true if the input value represents a valid time, and false otherwise.
 *
 * @param {string} name   Name of input to validate (there should only be one input with this name)
 *
 * @returns {boolean}
 */
Oscar.Util.Appointment.validateStartTime = function validateStartTime(name)
{
	var startElem = document.getElementsByName(name)[0];
	return moment(startElem.value, [this.TimeFormat, this.TimeFormatAlt], true).isValid();
}

/**
 * Given a start time and duration, calculates the end time and sets it as the value of the given
 * input using the H:m format.
 * If the calculated time wraps around to the next day, fails and returns false.
 *
 * @param {string} name   Name of input to validate (there should only be one input with this name)
 * @param {string} startTime   The start time to use, in the H:m format.
 * @param {string} duration   The duration to use, in minutes.
 *
 * @returns {boolean}
 */
Oscar.Util.Appointment.setEndTime = function setEndTime(name, startTime, duration)
{
	var endElem = document.getElementsByName(name)[0];

	// if duration is unparseable or 0, use 1
	var duration = parseInt(duration) || 1;

	// moments are mutable; clone so we don't alter the original
	var startTime = moment(startTime, this.TimeFormat);
	var endTime = startTime.clone();

	endTime.add(Math.abs(duration) - 1, 'minutes');
	if (startTime.diff(endTime, 'days') > 0)
	{
		return false;
	}

	endElem.value = endTime.format(this.TimeFormat);
	return true;
}
