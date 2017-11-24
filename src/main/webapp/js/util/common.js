'use strict';

if (!window.Oscar) window.Oscar = {};
if (!Oscar.Util) Oscar.Util = {};

Oscar.Util.Common = {};

/**
 * Returns true if the value of the input is a valid number.
 *
 * @param {string} name   Name of the input to validate (there should only be one
 * input with this name)
 * @returns {boolean}
 */
Oscar.Util.Common.validateNumberInput = function validateNumberInput(name)
{
	var elem = document.getElementsByName(name)[0];
	return !isNaN(elem.value.trim());
};