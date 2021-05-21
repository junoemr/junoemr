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

package org.oscarehr.messaging.model;

import java.time.ZonedDateTime;
import java.util.List;

public interface Message
{
	// ==========================================================================
	// Getters
	// ==========================================================================

	public String getId();
	public String getConversationId();

	public String getSubject();
	public String getMessage();

	/**
	 * get the group of the message
	 * @return - the messages group
	 */
	public MessageGroup getGroup();

	/**
	 * indicate if the user has read the message or not.
	 * @return - true / false
	 */
	public Boolean isRead();

	/**
	 * get the time at which the message was created.
	 * @return - message creation time.
	 */
	public ZonedDateTime getCreatedAtDateTime();

	/**
	 * get the messageable that sent this message.
	 * @return - the sender.
	 */
	public Messageable<?> getSender();

	/**
	 * get list of recipients of this message.
	 * @return - list of recipients.
	 */
	public List<? extends Messageable<?>> getRecipients();

	/**
	 * get JSON string of meta data.
	 * @return - json meta data.
	 */
	public String getMetaData();

	/**
	 * get a list of attachments
	 * @return - the attachments
	 */
	public List<? extends Attachment> getAttachments();

}
