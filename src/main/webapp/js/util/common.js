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