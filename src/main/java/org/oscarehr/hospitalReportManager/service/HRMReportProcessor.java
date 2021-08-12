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

import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.converter.in.hrm.HrmDocumentModelToDbConverter;
import org.oscarehr.dataMigration.mapper.hrm.in.HRMReportDemographicMapper;
import org.oscarehr.dataMigration.mapper.hrm.in.HRMReportImportMapper;
import org.oscarehr.dataMigration.model.demographic.Demographic;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.search.DemographicCriteriaSearch;
import org.oscarehr.hospitalReportManager.HRMReport;
import org.oscarehr.hospitalReportManager.HRMReportParser;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.reportImpl.HRMReport_4_3;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.util.List;

@Component
public class HRMReportProcessor
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final String SCHEMA_VERSION = "4.3";
	
	@Autowired
	private HRMReportImportMapper reportMapper;
	
	@Autowired
	private HRMReportDemographicMapper demoMapper;
	
	@Autowired
	private HrmDocumentModelToDbConverter hrmDocumentModelToDbConverter;
	
	@Autowired
	private HRMService hrmService;
	
	@Autowired
	private DemographicDao demographicDao;
	
	public void processHRMFile_43(GenericFile hrmFile)
	{
		try
		{
			HRMReport hrmReport = HRMReportParser.parseReport(hrmFile, SCHEMA_VERSION);
			HrmDocument model = reportMapper.importToJuno((HRMReport_4_3) hrmReport);
			
			HRMDocument hrmDocument = hrmDocumentModelToDbConverter.convert(model);
			HRMReportParser.fillDocumentHashData(hrmDocument, hrmFile);
			
			if (!hrmService.isDuplicateReport(hrmDocument))
			{
				Demographic demographicMatchingData = demoMapper.importToJuno((HRMReport_4_3) hrmReport);
				
				List<org.oscarehr.demographic.model.Demographic> matchingDemographics = findDemographicToLink(demographicMatchingData);
				
				org.oscarehr.demographic.model.Demographic demographicToLink = null;
				
				if (matchingDemographics.size() > 1)
				{
					logger.info(String.format("Multiple demographics matched for HRM file, leaving unlinked: %s", hrmFile.getPath()));
				}
				if (matchingDemographics.size() == 1)
				{
					demographicToLink = matchingDemographics.get(0);
				}
				if (matchingDemographics.size() == 0)
				{
					logger.info(String.format("No demographics matched for HRM file: %s", hrmFile.getPath()));
				}
				
				// sending null here is ok, will not associate with a demographic if one can't be found
				hrmService.persistAndLinkHRMDocument(hrmDocument, demographicToLink);
			}
			else
			{
				logger.info(String.format("Duplicate report hash (%s) for file: %s", model.getHashData().getReportHash(), model.getReportFile().getPath()));
				hrmService.handleDuplicate(hrmDocument);
			}
		}
		catch (Exception e)
		{
			logger.error(String.format("Could not process HRM file: %s", hrmFile.getPath()), e);
		}
	}
	
	private List<org.oscarehr.demographic.model.Demographic> findDemographicToLink(org.oscarehr.dataMigration.model.demographic.Demographic matchingData)
	{
		DemographicCriteriaSearch criteria = new DemographicCriteriaSearch();
		
		if (ConversionUtils.hasContent(matchingData.getHealthNumber()))
		{
			criteria.setHin(matchingData.getHealthNumber());
		}
		
		if (ConversionUtils.hasContent(matchingData.getHealthNumberVersion()))
		{
			criteria.setHealthCardVersion(matchingData.getHealthNumberVersion());
		}
		
		if (ConversionUtils.hasContent(matchingData.getHealthNumberProvinceCode()))
		{
			criteria.setHealthCardProvince(matchingData.getHealthNumberProvinceCode());
		}
		
		if (matchingData.getDateOfBirth() != null)
		{
			criteria.setDateOfBirth(matchingData.getDateOfBirth());
		}
		
		return demographicDao.criteriaSearch(criteria);
	}
}
