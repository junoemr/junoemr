package org.oscarehr.health;

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
import org.oscarehr.common.model.Demographic;
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

@Component
public class CustomHealthIndicator implements HealthIndicator
{

    private static final Logger logger = MiscUtils.getLogger();
    private CaseManagementNoteDAO caseManagementNoteDAO;
    private DemographicDao demographicDao;
    private EChartDao eChartDao;
    private MyGroupDao myGroupDao;
    private MyGroupAccessRestrictionDao myGroupAccessRestrictionDao;
    private ProviderDao providerDao;
    private ProviderSiteDao providerSiteDao;
    private ScheduleDateDao scheduleDateDao;
    private SecurityDao securityDao;
    private SecUserRoleDao secUserRoleDao;
    private SiteDao siteDao;

    @Autowired
    public CustomHealthIndicator(
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

    @Override
    public Health health()
    {
        int errorCode = check();
        if (errorCode != 0)
        {
            return Health.down().withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();
    }

    private int check()
    {
        try
        {
            // Test similar process to LoginService by getting security, provider, secUserRole DAOs
            String userName = "oscar_host";
            String myGroupNo = ".default";
            Security security = securityDao.findByUserName(userName);
            String providerNo = security.getProviderNo();
            providerDao.getProvider(providerNo);
            secUserRoleDao.getUserRoles(providerNo);

            // Try getProperty similar to providercontrol.jsp
            OscarProperties props = OscarProperties.getInstance();
            props.getProperty("default_schedule_viewall");

            // Test all DAOs in appointment_optimized.jsp
            siteDao.getAllActiveSites();
            providerSiteDao.findByProviderNo(providerNo);
            myGroupDao.getGroupByGroupNo(myGroupNo);
            scheduleDateDao.search_numgrpscheduledate(myGroupNo, new Date());
            myGroupAccessRestrictionDao.findByProviderNo(providerNo);

            // Test DAOs for demographics/notes/echarts
            String demographicNo = "1";
            Demographic demographic = demographicDao.getDemographic(demographicNo);
            if (demographic != null)
            {
                caseManagementNoteDAO.getNotesByDemographic(demographicNo);
                eChartDao.getLatestChart(Integer.parseInt(demographicNo));
            }
            else
            {
                logger.warn("No demographic found in health check");
            }
        }
        catch (Exception e)
        {
            logger.error("CustomHealthIndicatorError", e);
            return 1;
        }
        return 0;
    }

}
