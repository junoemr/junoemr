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

package org.oscarehr.integration.service;

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Security;
import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.integration.dao.UserIntegrationAccessDao;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.model.UserIntegrationAccess;
import org.oscarehr.integration.myhealthaccess.dto.IntegrationTransfer;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.OscarProperties;
import oscar.util.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

@Service
public class IntegrationService
{
    private static Logger logger = MiscUtils.getLogger();

    @Autowired
    private IntegrationDao integrationDao;

    @Autowired
    private UserIntegrationAccessDao userIntegrationAccessDao;

    @Autowired
    private SiteDao siteDao;

    public Integration findMhaIntegrationByClinicId(String clinicId)
    {
        return integrationDao.findByIntegrationAndRemoteId(clinicId, Integration.INTEGRATION_TYPE_MHA);
    }

    public List<Integration> getMyHealthAccessIntegrations()
    {
        return integrationDao.findMyHealthAccessIntegrations();
    }

    public boolean hasMyHealthAccessIntegration()
    {
        List<Integration> integrations = getMyHealthAccessIntegrations();
        return integrations != null && integrations.size() > 0;
    }

    /**
     * find MHA integration by appointment. Integration selected is based on the appointments site.
     * @param appointment - the appointment used to determine the integration.
     * @return - mha integration
     */
    public Integration findMhaIntegration(Appointment appointment)
    {
        String siteName = null;
        if (OscarProperties.getInstance().isMultisiteEnabled())
        {
            siteName = appointment.getLocation();
        }
        return findMhaIntegration(siteName);
    }

    public Integration findMhaIntegration(String siteName)
    {
        Integration integration;

        if (StringUtils.isNullOrEmpty(siteName))
        {
            integration = integrationDao.findDefaultByIntegration(Integration.INTEGRATION_TYPE_MHA);

            if (integration == null)
            {
                integration = integrationDao.findDefaultByIntegration(Integration.INTEGRATION_TYPE_CLOUD_MD);
            }
        }
        else
        {
            integration = integrationDao.findByIntegrationAndSiteName(siteName, Integration.INTEGRATION_TYPE_MHA);
        }

        return integration;
    }

    public UserIntegrationAccess findMhaUserAccessBySecurityAndSiteName(Security security, String siteName)
    {
        if (StringUtils.isNullOrEmpty(siteName))
        {
            return userIntegrationAccessDao.findBySecurityNoAndIntegration(security.getSecurityNo(), Integration.INTEGRATION_TYPE_MHA);
        }

        return userIntegrationAccessDao.findBySecurityNoAndSiteName(security.getSecurityNo(), siteName);
    }

    public void updateIntegration(IntegrationTransfer integrationTransfer)
    {
        Integration integration = integrationTransfer.toIntegration();
        integrationDao.save(integration);
    }

    public void updateUserIntegrationAccess(UserIntegrationAccess userIntegrationAccess)
    {
        userIntegrationAccessDao.save(userIntegrationAccess);
    }

    public void deleteUserIntegrationAccess(UserIntegrationAccess userIntegrationAccess)
    {
        userIntegrationAccessDao.remove(userIntegrationAccess);
    }

	/**
	 * Find an integration by name and (optional) site.  Integrations matching the same key, but at a different site will not be returned
	 * if the site is specified as null.
	 *
	 * @param integrationType unique integration key
	 * @param siteId optional siteName.  For non-multisite instances, or for integrations not reliant on site, this should be set as null.
	 * @return Integration if found, or null if no such record exists
	 * @throws javax.persistence.NonUniqueResultException if more than one record is found
	 */
	public Integration findIntegrationByTypeAndSite(String integrationType, @Nullable Integer siteId)
	{
		return integrationDao.findByIntegrationTypeAndSiteId(integrationType, siteId);
	}

	public List<Integration> findIntegrationsByType(String integrationType)
	{
		return integrationDao.findByIntegrationType(integrationType);
	}
}
