package org.oscarehr.dataMigration.mapper.hrm.in;

import org.oscarehr.dataMigration.model.demographic.Demographic;
import org.oscarehr.hospitalReportManager.reportImpl.HRMReport_4_3;

public class HRMReportDemographicMapper extends AbstractHRMImportMapper<HRMReport_4_3, Demographic>
{
	
	@Override
	public Demographic importToJuno(HRMReport_4_3 importStructure) throws Exception
	{
		Demographic demographic = new Demographic();
		
		// Stub on matching criteria, which are the heallth number and birthday
		demographic.setHealthNumber(importStructure.getHCN());
		demographic.setHealthNumberProvinceCode(importStructure.getHCNProvinceCode());
		demographic.setHealthNumberVersion(importStructure.getHCNVersion());
	}
}
