/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

// init juno validations object.
if (!Juno)
{
	Juno = {};
}
Juno.Validations = {};

/**
 * check if all validations in this validation object are valid
 * @param validationObj - the validation object to check
 * @returns {boolean} - true / false indicating weather all validations are valid.
 */
Juno.Validations.allValidationsValid = function(validationObj)
{
	for(let validation in validationObj)
	{
		if (Object.prototype.hasOwnProperty.call(validationObj, validation)) {
			if (!validationObj[validation]())
			{
				return false;
			}
		}
	}
	return true;
};

// generate a validation function. This is a nop validation and has no effect.
// validationFunc, is a optional validation function that will be chained with this one.
Juno.Validations.validationFieldNop = function(...validationFunc)
{
	return function validationFunction ()
	{
		return Juno.Validations.validationFieldsChain(...validationFunc);
	}
};

// generate a validation function that returns true if either of the validation functions returns true
Juno.Validations.validationFieldOr = function( validationFunc0, validationFunc1)
{
	return function validationFunction ()
	{
		return validationFunc0() || validationFunc1();
	}
};

Juno.Validations.validationFieldsChain = function(...validationFunctions)
{
	if (validationFunctions)
	{
		let res = true;
		for (let func of validationFunctions)
		{
			res = res && func();
		}
		return res;
	}
	else
	{
		return true;
	}
};


// generate a validation function that requires the field to be filled.
// validationFunc, is a optional validation function that will be chained with this one.
Juno.Validations.validationFieldRequired = function(obj, field, ...validationFunc)
{
	return function validationFunction ()
	{
		if (!obj[field])
		{
			return false;
		}
		if (typeof (obj[field]) === "string" && obj[field].length === 0)
		{
			return false;
		}

		return Juno.Validations.validationFieldsChain(...validationFunc);
	}
};

/**
 * valid if the field is (blank / undefined / null) or ( if validationFunction(s) is true)
 * @param obj - object to check
 * @param field - field to check
 * @param validationFunc - additional validations functions
 * @returns - true / false indicating validity
 */
Juno.Validations.validationFieldBlankOrOther = function(obj, field, ...validationFunc)
{
	return function validationFunction ()
	{
		if (!obj[field])
		{
			return true;
		}
		if (typeof (obj[field]) === "string" && obj[field].length === 0)
		{
			return true;
		}

		return Juno.Validations.validationFieldsChain(...validationFunc);
	}
};

// generate a validation function that requires the field to be a number
// validationFunc, is a optional validation function that will be chained with this one.
Juno.Validations.validationFieldNumber = function(obj, field, ...validationFunc)
{
	return function validationFunction ()
	{
		if (!obj[field])
		{
			return false;
		}
		if (isNaN(obj[field]))
		{
			return false;
		}

		return Juno.Validations.validationFieldsChain(...validationFunc);
	}
};

// generate a validation function that requires the field to be the same as another
// validationFunc, is a optional list of validation function that will be chained with this one.
Juno.Validations.validationFieldsEqual = function(obj0, field0, obj1, field1, ...validationFunc)
{
	return function validationFunction ()
	{
		if (obj0[field0] !== obj1[field1])
		{
			return false;
		}

		return Juno.Validations.validationFieldsChain(...validationFunc);
	}
};


Juno.Validations.validationPassword = function (obj, field, ...validationFunc)
{
	return function validationFunction ()
	{
		if (!obj[field] ||
				obj[field].length < 8 ||
				!obj[field].match(".*[^a-zA-Z].*")// must contain a number
		)
		{
			return false;
		}

		return Juno.Validations.validationFieldsChain(...validationFunc);
	}
};