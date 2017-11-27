'use strict';

if (!window.Oscar) window.Oscar = {};
if (!Oscar.Util) Oscar.Util = {};

Oscar.Util.Date = {};

// single digit formatters will work with either one or two digits
Oscar.Util.Date.DateFormat = "YYYY-M-D";
Oscar.Util.Date.DateFormatAlt = "YYYY/M/D";

/**
 * Normalizes a date input value to use the default format delimiters.
 * The input value is assumed to already be in the correct format.
 *
 * @param {object} elem   The DOM element to normalize.
 */
Oscar.Util.Date.normalizeDateInput = function normalizeDateInput(elem)
{
	// If the value is not a recognized RFC2822 or ISO format, date construction
	// falls back to js Date(). To avoid this, we provide format to parse from.
	// Since we are not enforcing strict parsing, delimiters don't need to match.
	var date = moment(elem.value.trim(), this.DateFormat);
	elem.value = date.format(this.DateFormat);
};

/**
 * Returns true if the input value represents a valid date, and false otherwise.
 * Displays error if invalid.
 *
 * @param {object} elem   The DOM element to validate.
 * @param {boolean} [strict=true]   If false, the date will be considered valid even if delimiters
 * don't match (format still has to be correct). True by default.
 *
 * @returns {boolean}
 */
Oscar.Util.Date.validateDateInput = function validateDateInput(elem, strict)
{
	strict = strict !== false;
	var validFormats = strict ? [this.DateFormat] : [this.DateFormat, this.DateFormatAlt];
	var dateStr = elem.value.trim();

	// enforce strict parsing to match against the given formats exactly
	return moment(dateStr, validFormats, true).isValid();
};

/**
 * Returns true if the input value represents a valid date.
 *
 * Empty date is considered valid.
 * Date formats using different delimiters than the default are allowed. If valid, updates value
 * to use default delimiters.
 *
 * @param {object} elem   The DOM element to validate.
 *
 * @returns {boolean}
 */
Oscar.Util.Date.validateDateInputTolerant = function validateDateInputTolerant(elem)
{
	if (elem.value.trim() === '')
	{
		return true;
	}

	var isValid = this.validateDateInput(elem, false);

	if (isValid)
	{
		this.normalizeDateInput(elem);
	}

	return isValid;
};
