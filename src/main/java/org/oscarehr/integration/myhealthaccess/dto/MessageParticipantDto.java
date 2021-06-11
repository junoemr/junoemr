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

package org.oscarehr.integration.myhealthaccess.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.oscarehr.messaging.model.MessageableType;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class MessageParticipantDto implements Serializable
{
	protected String id;
	protected String name;
	@JsonProperty("identification_name")
	protected String identificationName;
	protected MessageableType type;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	public MessageParticipantDto(String id, String name, MessageableType type)
	{
		this(id, name, null, type);
	}

	public MessageParticipantDto(String id, String name, String identificationName, MessageableType type)
	{
		this.id = id;
		this.name = name;
		this.identificationName = identificationName != null ? identificationName : name;
		this.type = type;
	}
}
