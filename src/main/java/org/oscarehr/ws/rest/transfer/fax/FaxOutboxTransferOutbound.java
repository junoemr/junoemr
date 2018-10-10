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
package org.oscarehr.ws.rest.transfer.fax;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore properties that are not defined in this class
public class FaxOutboxTransferOutbound implements Serializable
{
	private Long faxAccountId;
	private String fileName;
	private String subject;

	private String sentStatus;
	private String dateQueued;
	private String dateSent;
	private String toFaxNumber;

	public Long getFaxAccountId()
	{
		return faxAccountId;
	}

	public void setFaxAccountId(Long faxAccountId)
	{
		this.faxAccountId = faxAccountId;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public String getSentStatus()
	{
		return sentStatus;
	}

	public void setSentStatus(String sentStatus)
	{
		this.sentStatus = sentStatus;
	}

	public String getDateQueued()
	{
		return dateQueued;
	}

	public void setDateQueued(String dateQueued)
	{
		this.dateQueued = dateQueued;
	}

	public String getDateSent()
	{
		return dateSent;
	}

	public void setDateSent(String dateSent)
	{
		this.dateSent = dateSent;
	}

	public String getToFaxNumber()
	{
		return toFaxNumber;
	}

	public void setToFaxNumber(String toFaxNumber)
	{
		this.toFaxNumber = toFaxNumber;
	}
}
