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

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.consultations.service.ConsultationAttachmentService;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import oscar.dms.EDoc;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConsultationGetAttachedAction extends Action
{
	private static ConsultationAttachmentService consultationAttachmentService = SpringUtils.getBean(ConsultationAttachmentService.class);

	private static final String LABEL_ADDED = "...";
	private static final int LABEL_MAX_LEN = 19;

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String demoNo = request.getParameter("demo");
		String requestId = request.getParameter("requestId");

		List<String> labLabels;
		List<String> docLabels;
		List<String> eFormLabels;
		List<String> hrmLabels;

		if(StringUtils.isNumeric(demoNo) && StringUtils.isNumeric(requestId))
		{
			List<LabResultData> labs = consultationAttachmentService.getAttachedLabs(loggedInInfo, demoNo, requestId);
			labLabels = getLabLabels(labs);

			List<EDoc> privateDocs = consultationAttachmentService.getAttachedDocuments(loggedInInfo, demoNo, requestId);
			docLabels = getDocumentLabels(privateDocs);

			List<EFormData> eFormList = consultationAttachmentService.getAttachedEForms(Integer.parseInt(demoNo), Integer.parseInt(requestId));
			eFormLabels = getEFormLabels(eFormList);

			List<HrmDocument> hrmList = consultationAttachmentService.getAttachedHRMList(Integer.parseInt(demoNo), Integer.parseInt(requestId));
			hrmLabels = getHRMLabels(hrmList);
		}
		else
		{
			labLabels = new ArrayList<>(0);
			docLabels = new ArrayList<>(0);
			eFormLabels = new ArrayList<>(0);
			hrmLabels = new ArrayList<>(0);
		}

		request.setAttribute("docArray", docLabels);
		request.setAttribute("labArray", labLabels);
		request.setAttribute("eFormArray", eFormLabels);
		request.setAttribute("hrmLabels", hrmLabels);

		return mapping.findForward("success");
	}

	private List<String> getEFormLabels(List<EFormData> eFormList)
	{
		List<String> labels = new ArrayList<>(eFormList.size());
		for(EFormData eForm : eFormList)
		{
			String label = eForm.getFormName();
			labels.add(StringUtils.maxLenString(label, LABEL_MAX_LEN, LABEL_MAX_LEN-LABEL_ADDED.length(), LABEL_ADDED));
		}
		return labels;
	}

	private List<String> getDocumentLabels(List<EDoc> docList)
	{
		List<String> labels = new ArrayList<>(docList.size());
		for(EDoc doc : docList)
		{
			String label = doc.getDescription();
			labels.add(StringUtils.maxLenString(label, LABEL_MAX_LEN, LABEL_MAX_LEN-LABEL_ADDED.length(), LABEL_ADDED));
		}
		return labels;
	}

	private List<String> getLabLabels(List<LabResultData> labResultList)
	{
		List<String> labels = new ArrayList<>(labResultList.size());
		for(LabResultData lab : labResultList)
		{
			String label = lab.getDiscipline()+" "+lab.getDateTime();
			labels.add(StringUtils.maxLenString(label, LABEL_MAX_LEN, LABEL_MAX_LEN-LABEL_ADDED.length(), LABEL_ADDED));
		}
		return labels;
	}

	private List<String> getHRMLabels(List<HrmDocument> hrmList)
	{
		return hrmList.stream()
				.map(hrmDocument -> StringUtils.maxLenString(
						hrmDocument.getDescription(), LABEL_MAX_LEN, LABEL_MAX_LEN - LABEL_ADDED.length(), LABEL_ADDED))
				.collect(Collectors.toList());
	}
}