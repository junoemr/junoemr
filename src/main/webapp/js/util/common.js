'use strict';

if (!window.Oscar) window.Oscar = {};
if (!Oscar.Util) Oscar.Util = {};

Oscar.Util.Common = {};

/**
 * Returns true if the input value is a valid number.
 *
 * @param {object} elem   The DOM element to validate.
 * @returns {boolean}
 */
Oscar.Util.Common.validateNumberInput = function validateNumberInput(elem)
{
	return !isNaN(elem.value);
};

/**
 * Returns true if the input value is not empty.
 *
 * @param {object} elem   The DOM element to validate.
 * @returns {boolean}
 */
Oscar.Util.Common.validateInputNotEmpty = function validateInputNotEmpty(elem)
{
	return elem.value.length !== 0;
};

/**
 * Javascript doesn't count the '\r' character by default when you do a .length.
 * It doesn't recognize them at all and the vanilla language believes the characters aren't there.
 *
 * However, the HTTP specification insists that new lines are represented by '\r\n'
 * so we need to consider those characters too when we ask for length.
 * The only real way to consider this is to double-count the number of '\n' characters
 * and add it on top of whatever the regular length is.
 *
 * @param elem element to check length of (incl. carriage returns)
 * @return {integer} usual .length from JS + the number of '\n' occurrences
 */
Oscar.Util.Common.getLengthWithLineBreaks = function getLengthWithLineBreaks(elem)
{
	var elemLength = elem.value.length;
	var numCarriageReturns = elem.value.split(/\n/).length - 1;

	return elemLength + numCarriageReturns;
};
