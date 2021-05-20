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
package org.oscarehr.dataMigration.service.cds;

import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.XMLFile;
import org.oscarehr.dataMigration.exception.InvalidImportFileException;
import org.oscarehr.dataMigration.mapper.cds.in.CDSImportMapper;
import org.oscarehr.dataMigration.model.PatientRecord;
import org.oscarehr.dataMigration.parser.cds.CDSFileParser;
import org.oscarehr.dataMigration.service.DemographicImporter;
import org.oscarehr.dataMigration.pref.ExportPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.log.LogAction;
import xml.cds.v5_0.OmdCds;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class CDSImporter implements DemographicImporter
{
	@Autowired
	private CDSImportMapper cdsImportMapper;

	public CDSImporter()
	{
	}

	@Override
	public void verifyFileFormat(GenericFile importFile) throws InvalidImportFileException
	{
		if(importFile instanceof XMLFile)
		{
			//TODO content validation. hopefully through the schema
			return;
		}
		throw new InvalidImportFileException();
	}

	public PatientRecord importDemographic(GenericFile importFile) throws Exception
	{
		Instant instant = Instant.now();
		CDSFileParser parser = new CDSFileParser();

		OmdCds elem = parser.parse(importFile);
		instant = LogAction.printDuration(instant, "Importer: file parse to CDS structure");

		PatientRecord patientRecord = cdsImportMapper.importToJuno(elem);
		instant = LogAction.printDuration(instant, "Importer: CDS structure to model conversion");
		return patientRecord;
	}

	@Override
	public List<GenericFile> getAdditionalFiles(ExportPreferences preferences)
	{
		return new ArrayList<>();
	}
}
