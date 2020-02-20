/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.web;

import org.oscarehr.eform.dao.EFormDao;
import org.oscarehr.common.dao.ProviderPreferenceDao;
import org.oscarehr.eform.model.EForm;
import org.oscarehr.common.model.ProviderPreference;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

public final class AppointmentProviderAdminDayUIBean {
	private static EFormDao eFormDao = SpringUtils.getBean(EFormDao.class);
	private static ProviderPreferenceDao providerPreferenceDao = SpringUtils.getBean(ProviderPreferenceDao.class);
	
	public static String getLengthLimitedLinkName(LoggedInInfo loggedInInfo, String formName)
	{
		int maxLength = 3;

		ProviderPreference providerPreference = providerPreferenceDao.find(loggedInInfo.getLoggedInProviderNo());
		if (providerPreference != null && providerPreference.getAppointmentScreenLinkNameDisplayLength() > 0)
		{
			maxLength = providerPreference.getAppointmentScreenLinkNameDisplayLength();
		}
		
		if (formName.length() <= maxLength)
		{
			return formName;
		}

		return formName.substring(0, maxLength - 1) + ".";
	}
	
	public static EForm getEForms(Integer eformId)
	{
		return(eFormDao.find(eformId));
	}
}
