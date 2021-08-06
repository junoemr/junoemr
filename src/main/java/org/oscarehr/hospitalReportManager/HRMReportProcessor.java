package org.oscarehr.hospitalReportManager;

import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.mapper.hrm.in.HRMReportDemographicMapper;
import org.oscarehr.dataMigration.mapper.hrm.in.HRMReportImportMapper;
import org.oscarehr.dataMigration.model.demographic.Demographic;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.search.DemographicCriteriaSearch;
import org.oscarehr.hospitalReportManager.reportImpl.HRMReport_4_3;
import org.oscarehr.hospitalReportManager.service.HRMService;
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
	private HRMService hrmService;
	
	@Autowired
	private DemographicDao demographicDao;
	
	public void processHRMFile_43(GenericFile hrmFile)
	{
		try
		{
			HRMReport hrmReport = HRMReportParser.parseReport(hrmFile, SCHEMA_VERSION);
			HrmDocument model = reportMapper.importToJuno((HRMReport_4_3) hrmReport);
			
			Demographic demographicMatchingData = demoMapper.importToJuno((HRMReport_4_3) hrmReport);
			
			List<org.oscarehr.demographic.model.Demographic> matchingDemographics = findDemographicToLink(demographicMatchingData);
			
			org.oscarehr.demographic.model.Demographic demographicToLink = null;
			
			if (matchingDemographics.size() > 1)
			{
				logger.info("Multiple demographics matched for HRM file, leaving unlinked: " + hrmFile.getPath());
			}
			if (matchingDemographics.size() == 1)
			{
				demographicToLink = matchingDemographics.get(0);
			}
			if (matchingDemographics.size() == 0)
			{
				logger.info("No demographics matched for HRM file: " + hrmFile.getPath());
			}
			
			// sending null here is ok, will not associate with a demographic if one can't be found
			hrmService.uploadNewHRMDocument(model, demographicToLink);
		}
		catch (Exception e)
		{
			logger.error("Could not process HRM file: " + hrmFile.getPath(), e);
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
		
		List<org.oscarehr.demographic.model.Demographic> demographics = demographicDao.criteriaSearch(criteria);
		
		return demographics;
	}
}
