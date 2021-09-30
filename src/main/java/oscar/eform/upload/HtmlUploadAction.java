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


package oscar.eform.upload;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.oscarehr.eform.model.EForm;
import org.oscarehr.eform.service.EFormTemplateService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HtmlUploadAction extends Action
{
	private static Logger logger = MiscUtils.getLogger();
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private EFormTemplateService eFormTemplateService = SpringUtils.getBean(EFormTemplateService.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	                             HttpServletRequest request, HttpServletResponse response)
	{

		String loggedInProviderNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
		String ipAddress = LoggedInInfo.getLoggedInInfoFromSession(request).getIp();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, Permission.EFORM_CREATE);

		HtmlUploadForm fm = (HtmlUploadForm) form;
		FormFile formHtml = fm.getFormHtml();
		try
		{
			String formHtmlStr = StringUtils.readFileStream(formHtml);
			String formName = fm.getFormName();
			String roleType = fm.getRoleType();
			String formSubject = fm.getSubject();
			boolean showLatestFormOnly = fm.isShowLatestFormOnly();
			boolean patientIndependent = fm.isPatientIndependent();
			boolean instanced = fm.isInstanced();
			String formFileName = formHtml.getFileName();

			logger.info("Created new EForm Template");
			EForm eFormTemplate = eFormTemplateService.addEFormTemplate(formName, formSubject, formFileName, formHtmlStr,
					loggedInProviderNo, showLatestFormOnly, patientIndependent, instanced, roleType);
			LogAction.addLogEntry(loggedInProviderNo, null, LogConst.ACTION_ADD, LogConst.CON_EFORM_TEMPLATE, LogConst.STATUS_SUCCESS,
					String.valueOf(eFormTemplate.getId()), ipAddress, eFormTemplate.getFormName());
			request.setAttribute("status", "success");
			return (mapping.findForward("success"));
		}
		catch(Exception e)
		{
			MiscUtils.getLogger().error("Error", e);
			return (mapping.findForward("fail"));
		}

	}
}
