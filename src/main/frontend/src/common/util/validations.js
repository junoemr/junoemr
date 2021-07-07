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
		let value = Juno.Validations.getAttribute(obj, field);
		if (!value)
		{
			return false;
		}
		if (typeof (value) === "string" && value.length === 0)
		{
			return false;
		}

		return Juno.Validations.validationFieldsChain(...validationFunc);
	}
};

Juno.Validations.validationFieldBlank = function(obj, field, ...validationFunc)
{
	return function validationFunction ()
	{
		let value = Juno.Validations.getAttribute(obj, field);
		let fieldBlank = false;
		if (!value)
		{
			 fieldBlank = true;
		}
		if (typeof (value) === "string" && value.length === 0)
		{
			fieldBlank = true;
		}

		return fieldBlank && Juno.Validations.validationFieldsChain(...validationFunc);
	}
}
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
		let value = Juno.Validations.getAttribute(obj, field);
		if (!value)
		{
			return true;
		}
		if (typeof (value) === "string" && value.length === 0)
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
		let value = Juno.Validations.getAttribute(obj, field);
		if (!value)
		{
			return false;
		}
		if (isNaN(value))
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
		let value0 = Juno.Validations.getAttribute(obj0, field0);
		let value1 = Juno.Validations.getAttribute(obj1, field1);
		if (value0 !== value1)
		{
			return false;
		}

		return Juno.Validations.validationFieldsChain(...validationFunc);
	}
};

// validates that the provided field is truthy
Juno.Validations.validationFieldTrue = function(obj, field, ...validationFunc)
{
	return Juno.Validations.validationCustom(() => !!Juno.Validations.getAttribute(obj, field), ...validationFunc);
}

/**
 * get the value of the attribute from object, denoted by attributeString
 * @param obj - the object containing the attribute you wish to access
 * @param attributeString - a string indicating the attribute like "attr" or "foo.bar"
 */
Juno.Validations.getAttribute = (obj, attributeString) =>
{
	let currObj = obj;
	let splitAttributes = attributeString.split(".");
	for (let attr of splitAttributes)
	{
		currObj = currObj[attr];
		if (currObj === undefined)
		{
			break;
		}
	}
	return currObj;
}

Juno.Validations.validationCustom = function (customValidationFunc, ...validationFunc)
{
	return () =>
	{
		return Juno.Validations.validationFieldsChain(...[customValidationFunc, ...validationFunc]);
	}
}

Juno.Validations.validationPassword = function (obj, field, ...validationFunc)
{
	return function validationFunction ()
	{
		let value = Juno.Validations.getAttribute(obj, field);
		if (!value ||
				value.length < 8 ||
				!value.match(".*[^a-zA-Z].*")// must contain a number
		)
		{
			return false;
		}

		return Juno.Validations.validationFieldsChain(...validationFunc);
	}
};

Juno.Validations.validationEmail = function (obj, field, ...validationFunc)
{
	return function validationFunction ()
	{
		let value = Juno.Validations.getAttribute(obj, field);
		// valid if undefined, blank or matches regex
		if (value && (value === "" || !value.match(/^[^@ ]+@([A-z0-9-]+\.)+[A-z0-9-]+$/)))
		{
			return false;
		}

		return Juno.Validations.validationFieldsChain(...validationFunc);
	}
};

Juno.Validations.validationPhone = function (obj, field, ...validationFunc)
{
	return function validationFunction ()
	{
		let value = Juno.Validations.getAttribute(obj, field);
		// check phone number
		if (value && value != "" && value.match(/[^\d-()\s,]/))
		{
			return false;
		}

		return Juno.Validations.validationFieldsChain(...validationFunc);
	}
};

Juno.Validations.PartialDate =
{
	validationYear: function (obj, year, ...validationFunc)
	{
		return function validationFunction ()
		{
			let yearValue = Juno.Validations.getAttribute(obj, year);

			if (!yearValue)
			{
				return false;
			}

			let currentMoment = moment({year: yearValue, month: 1, day: 1});

			if (!currentMoment.isValid() || yearValue < 1900)
			{
				return false;
			}

			return Juno.Validations.validationFieldsChain(...validationFunc);
		}
	},

	validationMonth: function (obj, month, ...validationFunc)
	{
		return function validationFunction ()
		{
			let monthValue = Juno.Validations.getAttribute(obj, month);

			if (!monthValue || monthValue === 0 || monthValue === "0")
			{
				return false;
			}

			if (typeof monthValue !== "number")
			{
				monthValue = parseInt(monthValue);
				if (Number.isNaN(monthValue))
				{
					return false;
				}
			}
			monthValue -= 1; // Moment uses 0 indexed months

			let currentMoment = moment({year: 1900, month: monthValue, day: 1});

			if (!currentMoment.isValid())
			{
				return false;
			}

			return Juno.Validations.validationFieldsChain(...validationFunc);
		}
	},

	validationDay: function (obj, year, month, day, ...validationFunc)
	{
		return function validationFunction ()
		{
			let yearValue = Juno.Validations.getAttribute(obj, year);
			let monthValue = Juno.Validations.getAttribute(obj, month);
			let dayValue = Juno.Validations.getAttribute(obj, day);

			if (!dayValue)
			{
				return false
			}

			if (!yearValue)
			{
				yearValue = 1900;
			}

			if (monthValue && monthValue > 0 && monthValue < 13)
			{
				monthValue -= 1; // Moment uses 0 indexed months
			}
			else
			{
				monthValue = 0;
			}

			dayValue = parseInt(dayValue.toString());

			let currentMoment = moment({year: yearValue, month: monthValue, day: dayValue});

			if (!currentMoment.isValid())
			{
				return false;
			}

			return Juno.Validations.validationFieldsChain(...validationFunc);
		}
	}
};