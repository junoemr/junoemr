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

//Action that takes place when adding or editing template XML
/*
 * GenerateReport.java
 *
 * Created on March 02/2007, 10:47 PM
 *
 */

package oscar.oscarReport.reportByTemplate.actions;


import org.apache.jcs.access.exception.InvalidArgumentException;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.report.reportByTemplate.exception.ReportByTemplateException;
import org.oscarehr.report.reportByTemplate.model.ReportTemplates;
import org.oscarehr.report.reportByTemplate.service.ReportByTemplateService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author apavel (Paul)
 */
public class ManageTemplatesAction extends Action
{
	private static final Logger logger = MiscUtils.getLogger();
	private static ReportByTemplateService reportByTemplateService = SpringUtils.getBean(ReportByTemplateService.class);
	private static SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);


	public ActionForward execute(ActionMapping mapping, ActionForm form,
	                             HttpServletRequest request, HttpServletResponse response)
	{
		String action = request.getParameter("action");
		String templateIdStr = request.getParameter("templateid");
		String templateIdStrReturn = templateIdStr;
		String xmlText = request.getParameter("xmltext");
		boolean adminVerifiedChecked = Boolean.parseBoolean(request.getParameter("admin_verified"));
		String sessionProviderNo = (String) request.getSession().getAttribute("user");

		securityInfoManager.requireOnePrivilege(sessionProviderNo, SecurityInfoManager.CREATE, null, "_admin", "_admin.reporting", "_report");

		String message;
		try
		{
			if(action.equals("add"))
			{
				ReportTemplates template = reportByTemplateService.addTemplate(xmlText, sessionProviderNo, adminVerifiedChecked);
				templateIdStrReturn = String.valueOf(template.getId());
			}
			else if(action.equals("edit"))
			{
				ReportTemplates template = reportByTemplateService.updateTemplate(Integer.parseInt(templateIdStr), xmlText, sessionProviderNo, adminVerifiedChecked);
				templateIdStrReturn = String.valueOf(template.getId());
			}
			else if(action.equals("delete"))
			{
				reportByTemplateService.deleteTemplate(Integer.parseInt(templateIdStr));
				return mapping.findForward("deleted");
			}
			else
			{
				throw new InvalidArgumentException("Invalid action: " + action);
			}
			message = "Success";
		}
		catch(ReportByTemplateException e)
		{
			logger.warn(e.getMessage());
			message = e.getPublicMessage();
		}
		catch(Exception e)
		{
			logger.error("Error", e);
			message = "Error";
		}

		request.setAttribute("message", message);
		request.setAttribute("action", action);
		request.setAttribute("templateid", templateIdStrReturn);
		request.setAttribute("opentext", request.getParameter("opentext"));
		return mapping.findForward("success");
	}

}
