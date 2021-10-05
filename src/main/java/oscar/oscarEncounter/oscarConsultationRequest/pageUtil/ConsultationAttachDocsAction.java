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

package oscar.oscarEncounter.oscarConsultationRequest.pageUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.oscarehr.consultations.service.ConsultationAttachmentService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConsultationAttachDocsAction extends Action
{
	private static final Logger logger = Logger.getLogger(ConsultationAttachDocsAction.class);

	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private ConsultationAttachmentService consultationAttachmentService = SpringUtils.getBean(ConsultationAttachmentService.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		securityInfoManager.requireOnePrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(),
				SecurityInfoManager.CREATE, null, "_con");

		DynaActionForm frm = (DynaActionForm) form;

		String requestIdStr = frm.getString("requestId");
		String demoNo = frm.getString("demoNo");
		String provNo = frm.getString("providerNo");
		Integer requestId = Integer.parseInt(requestIdStr);
		
		boolean demoNoValid = StringUtils.isNumeric(demoNo) && !demoNo.trim().isEmpty() && !demoNo.equalsIgnoreCase("0");
		boolean provNoValid = provNo != null && !provNo.equalsIgnoreCase("null");
		boolean requestIdValid = StringUtils.isNumeric(requestIdStr) && !requestIdStr.trim().isEmpty() && !requestIdStr.equalsIgnoreCase("0");

		if (demoNoValid && provNoValid && requestIdValid)
		{
			List<Integer> labIds = toIntList(request.getParameterValues("labNo"));
			List<Integer> docIds = toIntList(request.getParameterValues("docNo"));
			List<Integer> eformIds = toIntList(request.getParameterValues("eFormNo"));

			consultationAttachmentService.setAttachedDocuments(requestId, provNo, docIds);
			consultationAttachmentService.setAttachedLabs(requestId, provNo, labIds);
			consultationAttachmentService.setAttachedEForms(requestId, provNo, eformIds);
			return mapping.findForward("success");
		}
		logger.error("Invalid consultation document parameters " +
				"(provider:" + provNo + ",demoNo:" + demoNo + ",requestId:" + requestIdStr + "). Save attempt aborted.");
		return mapping.findForward("failure");
	}

	private List<Integer> toIntList(String[] strList)
	{
		List<Integer> intList = new ArrayList<>();
		if(strList != null)
		{
			for(String str : strList)
			{
				intList.add(Integer.parseInt(str));
			}
		}
		return intList;
	}
}
