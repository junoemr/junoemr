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
package org.oscarehr.fax.model;

import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.fax.provider.FaxProvider;
import org.oscarehr.provider.model.ProviderData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "fax_outbound")
public class FaxOutbound extends AbstractModel<Long>
{
	public enum FileType
	{
		DOCUMENT,
		FORM,
		PRESCRIPTION,
		CONSULTATION
	}

	public enum Status
	{
		ERROR,
		QUEUED,
		SENT
	}

	public enum NotificationStatus
	{
		NOTIFY,
		SILENT
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name="created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Enumerated(EnumType.STRING)
	@Column(name= "status")
	private Status status;

	@Column(name= "status_message")
	private String statusMessage;

	@Column(name= "sent_to")
	private String sentTo;

	@Column(name= "notification_status")
	@Enumerated(EnumType.STRING)
	private NotificationStatus notificationStatus = NotificationStatus.NOTIFY;

	@Column(name= "archived")
	private Boolean archived = false;

	@Column(name= "provider_no")
	private String providerNo;

	@Column(name= "demographic_no")
	private Integer demographicNo;

	@Enumerated(EnumType.STRING)
	@Column(name= "file_type")
	private FileType fileType;

	@Column(name= "file_name")
	private String fileName;

	@Column(name= "external_account_id")
	private String externalAccountId;

	@Column(name= "external_account_type")
	@Enumerated(EnumType.STRING)
	private FaxProvider externalAccountType;

	@Column(name= "external_reference_id")
	private Long externalReferenceId;

	@Column(name= "external_status")
	private String externalStatus;

	@Column(name= "external_delivery_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date externalDeliveryDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fax_account_id")
	private FaxAccount faxAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="provider_no", referencedColumnName="provider_no", insertable=false, updatable=false)
	private ProviderData provider;

	@Override
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Date getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt(Date createdAt)
	{
		this.createdAt = createdAt;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public void setStatusSent()
	{
		setStatus(Status.SENT);
	}

	public void setStatusQueued()
	{
		setStatus(Status.QUEUED);
	}

	public void setStatusError()
	{
		setStatus(Status.ERROR);
	}

	public boolean isStatusSent()
	{
		return Status.SENT.equals(getStatus());
	}

	public boolean isStatusQueued()
	{
		return Status.QUEUED.equals(getStatus());
	}

	public boolean isStatusError()
	{
		return Status.ERROR.equals(getStatus());
	}

	public String getStatusMessage()
	{
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}

	public Boolean getArchived()
	{
		return archived;
	}

	public void setArchived(Boolean acknowledged)
	{
		this.archived = acknowledged;
	}

	public String getSentTo()
	{
		return sentTo;
	}

	public void setSentTo(String sentTo)
	{
		this.sentTo = sentTo;
	}

	public NotificationStatus getNotificationStatus()
	{
		return notificationStatus;
	}

	public void setNotificationStatus(NotificationStatus notificationStatus)
	{
		this.notificationStatus = notificationStatus;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
	}

	public ProviderData getProvider()
	{
		return provider;
	}

	public void setProvider(ProviderData provider)
	{
		this.provider = provider;
		this.providerNo = provider.getId();
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public FileType getFileType()
	{
		return fileType;
	}

	public void setFileType(FileType fileType)
	{
		this.fileType = fileType;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getExternalAccountId()
	{
		return externalAccountId;
	}

	public void setExternalAccountId(String externalAccountId)
	{
		this.externalAccountId = externalAccountId;
	}

	public FaxProvider getExternalAccountType()
	{
		return externalAccountType;
	}

	public void setExternalAccountType(FaxProvider externalAccountType)
	{
		this.externalAccountType = externalAccountType;
	}

	public Long getExternalReferenceId()
	{
		return externalReferenceId;
	}

	public void setExternalReferenceId(Long externalReferenceId)
	{
		this.externalReferenceId = externalReferenceId;
	}

	public String getExternalStatus()
	{
		return externalStatus;
	}

	public void setExternalStatus(String externalStatus)
	{
		this.externalStatus = externalStatus;
	}

	public Date getExternalDeliveryDate()
	{
		return externalDeliveryDate;
	}

	public void setExternalDeliveryDate(Date externalDeliveryDate)
	{
		this.externalDeliveryDate = externalDeliveryDate;
	}

	public FaxAccount getFaxAccount()
	{
		return faxAccount;
	}

	public void setFaxAccount(FaxAccount faxAccount)
	{
		this.faxAccount = faxAccount;
	}

	/**
	 * determine the given account will have a record of this outbound fax.
	 * This requires this fax to have been sent (failed faxes will have no remote record),
	 * and for the account id/type to match the account that was originally used for this record.
	 * @param accountToCheck the account to verify
	 * @return true if the the account is expected to have information on this fax record, false otherwise
	 */
	public boolean isLinkedWithRemoteAccount(FaxAccount accountToCheck)
	{
		return  this.isStatusSent() &&
				this.getExternalAccountId().equals(accountToCheck.getLoginId()) &&
				this.getExternalAccountType().equals(accountToCheck.getIntegrationType());

	}
}