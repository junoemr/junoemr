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
package org.oscarehr.consultations.service;

import org.oscarehr.consultations.dao.ConsultDocsDao;
import org.oscarehr.consultations.model.ConsultDocs;
import org.oscarehr.document.model.CtlDocument;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.eform.EFormUtil;
import oscar.oscarLab.ca.all.Hl7textResultsData;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ConsultationAttachmentService
{
	@Autowired
	private ConsultDocsDao consultDocsDao;

	public List<EFormData> getAttachedEForms(Integer demographicNo, Integer consultId)
	{
		return getEFormList(demographicNo, consultId, true);
	}

	public List<EFormData> getUnattachedEForms(Integer demographicNo, Integer consultId)
	{
		return getEFormList(demographicNo, consultId, false);
	}

	public List<EFormData> getAllEForms(Integer demographicNo)
	{
		return getEFormList(demographicNo, null, false);
	}

	private List<EFormData> getEFormList(Integer demographicNo, Integer consultId, boolean findAttached)
	{
		// TODO-legacy this could be refined to a single query
		List<EFormData> eForms = EFormUtil.listPatientEFormsShowLatestOnly(demographicNo.toString());
		if(consultId == null && !findAttached)
		{
			// without a consult ID, all eForms are unattached
			return eForms;
		}
		List<ConsultDocs> docs = consultDocsDao.findByRequestIdAndType(consultId, ConsultDocs.DOCTYPE_EFORM);

		int listSize = findAttached? docs.size() : (eForms.size() - docs.size());
		List<EFormData> returnList = new ArrayList<>(listSize);

		for (EFormData eForm : eForms)
		{
			boolean isAttached = false;
			for (ConsultDocs doc : docs)
			{
				if (eForm.getId().equals(doc.getDocumentNo()))
				{
					isAttached = true; break;
				}
			}
			if (findAttached == isAttached)
			{
				returnList.add(eForm);
			}
		}
		return returnList;
	}

	public List<EDoc> getAttachedDocuments(LoggedInInfo loggedInInfo, Integer demographicNo, Integer consultId)
	{
		return getAttachedDocuments(loggedInInfo, String.valueOf(demographicNo), String.valueOf(consultId));
	}
	public List<EDoc> getAttachedDocuments(LoggedInInfo loggedInInfo, String demographicNo, String consultId)
	{
		return EDocUtil.listDocs(loggedInInfo, demographicNo, consultId, EDocUtil.ATTACHED);
	}

	public List<EDoc> getUnattachedDocuments(LoggedInInfo loggedInInfo, Integer demographicNo, Integer consultId)
	{
		return getUnattachedDocuments(loggedInInfo, String.valueOf(demographicNo), String.valueOf(consultId));
	}
	public List<EDoc> getUnattachedDocuments(LoggedInInfo loggedInInfo, Integer demographicNo)
	{
		return getUnattachedDocuments(loggedInInfo, String.valueOf(demographicNo), "null");
	}
	public List<EDoc> getUnattachedDocuments(LoggedInInfo loggedInInfo, String demographicNo, String consultId)
	{
		return EDocUtil.listDocs(loggedInInfo, demographicNo, consultId, EDocUtil.UNATTACHED);
	}
	public List<EDoc> getAllDocuments(LoggedInInfo loggedInInfo, String demographicNo)
	{
		return EDocUtil.listDocs(loggedInInfo, CtlDocument.MODULE_DEMOGRAPHIC, demographicNo, null, EDocUtil.PRIVATE, EDocUtil.EDocSort.OBSERVATIONDATE);
	}

	public List<LabResultData> getAttachedLabs(LoggedInInfo loggedInInfo, Integer demographicNo, Integer consultId)
	{
		return getAttachedLabs(loggedInInfo, String.valueOf(demographicNo), String.valueOf(consultId));
	}
	public List<LabResultData> getAttachedLabs(LoggedInInfo loggedInInfo, String demographicNo, String consultId)
	{
		CommonLabResultData labData = new CommonLabResultData();
		return labData.populateConsultLabResultsData(loggedInInfo, demographicNo, consultId, CommonLabResultData.ATTACHED);
	}

	public List<LabResultData> getUnattachedLabs(LoggedInInfo loggedInInfo, Integer demographicNo, Integer consultId)
	{
		return getUnattachedLabs(loggedInInfo, String.valueOf(demographicNo), String.valueOf(consultId));
	}
	public List<LabResultData> getUnattachedLabs(LoggedInInfo loggedInInfo, Integer demographicNo)
	{
		return getUnattachedLabs(loggedInInfo, String.valueOf(demographicNo), "null");
	}
	public List<LabResultData> getUnattachedLabs(LoggedInInfo loggedInInfo, String demographicNo, String consultId)
	{
		CommonLabResultData labData = new CommonLabResultData();
		List<LabResultData> unfilteredLabs = labData.populateConsultLabResultsData(loggedInInfo, demographicNo, consultId, CommonLabResultData.UNATTACHED);
		return filterLabVersions(unfilteredLabs);
	}
	public List<LabResultData> getAllLabs(LoggedInInfo loggedInInfo, String demographicNo, String consultId)
	{
		//TODO-legacy refactor to single get when lab logic is re-worked
		/* This relies on the unattached lab version being filtered (only latest is shown) but attached labs being unfiltered,
		there is a case where old lab versions are attached and if those are filtered out,
		they will not appear. It may look like a duplicate in this case. */
		List<LabResultData> allLabs = getAttachedLabs(loggedInInfo, demographicNo, consultId);
		allLabs.addAll(getUnattachedLabs(loggedInInfo, demographicNo, consultId));
		Collections.sort(allLabs);
		return allLabs;
	}
	/* This filters lab versions using the old logic from the jsp, so that only the latest version appears
	 * TODO-legacy - refactor all this to the DAO */
	private List<LabResultData> filterLabVersions(List<LabResultData> unfilteredLabs)
	{
		List<LabResultData> filteredLabs = new ArrayList<>(unfilteredLabs.size());
		for(LabResultData lab : unfilteredLabs)
		{
			if(lab.labType.equals(LabResultData.HL7TEXT) && Hl7textResultsData.getMatchingLabs(lab.segmentID).endsWith(lab.segmentID))
			{
				filteredLabs.add(lab);
			}
		}
		return filteredLabs;
	}

	public void setAttachedEForms(Integer consultId, String providerNo, List<Integer> idList)
	{
		setAttached(consultId, providerNo, ConsultDocs.DOCTYPE_EFORM, idList);
	}

	public void setAttachedDocuments(Integer consultId, String providerNo, List<Integer> idList)
	{
		setAttached(consultId, providerNo, ConsultDocs.DOCTYPE_DOC, idList);
	}

	public void setAttachedLabs(Integer consultId, String providerNo, List<Integer> idList)
	{
		setAttached(consultId, providerNo, ConsultDocs.DOCTYPE_LAB, idList);
	}

	private void setAttached(Integer consultId, String providerNo, String docType, List<Integer> idList)
	{
		//first we get a list of currently attached docs
		List<ConsultDocs> oldList = consultDocsDao.findByRequestIdAndType(consultId, docType);
		List<ConsultDocs> newList = new ArrayList<>();
		List<ConsultDocs> keepList = new ArrayList<>();
		boolean alreadyAttached;

		//add new documents to list and get ids of docs to keep attached
		for(Integer docId : idList)
		{
			alreadyAttached = false;
			for(ConsultDocs consultDoc : oldList)
			{
				if(consultDoc.getDocumentNo() == docId)
				{
					alreadyAttached = true;
					keepList.add(consultDoc);
					break;
				}
			}
			if(!alreadyAttached)
			{
				ConsultDocs newConsultDoc = new ConsultDocs();
				newConsultDoc.setDocType(docType);
				newConsultDoc.setAttachDate(new Date());
				newConsultDoc.setDocumentNo(docId);
				newConsultDoc.setProviderNo(providerNo);
				newConsultDoc.setRequestId(consultId);

				newList.add(newConsultDoc);
			}
		}

		//now compare what we need to keep with what we have and remove association
		for(ConsultDocs consultDoc : oldList)
		{
			if (!keepList.contains(consultDoc))
			{
				consultDoc.setDeleted("Y");
				consultDocsDao.merge(consultDoc);
			}
		}

		//now we can add association to new list
		for(ConsultDocs consultDoc : newList)
		{
			consultDocsDao.persist(consultDoc);
		}
	}
}
