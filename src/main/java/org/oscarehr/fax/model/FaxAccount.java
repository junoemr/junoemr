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

import org.hibernate.annotations.Where;
import org.oscarehr.common.encryption.StringEncryptor;
import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.fax.provider.FaxProvider;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name="fax_account")
@Where(clause="deleted_at IS NULL")
public class FaxAccount extends AbstractModel<Long>
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="login_id")
	private String loginId;

	@Column(name="login_password")
	private String loginPassword;

	@Column(name="integration_type")
	@Enumerated(EnumType.STRING)
	private FaxProvider integrationType;

	@Column(name="integration_enabled")
	private Boolean integrationEnabled;

	@Column(name="inbound_enabled")
	private Boolean inboundEnabled;

	@Column(name="outbound_enabled")
	private Boolean outboundEnabled;

	@Column(name="reply_fax_number")
	private String replyFaxNumber;

	@Column(name="email")
	private String email;

	@Column(name="cover_letter_option")
	private String CoverLetterOption;

	@Column(name="display_name")
	private String displayName;

	@Column(name="deleted_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date deletedAt;

	@Override
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getLoginId()
	{
		return loginId;
	}

	public void setLoginId(String loginId)
	{
		this.loginId = loginId;
	}

	public String getLoginPassword()
	{
		return (loginPassword == null) ? null : StringEncryptor.decrypt(loginPassword);
	}

	public void setLoginPassword(String loginPassword)
	{
		this.loginPassword = (loginPassword == null) ? null : StringEncryptor.encrypt(loginPassword);
	}

	public FaxProvider getIntegrationType()
	{
		return integrationType;
	}

	public void setIntegrationType(FaxProvider integrationType)
	{
		this.integrationType = integrationType;
	}

	public Boolean getIntegrationEnabled()
	{
		return integrationEnabled;
	}

	public Boolean isIntegrationEnabled()
	{
		return integrationEnabled;
	}

	public void setIntegrationEnabled(Boolean integrationEnabled)
	{
		this.integrationEnabled = integrationEnabled;
	}

	public Boolean getInboundEnabled()
	{
		return inboundEnabled;
	}

	public Boolean isInboundEnabled()
	{
		return inboundEnabled;
	}

	public void setInboundEnabled(Boolean inboundEnabled)
	{
		this.inboundEnabled = inboundEnabled;
	}

	public Boolean getOutboundEnabled()
	{
		return outboundEnabled;
	}

	public Boolean isOutoundEnabled()
	{
		return outboundEnabled;
	}

	public void setOutboundEnabled(Boolean outoundEnabled)
	{
		this.outboundEnabled = outoundEnabled;
	}

	public String getReplyFaxNumber()
	{
		return replyFaxNumber;
	}

	public void setReplyFaxNumber(String replyFaxNumber)
	{
		this.replyFaxNumber = replyFaxNumber;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getCoverLetterOption()
	{
		return CoverLetterOption;
	}

	public void setCoverLetterOption(String coverLetterOption)
	{
		CoverLetterOption = coverLetterOption;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public Date getDeletedAt()
	{
		return deletedAt;
	}

	public void setDeletedAt(Date deletedAt)
	{
		this.deletedAt = deletedAt;
	}
}