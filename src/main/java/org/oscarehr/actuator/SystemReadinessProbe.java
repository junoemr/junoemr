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

package org.oscarehr.actuator;

import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.casemgmt.dao.CaseManagementNoteDAO;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.EChartDao;
import org.oscarehr.common.dao.MyGroupAccessRestrictionDao;
import org.oscarehr.common.dao.MyGroupDao;
import org.oscarehr.common.dao.ProviderSiteDao;
import org.oscarehr.common.dao.SecurityDao;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.Security;
import org.oscarehr.schedule.dao.ScheduleDateDao;
import org.oscarehr.security.dao.SecUserRoleDao;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import oscar.OscarProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom system health check of Juno for the spring boot actuator readiness probe
 */
@Component
public class SystemReadinessProbe implements HealthIndicator
{

    private static final Logger logger = MiscUtils.getLogger();
    private static final Health.Builder warning = Health.status("WARNING");
    private final CaseManagementNoteDAO caseManagementNoteDAO;
    private final DemographicDao demographicDao;
    private final EChartDao eChartDao;
    private final MyGroupDao myGroupDao;
    private final MyGroupAccessRestrictionDao myGroupAccessRestrictionDao;
    private final ProviderDao providerDao;
    private final ProviderSiteDao providerSiteDao;
    private final ScheduleDateDao scheduleDateDao;
    private final SecurityDao securityDao;
    private final SecUserRoleDao secUserRoleDao;
    private final SiteDao siteDao;

    @Autowired
    public SystemReadinessProbe(
            CaseManagementNoteDAO caseManagementNoteDAO,
            DemographicDao demographicDao,
            EChartDao eChartDao,
            MyGroupDao myGroupDao,
            MyGroupAccessRestrictionDao myGroupAccessRestrictionDao,
            ProviderDao providerDao,
            ProviderSiteDao providerSiteDao,
            ScheduleDateDao scheduleDateDao,
            SecurityDao securityDao,
            SecUserRoleDao secUserRoleDao,
            SiteDao siteDao
    )
    {
        this.caseManagementNoteDAO = caseManagementNoteDAO;
        this.demographicDao = demographicDao;
        this.myGroupDao = myGroupDao;
        this.myGroupAccessRestrictionDao = myGroupAccessRestrictionDao;
        this.eChartDao = eChartDao;
        this.providerDao = providerDao;
        this.providerSiteDao = providerSiteDao;
        this.scheduleDateDao = scheduleDateDao;
        this.securityDao = securityDao;
        this.secUserRoleDao = secUserRoleDao;
        this.siteDao = siteDao;
    }

    /**
     * Returns a custom system health check of Juno for readiness probes and the spring boot actuator
     *
     * @return Health of the system for readiness probes
     */
    @Override
    public Health health()
    {
        Health.Builder health = systemHealthCheck();
        return health.build();
    }

    /**
     * Test the health of the system for readiness probes
     *
     * @return Health.Builder with details
     */
    private Health.Builder systemHealthCheck()
    {
        try
        {
            Security security = loginHealthCheck();

            String providerNo = security.getProviderNo();
            scheduleHealthCheck(providerNo);

            Map<String, Object> healthWarningDetails = new HashMap<>();
            demographicHealthCheck(healthWarningDetails);

            boolean isExpireSet = security.isExpireSet();
            oscarHostHealthCheck(healthWarningDetails, providerNo, isExpireSet);

            if (healthWarningDetails.isEmpty())
            {
                return Health.up();
            }
            else
            {
                return warning.withDetails(healthWarningDetails);
            }
        }
        catch (Exception e)
        {
            logger.error("SystemReadinessProbe", e);
            return Health.down().withDetail("exception", "Application Health Check Exception");
        }
    }

    /**
     * Test similar process to LoginService by getting security, provider, secUserRole DAOs
     *
     * @return security entry for oscar_host
     */
    private Security loginHealthCheck()
    {
        String userName = "oscar_host";
        Security security = securityDao.findByUserName(userName);
        String providerNo = security.getProviderNo();
        secUserRoleDao.getUserRoles(providerNo);
        return security;
    }

    /**
     * Test getProperty similar to providercontrol.jsp and test all DAOs in appointment_optimized.jsp
     */
    private void scheduleHealthCheck(String providerNo)
    {
        OscarProperties props = OscarProperties.getInstance();
        props.getProperty("default_schedule_viewall");
        String myGroupNo = ".default";
        siteDao.getAllActiveSites();
        providerSiteDao.findByProviderNo(providerNo);
        myGroupDao.getGroupByGroupNo(myGroupNo);
        scheduleDateDao.search_numgrpscheduledate(myGroupNo, new Date());
        myGroupAccessRestrictionDao.findByProviderNo(providerNo);
    }

    /**
     * Test DAOs for demographics/notes/echarts
     *
     */
    private void demographicHealthCheck(Map<String, Object> healthWarningDetails)
    {
        List<Integer> demographics = demographicDao.getActiveDemographicIds();
        if (demographics.size() > 0 && demographics.get(0) != null)
        {
            Integer demographicNo = demographics.get(0);
            demographicDao.getDemographic(demographicNo.toString());
            caseManagementNoteDAO.getNotesByDemographic(demographicNo.toString());
            eChartDao.getLatestChart(demographicNo);
        }
        else
        {
            logger.warn("No demographic found in health check");
            healthWarningDetails.put("demographics", "No Demogrphics");
        }
    }

    /**
     * Check the health of the oscar_host record
     * 1) Security record is active
     * 2) Is a super_admin
     * 3) Not expired
     *
     * @param healthWarningDetails map of health warning details to add to the health builder
     * @param providerNo           the oscar_host provider number
     * @param isExpireSet          the security object for oscar_host
     */
    private void oscarHostHealthCheck(Map<String, Object> healthWarningDetails, String providerNo, boolean isExpireSet)
    {
        Provider oscarHostUser = providerDao.getProvider(providerNo);
        if (!oscarHostUser.isActive())
        {
            logger.warn("oscar_host security record is not active, status is set to 0 in provider");
            healthWarningDetails.put("oscar_host_security_record", "Security record is not active");
        }

        if (!oscarHostUser.getSuperAdmin())
        {
            logger.warn("oscar_host is not a super_admin, super_admin is set to 0 in provider");
            healthWarningDetails.put("oscar_host_super_admin", "Is not a super_admin");
        }

        if (isExpireSet)
        {
            logger.warn("oscar_host is expired, b_ExpiredSet is set to 1 in security");
            healthWarningDetails.put("oscar_host_expired", "Is expired");
        }
    }

}
