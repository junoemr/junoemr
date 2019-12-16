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


package org.oscarehr.integration.model;

import org.oscarehr.common.encryption.StringEncryptor;
import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.common.model.Security;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user_integration_access")
public class UserIntegrationAccess extends AbstractModel<Integer> implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinColumn(name = "security_no", referencedColumnName = "security_no")
	private Security security;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinColumn(name = "integration_id", referencedColumnName = "id")
	private Integration integration;

	@Column(name = "remote_user_id")
	private String remoteUserId;

	@Column(name = "api_key")
	private String apiKey;

	public UserIntegrationAccess()
	{
	}

	public UserIntegrationAccess(Integration integration, Security security, String remoteUserId, String apiKey)
	{
		setIntegration(integration);
		setSecurity(security);
		setRemoteUserId(remoteUserId);
		setApiKey(apiKey);
	}

	@Override
	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Security getSecurity()
	{
		return security;
	}

	public void setSecurity(Security security)
	{
		this.security = security;
	}

	public Integration getIntegration()
	{
		return integration;
	}

	public void setIntegration(Integration integration)
	{
		this.integration = integration;
	}

	public String getRemoteUserId()
	{
		return remoteUserId;
	}

	public void setRemoteUserId(String remoteUserId)
	{
		this.remoteUserId = remoteUserId;
	}

	public String getApiKey()
	{
		return (apiKey == null) ? null : StringEncryptor.decrypt(apiKey);
	}

	public void setApiKey(String accessToken)
	{
		this.apiKey = (accessToken == null) ? null : StringEncryptor.encrypt(accessToken);
	}
}
