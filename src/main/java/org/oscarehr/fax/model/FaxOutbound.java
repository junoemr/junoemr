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
	private String externalAccountType;

	@Column(name= "external_reference_id")
	private Long externalReferenceId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fax_account_id")
	private FaxAccount faxAccount;

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

	public String getStatusMessage()
	{
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}

	public String getSentTo()
	{
		return sentTo;
	}

	public void setSentTo(String sentTo)
	{
		this.sentTo = sentTo;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
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

	public String getExternalAccountType()
	{
		return externalAccountType;
	}

	public void setExternalAccountType(String externalAccountType)
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

	public FaxAccount getFaxAccount()
	{
		return faxAccount;
	}

	public void setFaxAccount(FaxAccount faxAccount)
	{
		this.faxAccount = faxAccount;
	}
}
