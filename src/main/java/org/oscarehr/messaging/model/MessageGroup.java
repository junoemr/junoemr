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
package org.oscarehr.messaging.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MessageGroup
{
	ALL("all"),
	RECEIVED("received"),
	SENT("sent"),
	ARCHIVED("archived"),
	UNKNOWN("unknown");

	// ==========================================================================
	// Boilerplate
	// ==========================================================================

	public static MessageGroup fromString(String name)
	{
		for (MessageGroup group : MessageGroup.values())
		{
			if (group.name.equals(name))
			{
				return group;
			}
		}
		throw new IllegalArgumentException("MessageGroup has no enum value for [" + name + "]");
	}

	private final String name;

	MessageGroup(String name)
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
