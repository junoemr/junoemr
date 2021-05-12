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
import org.oscarehr.messaging.model.MessageGroup;
import org.oscarehr.messaging.model.Messageable;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class MhaMessage implements org.oscarehr.messaging.model.Message
{
	protected String id;
	protected String conversationId;
	protected String subject;
	protected String message;
	protected MessageGroup group;
	protected Boolean read;
	protected ZonedDateTime createdAtDateTime;
	protected MhaMessageable sender;
	protected List<MhaMessageable> recipients;
	protected String metaData;
	protected MhaAttachment attachments;

	// ==========================================================================
	// Getters
	// ==========================================================================

	@Override
	public Boolean isRead()
	{
		return this.read;
	}
}
