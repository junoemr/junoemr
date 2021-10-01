/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package org.oscarehr.olis;

import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.dao.SecurityDao;
import org.oscarehr.olis.dao.OLISSystemPreferencesDao;
import org.oscarehr.olis.model.OLISSystemPreferences;
import org.oscarehr.olis.service.OLISPollingService;
import org.oscarehr.util.DbConnectionFilter;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A job can start many tasks
 * 
 * @author Indivica
 */
@Service
public class OLISSchedulerJob
{
	private static final Logger logger = MiscUtils.getLogger();

	@Autowired
	private OLISPollingService olisPollingService;

	@Autowired
	private OLISSystemPreferencesDao olisPrefDao;

	@Autowired
	private ProviderDao providerDao;

	@Autowired
	private SecurityDao securityDao;

	public void run()
	{
		try
		{
			logger.info("starting OLIS poller job");
			OLISSystemPreferences olisPrefs = olisPrefDao.getPreferences();
			if (olisPrefs == null) {
				// not set to run at all
				logger.info("OLISPoller - Cannot run. No entry in OLISSystemPreferences table");
				return;
			}
			Date now = new Date();
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmssZ");
			Date startDate = null, endDate = null;
			try {
				if (olisPrefs.getStartTime() != null && olisPrefs.getStartTime().trim().length() > 0) startDate = dateFormatter.parse(olisPrefs.getStartTime());

				if (olisPrefs.getEndTime() != null && olisPrefs.getEndTime().trim().length() > 0) endDate = dateFormatter.parse(olisPrefs.getEndTime());
			} catch (ParseException e) {
				logger.error("Error", e);
			}
			logger.info("start date = " + startDate);
			logger.info("end date = " + endDate);

			if ((startDate != null && now.before(startDate)) || (endDate != null && now.after(endDate))) {
				logger.info("Don't need to run right now");
				return;
			}

			if (olisPrefs.getLastRun() != null) {
				// check to see if we are past last run + frequency interval
				Integer freqMins = olisPrefs.getPollFrequency();
				if(freqMins == null)
				{
					freqMins = OLISSystemPreferences.DEFAULT_POLLING_FREQUENCY;
				}

				Calendar cal = Calendar.getInstance();
				cal.setTime(olisPrefs.getLastRun());
				cal.add(Calendar.MINUTE, freqMins);

				if (cal.getTime().getTime() > now.getTime()) {
					logger.info("not yet time to run - last run @ " + olisPrefs.getLastRun() + " and freq is " + freqMins + " mins.");
					return;
				}
			}

			logger.info("===== OLIS JOB RUNNING....");
			olisPrefs.setLastRun(new Date());
			olisPrefDao.merge(olisPrefs);

			LoggedInInfo loggedInInfo = new LoggedInInfo();
			loggedInInfo.setLoggedInProvider(providerDao.getProvider("999998"));
			loggedInInfo.setLoggedInSecurity(securityDao.getByProviderNo("999998"));

			olisPollingService.requestResults(loggedInInfo);
			logger.info("===== OLIS JOB COMPLETE....");
		}
		catch(Exception e)
		{
			logger.error("error", e);
		}
		finally
		{
			DbConnectionFilter.releaseAllThreadDbResources();
		}
	}

}
