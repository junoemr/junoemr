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
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.oscarPrevention.reports.PreventionReport;
import oscar.oscarPrevention.reports.PreventionReportFactory;
import oscar.oscarReport.data.RptDemographicQueryBuilder;
import oscar.oscarReport.data.RptDemographicQueryLoader;
import oscar.oscarReport.pageUtil.RptDemographicReportForm;
import oscar.util.UtilDateUtilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

/**
 *
 * @author Jay Gallagher
 */
public class PreventionReportAction extends Action {
	private static Logger log = MiscUtils.getLogger();

	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

	public PreventionReportAction() {
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_report", "r", null)) {
			throw new SecurityException("missing required security object (_report)");
		}
		log.info("PREVENTION REPORT");

		String setName = request.getParameter("patientSet");
		String prevention = request.getParameter("prevention");
		Date asofDate = UtilDateUtilities.getDateFromString(request.getParameter("asofDate"), "yyyy-MM-dd");
		if (asofDate == null) {
			Calendar today = Calendar.getInstance();
			asofDate = today.getTime();
		}
		
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
			RptDemographicReportForm frm = new RptDemographicReportForm();
			frm.setSavedQuery(setName);
			RptDemographicQueryLoader demoL = new RptDemographicQueryLoader();
			frm = demoL.queryLoader(frm);
			frm.addDemoIfNotPresent();
			frm.setAsofDate(request.getParameter("asofDate"));
			RptDemographicQueryBuilder demoQ = new RptDemographicQueryBuilder();
			ArrayList<ArrayList<String>> list = demoQ.buildQuery(loggedInInfo, frm, request.getParameter("asofDate"));

			log.debug("set size " + list.size());

			
			PreventionReport report = PreventionReportFactory.getPreventionReport(prevention);
			Hashtable h = report.runReport(loggedInInfo, list, asofDate);

			if (report.displayNumShots())
			{
				request.setAttribute("ReportType", "yes");
			}
			
			request.setAttribute("asDate", asofDate);
			request.setAttribute("up2date", h.get("up2date"));
			request.setAttribute("percent", h.get("percent"));
			request.setAttribute("percentWithGrace", h.get("percentWithGrace"));
			request.setAttribute("returnReport", h.get("returnReport"));
			request.setAttribute("inEligible", h.get("inEligible"));
			request.setAttribute("eformSearch", h.get("eformSearch"));
			request.setAttribute("followUpType", h.get("followUpType"));
			request.setAttribute("BillCode", h.get("BillCode"));

			request.setAttribute("prevType", prevention);
			request.setAttribute("patientSet", setName);
			request.setAttribute("prevention", prevention);

			log.debug("setting prevention type to " + prevention);	
		}
		catch(Exception e) {
			log.error("Prevention Report Error", e);
			request.setAttribute("error", "An unknown error has occured");
			return (mapping.findForward("failure"));
		}
		return (mapping.findForward("success"));
	}

}
