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

package org.oscarehr.hospitalReportManager.service;

import org.oscarehr.common.io.XMLFile;
import org.oscarehr.dataMigration.converter.in.hrm.HrmDocumentModelToDbConverter;
import org.oscarehr.dataMigration.converter.out.hrm.HrmDocumentDbToModelConverter;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.hospitalReportManager.HRMReport;
import org.oscarehr.hospitalReportManager.HRMReportParser;
import org.oscarehr.hospitalReportManager.converter.HRMToPDFConverter;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToDemographicDao;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToDemographic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class HRMDocumentService
{
	@Autowired
	protected HRMDocumentDao hrmDocumentDao;

	@Autowired
	protected HRMDocumentToDemographicDao hrmDocumentToDemographicDao;

	@Autowired
	protected HrmDocumentDbToModelConverter entityToModel;

	@Autowired
	protected HrmDocumentModelToDbConverter modelToEntity;

	@Autowired
	protected HRMToPDFConverter hrmToPDFConverter;

	public HrmDocument getHrmDocument(int hrmDocumentId)
	{
		HRMDocument document = hrmDocumentDao.find(hrmDocumentId);
		return entityToModel.convert(document);
	}

	public int updateHrmDocument(HrmDocument model)
	{
		HRMDocument entity = modelToEntity.convert(model);
		hrmDocumentDao.merge(entity);

		return entity.getId();
	}

	public List<HrmDocument> findForDemographic(Integer demographicId)
	{
		List<HRMDocumentToDemographic> documentToDemographicList = hrmDocumentToDemographicDao.findByDemographicNo(demographicId);
		return entityToModel.convert(
				documentToDemographicList.stream()
						.map(HRMDocumentToDemographic::getHrmDocument)
						.collect(Collectors.toList())
		);
	}

	public InputStream toPdfInputStream(HrmDocument reportModel) throws IOException, JAXBException, SAXException
	{
		return toPdfInputStream(HRMReportParser.parseReport(new XMLFile(reportModel.getReportFile().getFileObject()), reportModel.getReportFileSchemaVersion()));
	}
	public InputStream toPdfInputStream(HRMReport report) throws IOException
	{
		return hrmToPDFConverter.convert(report).toFileInputStream();
	}
}