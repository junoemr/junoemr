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


package oscar.oscarResearch.oscarDxResearch.pageUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import oscar.oscarResearch.oscarDxResearch.bean.dxQuickListBeanHandler;
import oscar.oscarResearch.oscarDxResearch.bean.dxQuickListItemsHandler;
import oscar.oscarResearch.oscarDxResearch.bean.dxResearchBeanHandler;
import oscar.oscarResearch.oscarDxResearch.util.dxResearchCodingSystem;
import oscar.util.ParameterActionForward;

public final class dxSetupResearchAction extends Action {
	private static SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

	public ActionForward execute(ActionMapping mapping,
	                             ActionForm form,
	                             HttpServletRequest request,
	                             HttpServletResponse response)
			throws Exception {

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), Permission.DX_READ);

		dxResearchCodingSystem codingSys = new dxResearchCodingSystem();
		String demographicNo = request.getParameter("demographicNo");
		String providerNo = request.getParameter("providerNo");
		String selectedQuickList = request.getParameter("quickList");
		dxResearchBeanHandler hd = new dxResearchBeanHandler(demographicNo);

		dxQuickListBeanHandler quicklistHd = new dxQuickListBeanHandler(providerNo);

		if (providerNo == null) {
			providerNo = loggedInInfo.getLoggedInProviderNo();
		}
		// if no quick list specified
		if (selectedQuickList == null || selectedQuickList.trim().isEmpty()) {
			// select the last one used (or default)
			selectedQuickList = quicklistHd.getLastUsedQuickList();
		}
		// get the corresponding items from the selected list
		dxQuickListItemsHandler quicklistItemsHd = new dxQuickListItemsHandler(selectedQuickList, providerNo);

		request.setAttribute("codingSystem", codingSys);
		request.setAttribute("allQuickLists", quicklistHd);
		request.setAttribute("allQuickListItems", quicklistItemsHd);
		request.setAttribute("allDiagnostics", hd);
		request.setAttribute("demographicNo", demographicNo);
		request.setAttribute("providerNo", providerNo);

		ParameterActionForward forward = new ParameterActionForward(mapping.findForward("success"));
		forward.addParameter("quickList", selectedQuickList);

		return forward;
	}
}
