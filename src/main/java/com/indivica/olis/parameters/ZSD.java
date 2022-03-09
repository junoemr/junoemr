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

package com.indivica.olis.parameters;

import lombok.Getter;

import javax.validation.constraints.NotNull;

/**
 * query parameter to identify the substitute decision maker*
 */

@Getter
public class ZSD implements Parameter
{
	public enum RelationshipToPatient
	{
		A0,
		A1,
		A2,
		A3,
		A4,
		A5,
		A6,
		A7,
	}

	private String givenName;
	private String lastName;
	private RelationshipToPatient relationship;

	public ZSD()
	{
		this(null, null, RelationshipToPatient.A7);
	}
	public ZSD(String givenName, String lastName, @NotNull RelationshipToPatient relationship)
	{
	    this.lastName = lastName;
		this.givenName = givenName;
		this.relationship = relationship;
    }

	@Override
	public String toOlisString()
	{
		return getQueryCode() + ".1^" + getGivenName() + "~" +
				getQueryCode() + ".2^" + getLastName() + "~" +
				getQueryCode() + ".3^" + getRelationship().name();
	}

	@Override
	public void setValue(Object value)
	{
		if(value instanceof String)
		{
			this.givenName = (String) value;
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void setValue(Integer part, Object value)
	{
		if(part == 1)
		{
			this.givenName = (String) value;
		}
		else if (part == 2)
		{
			this.lastName = (String) value;
		}
		else if (part == 3)
		{
			this.relationship = (RelationshipToPatient) value;
		}
	}

	@Override
	public void setValue(Integer part, Integer part2, Object value)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getQueryCode()
	{
		return "@ZSD";
	}

}
