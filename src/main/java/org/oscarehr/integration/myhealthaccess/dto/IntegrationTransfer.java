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

package org.oscarehr.integration.myhealthaccess.dto;

import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.util.SpringUtils;

public final class IntegrationTransfer
{
    private String clinicId;
    private String apiKey;
    private String siteName;
    private String integrationType;

    public String getClinicId()
    {
        return clinicId;
    }

    public void setClinicId(String clinicId)
    {
        this.clinicId = clinicId;
    }

    public String getApiKey()
    {
        return apiKey;
    }

    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
    }

    public String getSiteName()
    {
        return siteName;
    }

    public void setSiteName(String siteName)
    {
        this.siteName = siteName;
    }

    public String getIntegrationType()
    {
        return integrationType;
    }

    public void setIntegrationType(String integrationType)
    {
        this.integrationType = integrationType;
    }

    public Integration toIntegration()
    {
        SiteDao siteDao = SpringUtils.getBean(SiteDao.class);
        Integration integration = new Integration();

        integration.setRemoteId(this.clinicId);
        integration.setApiKey(this.apiKey);
        integration.setIntegrationType(this.integrationType);
        integration.setSite(siteDao.findByName(this.siteName));

        return integration;
    }
}
