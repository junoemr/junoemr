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
//This action generates the report after the user filled in all the params



package oscar.oscarReport.reportByTemplate.actions;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.oscarReport.reportByTemplate.ReportFactory;
import oscar.oscarReport.reportByTemplate.Reporter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created on December 21, 2006, 10:47 AM
 * @author apavel (Paul)
 */
public class GenerateReportAction extends Action
{
	private static final Logger logger = MiscUtils.getLogger();
	private static SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	                             HttpServletRequest request, HttpServletResponse response)
	{
		String sessionProviderNo = (String) request.getSession().getAttribute("user");
		securityInfoManager.requireAllPrivilege(sessionProviderNo, SecurityInfoManager.READ, null, "_admin", "_report");

		try
		{
			Reporter reporter = ReportFactory.getReporter(request.getParameter("type"));
			if(reporter.generateReport(request))
			{
				return mapping.findForward("success");
			}
		}
		catch(Exception e)
		{
			logger.error("Error", e);
		}
		return mapping.findForward("fail");
	}

}
