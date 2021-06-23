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
package org.oscarehr.ws.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Custom validator to allow strings of only specific values through validation
 */
public class StringValueValidator implements ConstraintValidator<StringValueConstraint, String>
{

	private Set<String> allowedValues;

	@Override
	public void initialize(StringValueConstraint stringValueConstraint)
	{
		allowedValues = Arrays.stream(stringValueConstraint.allows()).collect(toSet());
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext)
	{
		//TODO-legacy set custom validation error message
//		String invalidMessage = "Invalid String value. Allowed values: [" + String.join(",", allowedValues) + "]";
		return (value == null || allowedValues.contains(value));
	}
}
