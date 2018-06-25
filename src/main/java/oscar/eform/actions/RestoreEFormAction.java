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


package oscar.eform.actions;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.eform.model.EForm;
import org.oscarehr.eform.service.EFormTemplateService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RestoreEFormAction extends Action {

	private static Logger logger = MiscUtils.getLogger();
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private EFormTemplateService eFormTemplateService = SpringUtils.getBean(EFormTemplateService.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	                             HttpServletRequest request, HttpServletResponse response)
	{
		String fid = request.getParameter("fid");
		try
		{
			logger.info("Restore EForm Template (id: " + fid + ")");

			if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_eform", "w", null))
			{
				throw new SecurityException("missing required security object (_eform)");
			}

			LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
			String providerNo = loggedInInfo.getLoggedInProviderNo();

			EForm eFormTemplate = eFormTemplateService.restoreTemplate(Integer.parseInt(fid));
			LogAction.addLogEntry(providerNo, null, LogConst.ACTION_RESTORE, LogConst.CON_EFORM_TEMPLATE, LogConst.STATUS_SUCCESS,
					String.valueOf(eFormTemplate.getId()), loggedInInfo.getIp(), eFormTemplate.getFormName());
		}
		catch(IllegalArgumentException e)
		{
			logger.error("Invalid Form Id: " + fid, e);
			request.setAttribute("error","Invalid Form Id: " + fid);
			return mapping.findForward("failure");
		}
		catch(SecurityException e)
		{
			logger.error("Security Error", e);
			request.setAttribute("error","Security Error");
			return mapping.findForward("failure");
		}
		return mapping.findForward("success");
	}
}
