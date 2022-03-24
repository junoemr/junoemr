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
import org.oscarehr.document.model.Document;
import org.oscarehr.fax.provider.FaxProvider;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "fax_inbound")
public class FaxInbound extends AbstractModel<Long>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name="created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(name= "sent_from")
	private String sentFrom;

	@Column(name= "external_account_id")
	private String externalAccountId;

	@Column(name= "external_account_type")
	@Enumerated(EnumType.STRING)
	private FaxProvider externalAccountType;

	@Column(name= "external_reference_id")
	private Long externalReferenceId;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "document_no")
	private Document document;

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

	public String getSentFrom()
	{
		return sentFrom;
	}

	public void setSentFrom(String sentFrom)
	{
		this.sentFrom = sentFrom;
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

	public Document getDocument()
	{
		return document;
	}

	public void setDocument(Document document)
	{
		this.document = document;
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