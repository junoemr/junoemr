'use strict';

if (!window.Oscar) window.Oscar = {};
if (!Oscar.Util) Oscar.Util = {};

Oscar.Util.Appointment = {};

Oscar.Util.Appointment.TimeFormats = ["H:m", "Hmm", "H"];
Oscar.Util.Appointment.TimeFormatDisplay = "HH:mm";

/**
 * If the input value represents a valid time, normalizes it to the display format,
 * and returns true. Otherwise returns false.
 *
 * @param {string} name  Name of input to validate (there should only be one input with this name)
 *
 * @returns {boolean}
 */

Oscar.Util.Appointment.validateTimeInput = function validateTimeInput(name)
{
	var timeElem = document.getElementsByName(name)[0];

	var timeStr = timeElem.value.trim();
	var time = moment(timeStr, this.TimeFormats, true);

	if (!time.isValid())
	{
		timeElem.focus();
		return false;
	}

	timeElem.value = time.format(this.TimeFormatDisplay);
	return true;
};

/**
 * Given a start time and duration, calculates the end time and sets it as the value of the given
 * input using the H:m format.
 * If the calculated time wraps around to the next day, fails and returns false.
 *
 * @param {string} name   Name of input to validate (there should only be one input with this name)
 * @param {string} start   The start time to use, in the H:m format.
 * @param {string} duration   The duration to use, in minutes.
 *
 * @returns {boolean}
 */
Oscar.Util.Appointment.setEndTime = function setEndTime(name, start, duration)
{
	var endElem = document.getElementsByName(name)[0];

	// if duration is unparseable or 0, use 1
	var durationTime = parseInt(duration) || 1;
	var startTime = moment(start, this.TimeFormats);
	var endTime = startTime.clone();

	endTime.add(Math.abs(durationTime) - 1, 'minutes');
	if (endTime.diff(startTime, 'days') !== 0)
	{
		return false;
	}

	endElem.value = endTime.format(this.TimeFormatDisplay);
	return true;
};
