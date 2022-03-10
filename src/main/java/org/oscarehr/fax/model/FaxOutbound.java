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

import lombok.Data;
import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.fax.provider.FaxProvider;
import org.oscarehr.fax.provider.FaxUploadProvider;
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

@Data
@Entity
@Table(name = "fax_outbound")
public class FaxOutbound extends AbstractModel<Long>
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name="created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Enumerated(EnumType.STRING)
	@Column(name= "status")
	private FaxStatusInternal status;

	@Column(name= "status_message")
	private String statusMessage;

	@Column(name= "sent_to")
	private String sentTo;

	@Column(name= "notification_status")
	@Enumerated(EnumType.STRING)
	private FaxNotificationStatus notificationStatus = FaxNotificationStatus.NOTIFY;

	@Column(name= "archived")
	private Boolean archived = false;

	@Column(name= "provider_no")
	private String providerNo;

	@Column(name= "demographic_no")
	private Integer demographicNo;

	@Enumerated(EnumType.STRING)
	@Column(name= "file_type")
	private FaxFileType fileType;

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

	public void setStatusSent()
	{
		setStatus(FaxStatusInternal.SENT);
	}

	public void setStatusQueued()
	{
		setStatus(FaxStatusInternal.QUEUED);
	}

	public void setStatusError()
	{
		setStatus(FaxStatusInternal.ERROR);
	}

	public boolean isStatusSent()
	{
		return FaxStatusInternal.SENT.equals(getStatus());
	}

	public boolean isStatusQueued()
	{
		return FaxStatusInternal.QUEUED.equals(getStatus());
	}

	public boolean isStatusError()
	{
		return FaxStatusInternal.ERROR.equals(getStatus());
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

	public void setProvider(ProviderData provider)
	{
		this.provider = provider;
		this.providerNo = provider.getId();
	}

	public FaxStatusCombined getCombinedStatus(FaxUploadProvider uploadProvider)
	{
		FaxStatusInternal systemStatus = this.getStatus();
		FaxStatusCombined combinedStatus = null;
		if(FaxStatusInternal.ERROR.equals(systemStatus))
		{
			combinedStatus = FaxStatusCombined.ERROR;
		}
		else if(FaxStatusInternal.QUEUED.equals(systemStatus))
		{
			combinedStatus = FaxStatusCombined.QUEUED;
		}
		else if(FaxStatusInternal.SENT.equals(systemStatus) && uploadProvider.isFaxInRemoteSentState(this.getExternalStatus()))
		{
			combinedStatus = FaxStatusCombined.INTEGRATION_SUCCESS;
		}
		else if(FaxStatusInternal.SENT.equals(systemStatus) && uploadProvider.isFaxInRemoteFailedState(this.getExternalStatus()))
		{
			combinedStatus = FaxStatusCombined.INTEGRATION_FAILED;
		}
		else if(FaxStatusInternal.SENT.equals(systemStatus))
		{
			combinedStatus = FaxStatusCombined.IN_PROGRESS;
		}
		return combinedStatus;
	}
}