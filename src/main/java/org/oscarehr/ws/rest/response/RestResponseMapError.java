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
package org.oscarehr.ws.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

@Schema(description = "Response wrapper object for error information")
public class RestResponseMapError extends RestResponseError
{
	private Map<String, String> infoMap;

	public RestResponseMapError()
	{
		this(null);
	}
	public RestResponseMapError(String message)
	{
		super(message);
		infoMap = new HashMap<>();
	}

	public void put(String key, String value)
	{
		infoMap.put(key, value);
	}

	public void setInfoMap(Map<String, String> infoMap)
	{
		this.infoMap = infoMap;
	}

	public Map<String, String> getInfoMap()
	{
		return infoMap;
	}
}
