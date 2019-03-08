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

import org.oscarehr.common.dao.ConsultDocsDao;
import org.oscarehr.consultations.model.ConsultDocs;
import org.oscarehr.eform.model.EFormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.eform.EFormUtil;

import java.util.ArrayList;
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

	private List<EFormData> getEFormList(Integer demographicNo, Integer consultId, boolean isAttached)
	{
		// TODO this could be refined to a single query
		List<EFormData> eforms = EFormUtil.listPatientEFormsShowLatestOnly(demographicNo.toString());
		List<ConsultDocs> docs = consultDocsDao.findByRequestIdAndType(consultId, ConsultDocs.DOCTYPE_EFORM);

		int listSize = isAttached ? docs.size() : eforms.size() - docs.size();
		List<EFormData> returnList = new ArrayList<>(listSize);

		for (EFormData eform : eforms)
		{
			boolean found = false;
			for (ConsultDocs doc : docs)
			{
				if (eform.getId().equals(doc.getId()))
				{
					found = true; break;
				}
			}
			if (isAttached == found)
			{
				returnList.add(eform);
			}
		}
		return returnList;
	}


	public void setAttachedEforms(Integer consultId, String providerNo, List<Integer> idList)
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
				if(consultDoc.getId().equals(docId))
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
