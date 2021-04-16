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

package org.oscarehr.rx.web;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RxSettingsAction extends DispatchAction
{
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

	/**
	 * bulk set Rx settings. only one right now... perhaps more to come
	 * @param mapping - action mapping
	 * @param form - action form
	 * @param request - request obj
	 * @param response - response obj
	 * @return - forward to jsp page.
	 */
	public ActionForward setSettings(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireOnePrivilege(loggedInInfo.getLoggedInProviderNo(), SecurityInfoManager.CREATE, null,  "_admin");

		String promoText = StringUtils.trimToEmpty(request.getParameter("rx_promo_text"));
		UserPropertyDAO userPropertyDAO = SpringUtils.getBean(UserPropertyDAO.class);
		userPropertyDAO.saveProp(UserProperty.RX_PROMO_TEXT, promoText);

		response.setStatus(200);
		return null;
	}
}
