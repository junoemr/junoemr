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
import org.oscarehr.fax.model.FaxOutbound;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore properties that are not defined in this class
public class FaxOutboxTransferOutbound implements Serializable
{
	public enum CombinedStatus
	{
		ERROR,
		QUEUED,
		IN_PROGRESS,
		INTEGRATION_FAILED,
		INTEGRATION_SUCCESS
	}

	private Long id;
	private Long faxAccountId;

	private String providerNo;
	private String providerName;
	private Integer demographicNo;
	private String toFaxNumber;
	/* file type: document, form, consult, etc. */
	private String fileType;
	/* the sent status of the document as recorded in the system */
	private FaxOutbound.Status systemStatus;
	/* a message sent along with the status, usually for error explanations */
	private String systemStatusMessage;
	/* the sent date of the document as recorded in the system */
	private String systemDateSent;
	private Boolean archived;
	private String notificationStatus;

	/* the sent status of the document as retrieved from the api */
	private String integrationStatus;
	/* the received/queued date of the document as retrieved from the api.
	 * when the integration first learns of the document */
	private String integrationDateQueued;
	/* the sent date of the document as retrieved from the api */
	private String integrationDateSent;

	/* the single combined state of the systemStatus and the integrationStatus */
	private CombinedStatus combinedStatus;

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

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
	}

	public String getProviderName()
	{
		return providerName;
	}

	public void setProviderName(String providerName)
	{
		this.providerName = providerName;
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public String getToFaxNumber()
	{
		return toFaxNumber;
	}

	public void setToFaxNumber(String toFaxNumber)
	{
		this.toFaxNumber = toFaxNumber;
	}

	public String getFileType()
	{
		return fileType;
	}

	public void setFileType(String fileType)
	{
		this.fileType = fileType;
	}

	public FaxOutbound.Status getSystemStatus()
	{
		return systemStatus;
	}

	public void setSystemStatus(FaxOutbound.Status systemStatus)
	{
		this.systemStatus = systemStatus;
	}

	public String getSystemStatusMessage()
	{
		return systemStatusMessage;
	}

	public void setSystemStatusMessage(String systemStatusMessage)
	{
		this.systemStatusMessage = systemStatusMessage;
	}

	public Boolean getArchived()
	{
		return archived;
	}

	public void setArchived(Boolean archived)
	{
		this.archived = archived;
	}

	public String getNotificationStatus()
	{
		return notificationStatus;
	}

	public void setNotificationStatus(String notificationStatus)
	{
		this.notificationStatus = notificationStatus;
	}

	public String getSystemDateSent()
	{
		return systemDateSent;
	}

	public void setSystemDateSent(String systemDateSent)
	{
		this.systemDateSent = systemDateSent;
	}

	public String getIntegrationStatus()
	{
		return integrationStatus;
	}

	public void setIntegrationStatus(String integrationStatus)
	{
		this.integrationStatus = integrationStatus;
	}

	public String getIntegrationDateQueued()
	{
		return integrationDateQueued;
	}

	public void setIntegrationDateQueued(String integrationDateQueued)
	{
		this.integrationDateQueued = integrationDateQueued;
	}

	public String getIntegrationDateSent()
	{
		return integrationDateSent;
	}

	public void setIntegrationDateSent(String integrationDateSent)
	{
		this.integrationDateSent = integrationDateSent;
	}

	public CombinedStatus getCombinedStatus()
	{
		return combinedStatus;
	}

	public void setCombinedStatus(CombinedStatus combinedStatus)
	{
		this.combinedStatus = combinedStatus;
	}
}
