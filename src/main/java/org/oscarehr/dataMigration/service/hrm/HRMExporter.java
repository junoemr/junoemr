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
package org.oscarehr.dataMigration.service.hrm;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.mapper.hrm.out.HRMExportMapper;
import org.oscarehr.dataMigration.model.PatientRecord;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.dataMigration.parser.hrm.HRMFileParser;
import org.oscarehr.dataMigration.service.DemographicExporter;
import org.oscarehr.dataMigration.pref.ExportPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xml.hrm.v4_3.OmdCds;

import java.util.ArrayList;
import java.util.List;

@Service
public class HRMExporter implements DemographicExporter
{
	@Autowired
	private HRMExportMapper hrmExportMapper;

	public HRMExporter()
	{
	}

	public GenericFile exportDemographic(PatientRecord patientRecord) throws Exception
	{
		HRMFileParser parser = new HRMFileParser();
		OmdCds omdCds = hrmExportMapper.exportFromJuno(patientRecord);

		GenericFile exportFile = parser.write(omdCds);
		exportFile.rename(getHrmDocumentFilename(patientRecord));
		return exportFile;
	}

	@Override
	public List<GenericFile> getAdditionalFiles(ExportPreferences preferences)
	{
		return new ArrayList<>(0);
	}

	private String getHrmDocumentFilename(PatientRecord patientRecord)
	{
		HrmDocument firstDocument = patientRecord.getHrmDocumentList().get(0);

		String name = "-HRM_" + StringUtils.trimToEmpty(firstDocument.getSendingFacilityId()) + "-" +
				StringUtils.trimToEmpty(firstDocument.getSendingFacilityReport()) + "_" +
				 ".xml";

		return GenericFile.getFormattedFileName(name);
	}

	@Override
	public String getSchemaVersion()
	{
		return HRMFileParser.SCHEMA_VERSION;
	}
}
