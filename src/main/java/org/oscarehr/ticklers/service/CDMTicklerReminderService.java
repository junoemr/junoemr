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

package org.oscarehr.ticklers.service;

import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.Security;
import org.oscarehr.ticklers.dao.CDMTicklerDao;
import org.oscarehr.ticklers.dao.TicklersDao;
import org.oscarehr.ticklers.model.CDMTicklerInfo;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import oscar.OscarProperties;
import oscar.oscarBilling.ca.bc.MSP.ServiceCodeValidationLogic;
import oscar.oscarTickler.TicklerCreator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Schedule CDM Tickler reminder emails on a recurring basis.  Since this is defined as a customizable property, we use
 * ScheduledExecutorService instead of the @Scheduled annotation.
 */
@WebListener
@Component
public class CDMTicklerReminderService implements ServletContextListener
{

    private static final Logger logger = MiscUtils.getLogger();
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private LoggedInInfo systemProviderSecurity;

    @Autowired
    private CDMTicklerDao cdmTicklerDao;

    @Autowired
    private TicklersDao ticklersDao;

    @Autowired
    private ProviderDao providerDao;


    /**
     * Create a scheduled CDM tickler task to run in the background if we have a BC Oscar instance and
     * the alert_poll_frequency property has been defined.
     *
     * @param event context creation
     */
    @Override
    public void contextInitialized(ServletContextEvent event)
    {
        // Autowiring (managed by Spring) occurs after context initializaion (by the webserver).
        // Unless we wire the class "manually" here, these beans will be null.

        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
        ctx.getAutowireCapableBeanFactory().autowireBean(this);

        initializeSecurity();

        OscarProperties properties = OscarProperties.getInstance();

        if (properties.isBritishColumbiaInstanceType())
        {
            Long pollingFrequencyMs = Long.parseLong(properties.getProperty("ALERT_POLL_FREQUENCY"));

            if (pollingFrequencyMs > 0)
            {
                List<Integer> cdmDxCodes = getCDMDxCodes();
                scheduler.scheduleAtFixedRate(new ProcessTicklersAsyncJob(cdmDxCodes), 1000L, pollingFrequencyMs, TimeUnit.MILLISECONDS);
            }
        }
    }

    private void initializeSecurity()
    {
        LoggedInInfo systemUser= new LoggedInInfo();
        Security security = new Security();
        security.setSecurityNo(0);
        Provider provider = providerDao.getProvider(Provider.SYSTEM_PROVIDER_NO);
        systemUser.setLoggedInSecurity(security);
        systemUser.setLoggedInProvider(provider);
        systemProviderSecurity = systemUser;
    }

    @Transactional
    protected void deleteBilledTicklers(List<Integer> cdmDxCodes)       // protected access modifier because @Transactional requires method to be overridable
    {
        try
        {
            List<CDMTicklerInfo> cdmTicklersToDelete = cdmTicklerDao.getCDMTicklersToDelete(cdmDxCodes);

            if (!cdmTicklersToDelete.isEmpty())
            {
                List<Integer> ticklerIds = new ArrayList<>();

                for (CDMTicklerInfo info : cdmTicklersToDelete)
                {
                    ticklerIds.add(info.getTicklerNo());
                }

                ticklersDao.deactivateAllTicklers(ticklerIds);
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
    }

    @Transactional
    protected void createUnbilledTicklers(List<Integer> cdmDxCodes)     // protected access modifier because @Transactional requires method to be overridable
    {
        try
        {
            List<CDMTicklerInfo> cdmPatientsToUpdate = cdmTicklerDao.getCDMTicklerCreationInfo(cdmDxCodes);
            createReminderTicklers(cdmPatientsToUpdate);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
    }

    /**
     * Clear out the reminder thread when the application context is destroyed to prevent memory leaks.  shutdownNow()
     * is a best effort, and not a guarantee.
     *
     * @param event context destruction
     */
    @Override
    public void contextDestroyed(ServletContextEvent event)
    {
        scheduler.shutdownNow();
    }

    /**
     * Return a List of CDM dx codes from the database
     * @return list of cdmCodes.
     */
    private List<Integer> getCDMDxCodes()
    {
        ServiceCodeValidationLogic lgc = new ServiceCodeValidationLogic();
        List<String[]> cdmServiceCodes = lgc.getCDMCodes();

        List<Integer> toReturn = new ArrayList<>();

        for (String[] code : cdmServiceCodes)
        {
            toReturn.add(Integer.parseInt(code[0]));
        }

        return toReturn;
    }


    private void createReminderTicklers(List<CDMTicklerInfo> ticklerInfoList)
    {
        for (CDMTicklerInfo ticklerInfo : ticklerInfoList)
        {
            GenerateReminderTickler(ticklerInfo);
        }
    }

    private void GenerateReminderTickler(CDMTicklerInfo ticklerInfo)
    {
        String createMessage = "SERVICE CODE %s - Never billed for this patient";
        String updateMessage = "SERVICE CODE %s - Last billed on %s";


        TicklerCreator ticklerCreator = new TicklerCreator();

        if (ticklerInfo.getBillingDate() == null)
        {
            ticklerCreator.createTickler(systemProviderSecurity, Integer.toString(ticklerInfo.getDemographicNo()),
                                         ticklerInfo.getProviderNo(), String.format(createMessage, ticklerInfo.getBillingCode()));
        }
        else
        {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            String dateString = ticklerInfo.getBillingDate().format(formatter);

            ticklerCreator.createTickler(systemProviderSecurity, Integer.toString(ticklerInfo.getDemographicNo()),
                                         ticklerInfo.getProviderNo(), String.format(updateMessage, ticklerInfo.getBillingCode(), dateString));
        }
    }

    /**
     * Run the CDM reminder task asynchronously.  Every cycle the delete task will be called.  Call the update task
     * if it's been at least six hours since it was last run.
     */
    private class ProcessTicklersAsyncJob implements Runnable
    {
        private final long SIX_HOURS = Duration.of(6, ChronoUnit.HOURS).toMillis();
        private long lastUpdate = 0L;
        private List<Integer> cdmDxCodes;

        ProcessTicklersAsyncJob(List<Integer> cdmDxCodes)
        {
            this.cdmDxCodes = cdmDxCodes;
        }

        @Override
        public void run()
        {
            deleteBilledTicklers(cdmDxCodes);

            Calendar calendar = Calendar.getInstance();
            Long now = calendar.getTimeInMillis();

            if (now - lastUpdate >= SIX_HOURS)
            {
                createUnbilledTicklers(cdmDxCodes);
                lastUpdate = calendar.getTimeInMillis();
            }
        }
    }
}


