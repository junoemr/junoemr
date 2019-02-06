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
package org.oscarehr.email.model;


import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "log_emails")
public class EmailLog extends AbstractModel<Long> implements Serializable
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "timestamp", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp = new Date();

	@Column(name = "referring_provider_no")
	private String referringProviderNo;

	@Column(name = "loggedIn_provider_no")
	private String loggedInProviderNo;

	@Column(name = "referral_doctor_id")
	private Integer referralDoctorId;

	@Column(name = "demographic_no")
	private Integer demographicNo;

	@Column(name = "email_address")
	private String emailAddress;

	@Column(name = "email_success", columnDefinition = "TINYINT(1)", nullable = false)
	private boolean emailSent = false;

	@Column(name = "email_content")
	private String emailContent;

	@PreRemove
	protected void jpaPreventDelete()
	{
		throw (new UnsupportedOperationException("Remove is not allowed for this type of item."));
	}

	@PreUpdate
	protected void jpaPreventUpdate()
	{
		throw (new UnsupportedOperationException("Update is not allowed for this type of item."));
	}

	@Override
	public Long getId()
	{
		return id;
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getReferringProviderNo()
	{
		return referringProviderNo;
	}

	public void setReferringProviderNo(String referringProviderNo)
	{
		this.referringProviderNo = referringProviderNo;
	}

	public String getLoggedInProviderNo()
	{
		return loggedInProviderNo;
	}

	public void setLoggedInProviderNo(String loggedInProviderNo)
	{
		this.loggedInProviderNo = loggedInProviderNo;
	}

	public Integer getReferralDoctorId()
	{
		return referralDoctorId;
	}

	public void setReferralDoctorId(Integer referralDoctorId)
	{
		this.referralDoctorId = referralDoctorId;
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public String getEmailAddress()
	{
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	public boolean isEmailSent()
	{
		return emailSent;
	}

	public void setEmailSent(boolean emailSent)
	{
		this.emailSent = emailSent;
	}

	public String getEmailContent()
	{
		return emailContent;
	}

	public void setEmailContent(String emailContent)
	{
		this.emailContent = emailContent;
	}
}