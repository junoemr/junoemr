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
import org.oscarehr.common.model.Site;

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
@Table(name = "integration")
public class Integration extends AbstractModel<Integer> implements Serializable
{
    public static final String INTEGRATION_TYPE_MHA = "my_health_access";
    public static final String INTEGRATION_TYPE_CLOUD_MD = "cloud_md";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "remote_id")
    private String remoteId;

    @Column(name = "api_key")
    private String apiKey;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "site_id", referencedColumnName = "site_id")
    private Site site;

    @Column(name = "integration_type")
    private String integrationType;

    public Integration()
    {
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

    public String getRemoteId()
    {
        return remoteId;
    }

    public void setRemoteId(String remoteId)
    {
        this.remoteId = remoteId;
    }

    public String getApiKey()
    {
        return (apiKey == null) ? null : StringEncryptor.decrypt(apiKey);
    }

    public void setApiKey(String apiKey)
    {
        this.apiKey = (apiKey == null) ? null : StringEncryptor.encrypt(apiKey);
    }

    public Site getSite()
    {
        return site;
    }

    public void setSite(Site site)
    {
        this.site = site;
    }

    public String getIntegrationType()
    {
        return integrationType;
    }

    public void setIntegrationType(String integrationType)
    {
        this.integrationType = integrationType;
    }
}
