'use strict';

if (!window.Oscar) window.Oscar = {};

Oscar.Util = {};

// single digit formatters will work with either one or two digits
Oscar.Util.DateFormat = "YYYY-M-D";
Oscar.Util.DateFormatAlt = "YYYY/M/D";

/**
 * Returns true if the given string represents a valid date.
 *
 * @param {string} dateStr   The string to check
 * @param {boolean} strict   If true, date is considered valid only if it is in the default
 * format. If false, the alternate format is considered valid too.
 *
 * @returns {boolean}
 */
Oscar.Util.validateDateString = function validateDateString(dateStr, strict)
{
	var validFormats = strict ? [this.DateFormat] : [this.DateFormat, this.DateFormatAlt];

	// enforce strict parsing to match against the given formats exactly
	return moment(dateStr, validFormats, true).isValid();
}

/**
 * Normalizes a date input value to use the default format delimiters.
 * The input value is assumed to already be in the correct format.
 *
 * @param {string} name   Name of the input to normalize (there should only be one input with
 * this name)
 */
Oscar.Util.normalizeDateInput = function normalizeDateInput(name)
{
	var dateElem = document.getElementsByName(name)[0];

	// If the value is not a recognized RFC2822 or ISO format, date construction
	// falls back to js Date(). To avoid this, we provide format to parse from.
	// Since we are not enforcing strict parsing, delimiters don't need to match.
	var date = moment(dateElem.value.trim(), this.DateFormat);
	dateElem.value = date.format(this.DateFormat);
}

/**
 * Returns true if the input value represents a valid date, and false otherwise.
 * Displays error if invalid.
 *
 * @param {string} name   Name of input to validate (there should only be one input with this name)
 * @param {boolean} [strict=true]   If false, the date will be considered valid even if delimiters
 * don't match (format still has to be correct). True by default.
 *
 * @returns {boolean}
 */
Oscar.Util.validateDateInput = function validateDateInput(name, strict)
{
	strict = strict !== false;
	var dateElem = document.getElementsByName(name)[0];
	var dateStr = dateElem.value.trim();

	var isValid = this.validateDateString(dateStr, strict);

	if (!isValid)
	{
		// todo: this should be coming from a key:value bean defined in properties!
		alert("Invalid date.");
		dateElem.focus();
	}

	return isValid;
}

/**
 * Returns true if the input value represents a valid date.
 *
 * Empty date is considered valid.
 * Date formats using different delimiters than the default are allowed. If valid, updates value
 * to use default delimiters.
 *
 * @param {string} name   Name of input to validate (there should only be one input with this name)
 *
 * @returns {boolean}
 */
Oscar.Util.validateDateInputTolerant = function validateDateInputTolerant(name)
{
	var dateStr = document.getElementsByName(name)[0].value;

	if (dateStr.trim() === '')
	{
		return true;
	}

	var isValid = this.validateDateInput(name, false);

	if (isValid)
	{
		this.normalizeDateInput(name);
	}

	return isValid;
}
