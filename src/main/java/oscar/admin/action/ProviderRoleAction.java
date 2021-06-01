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

import com.quatro.model.security.Secuserrole;
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
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProviderRoleAction extends DispatchAction
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private static final ProviderRoleService providerRoleService = SpringUtils.getBean(ProviderRoleService.class);

	public ActionForward addRole(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		String currentProviderNo = (String) request.getSession().getAttribute("user");
		String ip = request.getRemoteAddr();
		String providerId = request.getParameter("providerId");
		Integer newRoleId = Integer.parseInt(request.getParameter("roleNew"));

		try
		{
			logger.info("ADD ROLE");
			securityInfoManager.requireAllPrivilege(currentProviderNo, Permission.CONFIGURE_PROVIDER_CREATE);
			securityInfoManager.requireUserCanModify(currentProviderNo, providerId);

			if(!providerRoleService.hasRole(providerId, newRoleId))
			{
				Secuserrole role = providerRoleService.addRole(providerId, newRoleId);
				LogAction.addLogEntry(currentProviderNo, null, LogConst.ACTION_ADD, LogConst.CON_ROLE, LogConst.STATUS_SUCCESS,
						String.valueOf(role.getId()), ip, providerId + "|" + newRoleId);
				request.setAttribute("message", "Role " + newRoleId + " is added. (" + providerId + ")");
			}
			else
			{
				request.setAttribute("message", "Role " + newRoleId + " already exists (" + providerId + ")");
			}

		}
		catch (SecurityException se)
		{
			request.setAttribute("messageNotAuthorized", "true");
			return mapping.findForward("failure");
		}
		catch(Exception e)
		{
			logger.error("Error", e);
			LogAction.addLogEntry(currentProviderNo, null, LogConst.ACTION_DELETE, LogConst.CON_ROLE, LogConst.STATUS_FAILURE,
					null, ip, providerId + "|" + newRoleId);
			request.setAttribute("message", "Role " + newRoleId + " <font color='red'>NOT</font> added!!! (" + providerId + ")");
			return mapping.findForward("failure");
		}
		return mapping.findForward("success");
	}

	public ActionForward updateRole(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		String currentProviderNo = (String) request.getSession().getAttribute("user");
		String ip = request.getRemoteAddr();
		String providerId = request.getParameter("providerId");
		Integer secUserRoleId = Integer.parseInt(request.getParameter("userRoleId"));
		String roleNameOld = request.getParameter("roleOld");
		Integer secRoleId = Integer.parseInt(request.getParameter("roleNew"));

		try
		{
			logger.info("UPDATE ROLE");
			securityInfoManager.requireAllPrivilege(currentProviderNo, Permission.CONFIGURE_PROVIDER_UPDATE);
			securityInfoManager.requireUserCanModify(currentProviderNo, providerId);

			providerRoleService.updateRole(currentProviderNo, providerId, secUserRoleId, secRoleId);

			LogAction.addLogEntry(currentProviderNo, null, LogConst.ACTION_UPDATE, LogConst.CON_ROLE, LogConst.STATUS_SUCCESS,
					String.valueOf(secUserRoleId), ip, providerId + "|" + roleNameOld + ">" + secRoleId);
		}
		catch (SecurityException se)
		{
			request.setAttribute("messageNotAuthorized", "true");
			return mapping.findForward("failure");
		}
		catch(Exception e)
		{
			logger.error("Error", e);
			LogAction.addLogEntry(currentProviderNo, null, LogConst.ACTION_DELETE, LogConst.CON_ROLE, LogConst.STATUS_FAILURE,
					String.valueOf(secUserRoleId), ip, providerId + "|" + roleNameOld);
			request.setAttribute("message", "Role " + roleNameOld + " is <font color='red'>NOT</font> updated!!! (" + providerId + ")");
			return mapping.findForward("failure");
		}

		return mapping.findForward("success");
	}

	public ActionForward deleteRole(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		String currentProviderNo = (String) request.getSession().getAttribute("user");
		String ip = request.getRemoteAddr();
		String providerId = request.getParameter("providerId");
		Integer secUserRoleId = Integer.parseInt(request.getParameter("userRoleId"));
		String roleOld = request.getParameter("roleOld");
		Integer newRoleId = Integer.parseInt(request.getParameter("roleNew"));

		try
		{
			logger.info("DELETE ROLE");
			securityInfoManager.requireAllPrivilege(currentProviderNo, Permission.CONFIGURE_PROVIDER_DELETE);
			securityInfoManager.requireUserCanModify(currentProviderNo, providerId);

			providerRoleService.deleteRole(currentProviderNo, providerId, secUserRoleId);

			LogAction.addLogEntry(currentProviderNo, null, LogConst.ACTION_DELETE, LogConst.CON_ROLE, LogConst.STATUS_SUCCESS,
					String.valueOf(newRoleId), ip, providerId + "|" + roleOld);
			request.setAttribute("message", "Role " + roleOld + " is deleted. (" + providerId + ")");
		}
		catch (SecurityException se)
		{
			request.setAttribute("messageNotAuthorized", "true");
			return mapping.findForward("failure");
		}
		catch(Exception e)
		{
			logger.error("Error", e);
			LogAction.addLogEntry(currentProviderNo, null, LogConst.ACTION_DELETE, LogConst.CON_ROLE, LogConst.STATUS_FAILURE,
					String.valueOf(newRoleId), ip, providerId + "|" + roleOld);
			request.setAttribute("message", "Role " + roleOld + " is <font color='red'>NOT</font> deleted!!! (" + providerId + ")");
			return mapping.findForward("failure");
		}
		return mapping.findForward("success");
	}
}
