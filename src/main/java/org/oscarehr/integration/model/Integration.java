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

import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.common.model.Site;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "integration")
public class Integration extends AbstractModel<String> implements Serializable
{
    public static final String INTEGRATION_TYPE_MHA = "MYHEALTHACCESS";

    @Id
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
    public String getId()
    {
        return remoteId;
    }

    public String getRemoteId()
    {
        return this.remoteId;
    }

    public void setId(String id)
    {
        this.remoteId = id;
    }

    public String getApiKey()
    {
        return apiKey;
    }

    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
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
