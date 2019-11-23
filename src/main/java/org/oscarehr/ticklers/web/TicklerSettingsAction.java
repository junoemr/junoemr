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

package org.oscarehr.ticklers.web;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.common.model.Provider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TicklerSettingsAction extends DispatchAction
{
	private UserPropertyDAO userPropertyDAO = SpringUtils.getBean(UserPropertyDAO.class);

	/**
	 * set Tickler settings
	 * @param mapping - action mapping
	 * @param form - tickler settings form
	 * @param request - request
	 * @param response - response
	 * @return - close popup or back to tickler settings page
	 */
	public ActionForward setSettings(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		TicklerSettingsForm ticklerSettingsForm = (TicklerSettingsForm)form;
		Provider provider = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProvider();

		try
		{
			userPropertyDAO.saveProp(provider.getProviderNo(),
							UserProperty.TICKLER_VIEW_ONLY_MINE,
							ticklerSettingsForm.getTicklerViewOnlyMine().toString());
			return mapping.findForward("success");
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error("Failed to update tickler settings with error: " + e.getMessage(), e);
			return mapping.findForward("error");
		}
	}
}
