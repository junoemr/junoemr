/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


package oscar.oscarPrevention.reports;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.oscarehr.demographicArchive.dao.DemographicArchiveDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.demographicArchive.entity.DemographicArchive;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

public final class PreventionReportUtil {
	private static Logger logger = MiscUtils.getLogger();

	public static DemographicManager demographicManager =  SpringUtils.getBean(DemographicManager.class);
	public static DemographicArchiveDao demographicArchiveDao = (DemographicArchiveDao) SpringUtils.getBean("demographicArchiveDao");

	public static boolean wasRostered(LoggedInInfo loggedInInfo, Integer demographicId, Date onThisDate) {
		logger.debug("Checking rosterd:" + demographicId);
		Demographic demographic = demographicManager.getDemographic(loggedInInfo, demographicId);

		if (rosteredDuringThisTimeDemographic(onThisDate, demographic.getRosterDate(), demographic.getRosterTerminationDate())) return (true);

		List<DemographicArchive> archives = demographicArchiveDao.findByDemographicNo(demographicId);
		for (DemographicArchive archive : archives) {
			if (rosteredDuringThisTimeDemographicArchive(onThisDate, archive.getRosterDate(), archive.getRosterTerminationDate())) return (true);
		}

		return (false);
	}
	
	public static boolean wasRosteredToThisProvider(LoggedInInfo loggedInInfo, Integer demographicId, Date onThisDate,String providerNo) {
		logger.debug("Checking rosterd:" + demographicId+ " for this date "+onThisDate+" for this providerNo "+providerNo);
		if(providerNo == null){
			return false;
		}
		
		
		Demographic demographic = demographicManager.getDemographic(loggedInInfo, demographicId);

		if (rosteredDuringThisTimeDemographic(onThisDate, demographic.getRosterDate(), demographic.getRosterTerminationDate()) && providerNo.equals(demographic.getProviderNo())) return (true);

		List<DemographicArchive> archives = demographicArchiveDao.findByDemographicNo(demographicId);
		for (DemographicArchive archive : archives) {
			if (rosteredDuringThisTimeDemographicArchive(onThisDate, archive.getRosterDate(), archive.getRosterTerminationDate()) && providerNo.equals(demographic.getProviderNo())) return (true);
		}

		return (false);
	}

	private static boolean rosteredDuringThisTimeDemographic(Date onThisDate, Date rosterStart, Date rosterEnd) {

		if (rosterStart != null) {
			if (rosterStart.before(onThisDate)) {
				if (rosterEnd == null || rosterEnd.after(onThisDate)) {
					logger.debug("true:" + onThisDate + ", " + rosterStart + ", " + rosterEnd);
					return (true);
				}
			}
		}

		logger.debug("false:" + onThisDate + ", " + rosterStart + ", " + rosterEnd);
		return (false);
	}

	private static boolean rosteredDuringThisTimeDemographicArchive(Date onThisDate, Date rosterStart, Date rosterEnd) {
		// algorithm for demographic archive must only look at archiv erecords with end dates as the archive is populated upon every change not just people being unrostered.
		if (rosterStart != null && rosterEnd != null) {
			if (rosterStart.before(onThisDate) && rosterEnd.after(onThisDate)) {
				logger.debug("true:" + onThisDate + ", " + rosterStart + ", " + rosterEnd);
				return (true);
			}
		}

		logger.debug("false:" + onThisDate + ", " + rosterStart + ", " + rosterEnd);
		return (false);
	}
}
