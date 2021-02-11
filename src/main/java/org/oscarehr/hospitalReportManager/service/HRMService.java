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
package org.oscarehr.hospitalReportManager.service;

import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographicImport.converter.in.hrm.HrmDocumentModelToDbConverter;
import org.oscarehr.demographicImport.model.hrm.HrmDocument;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.hospitalReportManager.HRMReportParser;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToDemographicDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToProviderDao;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToDemographic;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToProvider;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class HRMService
{
	@Autowired
	private HrmDocumentModelToDbConverter hrmDocumentModelToDbConverter;

	@Autowired
	private HRMDocumentDao hrmDocumentDao;

	@Autowired
	private HRMDocumentToDemographicDao hrmDocumentToDemographicDao;

	@Autowired
	private HRMDocumentToProviderDao hrmDocumentToProviderDao;

	@Autowired
	private DocumentService documentService;

	/**
	 * upload a new HRM document to the database with an associated document reference.
	 * @param hrmDocumentModel - the model
	 * @param demographic - the demographic entity
	 * @return - the persisted HRM entity
	 * @throws IOException
	 */
	public HRMDocument uploadNewHRMDocument(HrmDocument hrmDocumentModel, Demographic demographic) throws IOException
	{
		HRMDocument hrmDocument = hrmDocumentModelToDbConverter.convert(hrmDocumentModel);

		// save document as an unassigned document record
		documentService.uploadNewDemographicDocument(hrmDocument.getDocument(), hrmDocumentModel.getDocument().getFile(), null);

		// persist hrm database info and associated objects through cascade
		HRMReportParser.fillDocumentHashData(hrmDocument, hrmDocumentModel.getReportFile());
		hrmDocumentDao.persist(hrmDocument);
		hrmDocumentModel.getReportFile().moveToHRMDocuments();

		// assign the hrm document to the demographic
		routeToDemographic(hrmDocument, demographic);

		return hrmDocument;
	}

	public void uploadAllNewHRMDocuments(List<HrmDocument> hrmDocumentModels, Demographic demographic) throws IOException
	{
		for(HrmDocument documentModel : hrmDocumentModels)
		{
			uploadNewHRMDocument(documentModel, demographic);
		}
	}

	public void routeToDemographic(HRMDocument document, Demographic demographic)
	{
		HRMDocumentToDemographic hrmDocumentToDemographic = hrmDocumentToDemographicDao.findByHrmDocumentIdAndDemographicNo(document.getId(), demographic.getId());
		if(hrmDocumentToDemographic == null)
		{
			hrmDocumentToDemographic = new HRMDocumentToDemographic();
			hrmDocumentToDemographic.setDemographic(demographic);
			hrmDocumentToDemographic.setHrmDocument(document);
			hrmDocumentToDemographic.setDemographicNo(demographic.getId());
			hrmDocumentToDemographic.setHrmDocumentId(document.getId());
			hrmDocumentToDemographic.setTimeAssigned(new Date());

			hrmDocumentToDemographicDao.persist(hrmDocumentToDemographic);
		}
	}

	public void routeToProvider(HRMDocument document, ProviderData ... providers)
	{
		for(ProviderData provider : providers)
		{
			// check for existing connection first
			HRMDocumentToProvider hrmDocumentToProvider = hrmDocumentToProviderDao.findByHrmDocumentIdAndProviderNo(document.getId(), provider.getId());
			if(hrmDocumentToProvider == null)
			{
				hrmDocumentToProvider = new HRMDocumentToProvider();
				hrmDocumentToProvider.setHrmDocument(document);
				hrmDocumentToProvider.setProviderNo(provider.getId());
				hrmDocumentToProvider.setProvider(provider);
				hrmDocumentToProviderDao.persist(hrmDocumentToProvider);
			}
		}
	}
}
