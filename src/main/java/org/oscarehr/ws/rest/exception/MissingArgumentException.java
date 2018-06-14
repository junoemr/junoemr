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
package org.oscarehr.ws.rest.exception;

import java.util.HashMap;
import java.util.Map;

public class MissingArgumentException extends RuntimeException
{
	private Map<String,String> missingArgumentMap;

	public MissingArgumentException()
	{
		this(null, new HashMap<>());
	}
	public MissingArgumentException(String message)
	{
		this(message, new HashMap<>());
	}
	public MissingArgumentException(String message, String argument, String reason)
	{
		this(message, new HashMap<>());
		addMissingArgument(argument, reason);
	}

	public MissingArgumentException(Map<String,String> missingArgumentMap)
	{
		this(null, missingArgumentMap);
	}
	public MissingArgumentException(String message, Map<String,String> missingArgumentMap)
	{
		super(message);
		this.missingArgumentMap = missingArgumentMap;
	}

	public void addMissingArgument(String argument, String reason)
	{
		missingArgumentMap.put(argument, reason);
	}

	public Map<String, String> getMissingArgumentMap()
	{
		return missingArgumentMap;
	}

	public void setMissingArgumentMap(Map<String, String> missingArgumentMap)
	{
		this.missingArgumentMap = missingArgumentMap;
	}
}
