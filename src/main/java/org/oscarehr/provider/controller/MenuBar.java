/**
 * Copyright (c) 2013-2015. Department of Computer Science, University of Victoria. All Rights Reserved.
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
 * Department of Computer Science
 * LeadLab
 * University of Victoria
 * Victoria, Canada
 */
package org.oscarehr.provider.controller;

//import org.oscarehr.provider.service.MenuBar;
import org.oscarehr.common.model.Dashboard;
import org.oscarehr.managers.AppManager;
import org.oscarehr.managers.DashboardManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Component
public class MenuBar
{
	private ServletRequest request;
	private HttpSession session;

	private AppManager appManager;
	private LoggedInInfo loggedInInfo;

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

		org.oscarehr.provider.service.MenuBar menuBar = new org.oscarehr.provider.service.MenuBar();

		this.appManager = SpringUtils.getBean(AppManager.class);

		this.dashboards = this.loadDashboards();

		/*
		roleName = session.getAttribute("userrole") + "," + session.getAttribute("user");
		//public scheduleMenuBarTo


		OscarProperties oscarProperties = OscarProperties.getInstance();

		String resourceBaseUrl =  oscarProperties.getProperty("resource_base_url");


		int year = Integer.parseInt(request.getParameter("year"));
		int month = Integer.parseInt(request.getParameter("month"));
		int day = Integer.parseInt(request.getParameter("day"));

		String roleName$ = session.getAttribute("userrole") + "," + session.getAttribute("user");
		String curUser_no = (String) session.getAttribute("user");
		String userFirstName = (String) session.getAttribute("userfirstname");
		String userLastName = (String) session.getAttribute("userlastname");
		String prov = oscarProperties.getBillingTypeUpperCase();
		*/
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
		return this.appManager.isK2AEnabled();
	}

	public boolean hasDashboards()
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
		DashboardManager dashboardManager = SpringUtils.getBean(DashboardManager.class);
		return dashboardManager.getActiveDashboards(this.loggedInInfo);
	}
}
