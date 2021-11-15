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

package org.oscarehr.integration.aqs.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CommunicationType
{
	IN_PERSON("in-person"),
	VIDEO("video"),
	AUDIO("audio"),
	CHAT("chat");

	// ==========================================================================
	// Boilerplate
	// ==========================================================================
	public static CommunicationType fromString(String name)
	{
		for (CommunicationType type : CommunicationType.values())
		{
			if (type.name.equals(name))
			{
				return type;
			}
		}
		throw new IllegalArgumentException("CommunicationType has no enum value for [" + name + "]");
	}

	private final String name;

	CommunicationType(String name)
	{
		this.name = name;
	}

	@JsonValue
	public String getName()
	{
		return this.name;
	}

	@Override
	public String toString()
	{
		return this.getName();
	}
}
