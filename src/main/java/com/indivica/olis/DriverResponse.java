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
package com.indivica.olis;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

@Data
public class DriverResponse
{
	private String unsignedRequest;
	private String signedRequest;
	private String signedResponse;
	private String unsignedResponse;
	private String hl7Request;
	private String hl7Response;
	private List<String> errors;
	private Exception searchException; // legacy property

	public Optional<String> getContinuationPointer()
	{
		if(StringUtils.isNotBlank(hl7Response))
		{
			String lastHl7Line = StringUtils.substringAfterLast(hl7Response.trim(), "\n");
			if(lastHl7Line.startsWith("DSC|"))
			{
				return Optional.of(lastHl7Line.substring(4)); // everything after DSC| segment
			}
		}
		return Optional.empty();
	}
}
