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

/*
 * PreventionReportAction.java
 *
 * Created on May 30, 2005, 7:52 PM
 */

package oscar.oscarPrevention.pageUtil;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.report.prevention.model.PreventionReportModel;
import org.oscarehr.report.prevention.service.PreventionReportService;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.oscarPrevention.reports.PreventionReport;
import oscar.util.ConversionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jay Gallagher
 */
public class PreventionReportAction extends Action
{
	private static final Logger log = MiscUtils.getLogger();
	private static final SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private static final PreventionReportService preventionReportService = SpringUtils.getBean(PreventionReportService.class);

	public PreventionReportAction() {
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), Permission.REPORT_READ, Permission.PREVENTION_READ);
		log.info("PREVENTION REPORT");

		String setName = request.getParameter("patientSet");
		String prevention = request.getParameter("prevention");
		String asofDate = ((PreventionReportForm) form).asofDate;

		/* some fast error checking */
		if("-1".equals(setName)) {
			request.setAttribute("error", "No patient set selected");
			return (mapping.findForward("failure"));
		}
		if("-1".equals(prevention)) {
			request.setAttribute("error", "No prevention selected");
			return (mapping.findForward("failure"));
		}
		
		try {
			log.debug("setting prevention type to " + prevention);

			PreventionReportModel transfer = preventionReportService.runPreventionReport(
					loggedInInfo,
					setName,
					ConversionUtils.fromDateString(asofDate),
					PreventionReport.PreventionReportType.fromStringIgnoreCase(prevention));
			request.setAttribute("report", transfer);
		}
		catch(Exception e)
		{
			log.error("Prevention Report Error", e);
			request.setAttribute("error", "An unknown error has occured");
			return (mapping.findForward("failure"));
		}
		return (mapping.findForward("success"));
	}

}
