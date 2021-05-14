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
package oscar.admin.action;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.provider.service.ProviderRoleService;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProgramProviderRoleAction extends DispatchAction
{
	private static final Logger logger = MiscUtils.getLogger();
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private ProviderRoleService providerRoleService = SpringUtils.getBean(ProviderRoleService.class);

	public ActionForward setPrimaryRole(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		String providerNoStr = request.getParameter("primaryRoleProvider");
		String roleIdStr = request.getParameter("primaryRoleRole");
		try
		{
			logger.info("SET PRIMARY ROLE");
			Integer roleId = Integer.parseInt(roleIdStr);

			String currentProviderNo = (String) request.getSession().getAttribute("user");
			securityInfoManager.requireAllPrivilege(currentProviderNo, Permission.ADMIN_CREATE);
			securityInfoManager.requireSuperAdminPrivilege(currentProviderNo, providerNoStr);

			providerRoleService.setPrimaryRole(providerNoStr, roleId);
		}
		catch (SecurityException se)
		{
			request.setAttribute("messageNotAuthorized", "true");
			return mapping.findForward("failure");
		}
		catch(Exception e)
		{
			logger.error("Error", e);
			request.setAttribute("message", "Failed to assign provider role");
			return mapping.findForward("failure");
		}
		return mapping.findForward("success");
	}
}
