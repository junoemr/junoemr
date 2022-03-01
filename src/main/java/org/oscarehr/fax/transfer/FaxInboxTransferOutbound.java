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
package org.oscarehr.fax.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore properties that are not defined in this class
public class FaxInboxTransferOutbound implements Serializable
{
	private Long id;
	private Long faxAccountId;
	/* the received date of the document as recorded in the system */
	private String systemDateReceived;
	/* the id of the document in the system */
	private Integer documentId;

	private String sentFrom;

	private Long externalReferenceId;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getFaxAccountId()
	{
		return faxAccountId;
	}

	public void setFaxAccountId(Long faxAccountId)
	{
		this.faxAccountId = faxAccountId;
	}

	public String getSystemDateReceived()
	{
		return systemDateReceived;
	}

	public void setSystemDateReceived(String systemDateReceived)
	{
		this.systemDateReceived = systemDateReceived;
	}

	public Integer getDocumentId()
	{
		return documentId;
	}

	public void setDocumentId(Integer documentId)
	{
		this.documentId = documentId;
	}

	public String getSentFrom()
	{
		return sentFrom;
	}

	public void setSentFrom(String sentFrom)
	{
		this.sentFrom = sentFrom;
	}

	public Long getExternalReferenceId()
	{
		return externalReferenceId;
	}

	public void setExternalReferenceId(Long externalReferenceId)
	{
		this.externalReferenceId = externalReferenceId;
	}
}
