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
import org.oscarehr.consultations.model.ConsultDocs;
import org.oscarehr.consultations.service.ConsultationAttachmentService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConsultationAttachDocsAction extends Action {

	private static Logger logger = Logger.getLogger(ConsultationAttachDocsAction.class);

	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private ConsultationAttachmentService consultationAttachmentService = SpringUtils.getBean(ConsultationAttachmentService.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)

	throws ServletException, IOException {

		if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_con", "u", null)) {
			throw new SecurityException("missing required security object (_con)");
		}

		DynaActionForm frm = (DynaActionForm) form;
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

		String requestId = frm.getString("requestId");
		String demoNo = frm.getString("demoNo");
		String provNo = frm.getString("providerNo");
		
		boolean demoNoValid = StringUtils.isNumeric(demoNo) && !demoNo.trim().isEmpty() && !demoNo.equalsIgnoreCase("0");
		boolean provNoValid = provNo != null && !provNo.equalsIgnoreCase("null");
		boolean requestIdValid = StringUtils.isNumeric(requestId) && !requestId.trim().isEmpty() && !requestId.equalsIgnoreCase("0");

		if (demoNoValid && provNoValid && requestIdValid) {

			String[] labs;
			String[] docs;
			String[] eforms;

			if(OscarProperties.getInstance().isPropertyActive("consultation_indivica_attachment_enabled"))
			{
				labs = request.getParameterValues("labNo");
				docs = request.getParameterValues("docNo");
				eforms = request.getParameterValues("eFormNo");
			}
			else
			{
				labs = frm.getStrings("attachedDocs");
				docs = frm.getStrings("attachedDocs");
				eforms = frm.getStrings("attachedDocs");
			}
			if (labs == null) { labs = new String[]{};}
			if (docs == null) { docs = new String[]{};}
			if (eforms == null) { eforms = new String[]{};}

			ConsultationAttachDocs Doc = new ConsultationAttachDocs(provNo, demoNo, requestId, docs);
			Doc.attach(loggedInInfo);

			ConsultationAttachLabs Lab = new ConsultationAttachLabs(provNo, demoNo, requestId, labs);
			Lab.attach(loggedInInfo);

			consultationAttachmentService.setAttachedEforms(Integer.parseInt(requestId), provNo, filterIdList(eforms, ConsultDocs.DOCTYPE_EFORM));
			return mapping.findForward("success");
		}
		logger.error("Invalid consultation document parameters " +
				"(provider:" + provNo + ",demoNo:" + demoNo + ",requestId:" + requestId + "). Save attempt aborted.");
		return mapping.findForward("failure");
	}

	/**
	 * filter the attachedDocs id list on prefix. ids start with a doctype letter, followed by the integerID value for that attachment
	 * @param idList
	 * @param filterPrefix
	 * @return filtered list converted to integers
	 */
	private List<Integer> filterIdList(String[] idList, String filterPrefix)
	{
		List<Integer> filterdList = new ArrayList<>();
		for(String id : idList)
		{
			if(id.startsWith(filterPrefix))
			{
				filterdList.add(Integer.parseInt(id.substring(filterPrefix.length())));
			}
		}
		return filterdList;
	}
}
