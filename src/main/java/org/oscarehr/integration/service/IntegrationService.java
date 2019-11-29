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
import org.oscarehr.common.model.Security;
import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.integration.dao.UserIntegrationAccessDao;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.model.UserIntegrationAccess;
import org.oscarehr.integration.myhealthaccess.dto.IntegrationTransfer;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.util.StringUtils;

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

    public Integration findMhaIntegration(String siteName)
    {
        if (StringUtils.isNullOrEmpty(siteName))
        {
            return integrationDao.findDefaultByIntegration(Integration.INTEGRATION_TYPE_MHA);
        }

        return integrationDao.findByIntegrationAndSiteName(siteName, Integration.INTEGRATION_TYPE_MHA);
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
}
