/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * <p>
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.messaging.backend.myhealthaccess.model;

import lombok.Data;
import org.oscarehr.messaging.model.Attachment;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.util.MimeType;

import java.net.URL;
import java.time.ZonedDateTime;

@Data
public class MhaAttachment implements Attachment
{
	protected String id;
	protected String name;
	protected MimeType mimeType;
	protected ZonedDateTime createdAtDateTime;
	protected URL documentUrl;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * create new attachment
	 * @param id - attachment id
	 * @param name - attachment name
	 * @param mimeType - attachment mime type
	 * @param createdAtDateTime - attachment creation time
	 * @param documentUrl - the url at which the document can be download
	 */
	public MhaAttachment(String id, String name, MimeType mimeType, ZonedDateTime createdAtDateTime, URL documentUrl)
	{
		this.id = id;
		this.name = name;
		this.mimeType = mimeType;
		this.createdAtDateTime = createdAtDateTime;
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	/**
	 * attachments data as base64.
	 * @param loggedInInfo - the logged in user
	 * @return - base64 encoded attachment data.
	 */
	@Override
	public String getAttachmentBase64Data(LoggedInInfo loggedInInfo)
	{
		// TODO this
		return null;
	}

}
