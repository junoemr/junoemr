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
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProviderRoleAction extends DispatchAction
{
	private static final Logger logger = MiscUtils.getLogger();
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private ProviderRoleService providerRoleService = SpringUtils.getBean(ProviderRoleService.class);

	public ActionForward addRole(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		String currentProviderNo = (String) request.getSession().getAttribute("user");
		String ip = request.getRemoteAddr();
		Integer providerId = Integer.parseInt(request.getParameter("providerId"));
		String roleNew = request.getParameter("roleNew");

		try
		{
			logger.info("ADD ROLE");
			securityInfoManager.requireOnePrivilege(currentProviderNo, SecurityInfoManager.WRITE, null, "_admin", "_admin.userAdmin");
			securityInfoManager.requireUserCanModify(currentProviderNo, String.valueOf(providerId));

			if(!providerRoleService.validRoleName(roleNew))
			{
				return mapping.findForward("failure");
			}

			if(!providerRoleService.hasRole(providerId, roleNew))
			{
				Secuserrole role = providerRoleService.addRole(providerId, roleNew);
				LogAction.addLogEntry(currentProviderNo, null, LogConst.ACTION_ADD, LogConst.CON_ROLE, LogConst.STATUS_SUCCESS,
						String.valueOf(role.getId()), ip, providerId + "|" + roleNew);
				request.setAttribute("message", "Role " + roleNew + " is added. (" + providerId + ")");
			}
			else
			{
				request.setAttribute("message", "Role " + roleNew + " already exists (" + providerId + ")");
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
					null, ip, providerId + "|" + roleNew);
			request.setAttribute("message", "Role " + roleNew + " <font color='red'>NOT</font> added!!! (" + providerId + ")");
			return mapping.findForward("failure");
		}
		return mapping.findForward("success");
	}

	public ActionForward updateRole(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		String currentProviderNo = (String) request.getSession().getAttribute("user");
		String ip = request.getRemoteAddr();
		String providerId = request.getParameter("providerId");
		String roleId = request.getParameter("roleId");
		String roleOld = request.getParameter("roleOld");
		String roleNew = request.getParameter("roleNew");

		try
		{
			logger.info("UPDATE ROLE");
			securityInfoManager.requireOnePrivilege(currentProviderNo, SecurityInfoManager.UPDATE, null, "_admin", "_admin.userAdmin");
			securityInfoManager.requireUserCanModify(currentProviderNo, providerId);

			if(!providerRoleService.validRoleName(roleNew))
			{
				return mapping.findForward("failure");
			}
			providerRoleService.updateRole(Integer.parseInt(currentProviderNo), Integer.parseInt(providerId), Integer.parseInt(roleId), roleNew);

			LogAction.addLogEntry(currentProviderNo, null, LogConst.ACTION_UPDATE, LogConst.CON_ROLE, LogConst.STATUS_SUCCESS,
					roleId, ip, providerId + "|" + roleOld + ">" + roleNew);
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
					roleId, ip, providerId + "|" + roleOld);
			request.setAttribute("message", "Role " + roleOld + " is <font color='red'>NOT</font> updated!!! (" + providerId + ")");
			return mapping.findForward("failure");
		}

		return mapping.findForward("success");
	}

	public ActionForward deleteRole(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		String currentProviderNo = (String) request.getSession().getAttribute("user");
		String ip = request.getRemoteAddr();
		String providerId = request.getParameter("providerId");
		String roleId = request.getParameter("roleId");
		String roleOld = request.getParameter("roleOld");
		String roleNew = request.getParameter("roleNew");

		try
		{
			logger.info("DELETE ROLE");
			securityInfoManager.requireOnePrivilege(currentProviderNo, SecurityInfoManager.DELETE, null, "_admin", "_admin.userAdmin");
			securityInfoManager.requireUserCanModify(currentProviderNo, providerId);

			if(!providerRoleService.validRoleName(roleNew))
			{
				return mapping.findForward("failure");
			}
			providerRoleService.deleteRole(Integer.parseInt(currentProviderNo), Integer.parseInt(providerId), Integer.parseInt(roleId));

			LogAction.addLogEntry(currentProviderNo, null, LogConst.ACTION_DELETE, LogConst.CON_ROLE, LogConst.STATUS_SUCCESS,
					roleId, ip, providerId + "|" + roleOld);
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
					roleId, ip, providerId + "|" + roleOld);
			request.setAttribute("message", "Role " + roleOld + " is <font color='red'>NOT</font> deleted!!! (" + providerId + ")");
			return mapping.findForward("failure");
		}
		return mapping.findForward("success");
	}
}
