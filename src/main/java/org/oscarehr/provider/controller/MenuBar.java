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

package org.oscarehr.provider.controller;

import org.oscarehr.common.model.Dashboard;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.managers.AppManager;
import org.oscarehr.managers.DashboardManager;
import org.oscarehr.preferences.service.SystemPreferenceService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MenuBar
{
	private ServletRequest request;
	private HttpSession session;

	private AppManager appManager;
	private LoggedInInfo loggedInInfo;
	private SystemPreferenceService systemPreferences;

	private List<Dashboard> dashboards;

	private int currentYear = 0;
	private int currentMonth = 0;
	private int currentDay = 0;

	public MenuBar(ServletRequest request, HttpSession session)
	{
		this.request = request;
		this.session = session;

		loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(this.session);

		GregorianCalendar cal = new GregorianCalendar();
		currentYear = cal.get(Calendar.YEAR);
		currentMonth = (cal.get(Calendar.MONTH)+1);
		currentDay = cal.get(Calendar.DAY_OF_MONTH);

		this.appManager = SpringUtils.getBean(AppManager.class);
		this.systemPreferences = SpringUtils.getBean(SystemPreferenceService.class);

		this.dashboards = this.loadDashboards();
	}

	public boolean isInfirmaryOscarView()
	{
		String isOscar = (String) session.getAttribute("infirmaryView_isOscar");

		if("true".equals(isOscar))
		{
				return true;
		}

		return false;
	}

	public boolean isViewAll()
	{
		return (
			this.request.getParameter("viewall") != null &&
			this.request.getParameter("viewall").equals("1")
		);
	}

	public boolean isK2AEnabled()
	{
		return this.systemPreferences.isPreferenceEnabled(UserProperty.INTEGRATION_KNOW2ACT_ENABLED, false) &&
				       this.appManager.isK2AEnabled();
	}

	public boolean isHasDashboards()
	{
		return (this.dashboards != null && this.dashboards.size() > 0);
	}

	public List<Dashboard> getDashboards()
	{
		return this.dashboards;
	}

	public int getCurrentYear()
	{
		return currentYear;
	}

	public int getCurrentMonth()
	{
		return currentMonth;
	}

	public int getCurrentDay()
	{
		return currentDay;
	}

	private List<Dashboard> loadDashboards()
	{
		if (OscarProperties.getInstance().isPropertyActive("enable_dashboards")
				&& OscarProperties.getInstance().isBritishColumbiaInstanceType())
		{
			DashboardManager dashboardManager = SpringUtils.getBean(DashboardManager.class);
			return dashboardManager.getActiveDashboards(this.loggedInInfo);
		}
		else
		{
			return new ArrayList<>();
		}
	}
}
