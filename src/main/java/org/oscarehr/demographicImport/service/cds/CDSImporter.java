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
package org.oscarehr.demographicImport.service.cds;

import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.XMLFile;
import org.oscarehr.common.xml.cds.v5_0.model.OmdCds;
import org.oscarehr.demographicImport.exception.InvalidImportFileException;
import org.oscarehr.demographicImport.mapper.cds.in.CDSImportMapper;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.parser.cds.CDSFileParser;
import org.oscarehr.demographicImport.service.DemographicImporter;
import org.oscarehr.demographicImport.util.ExportPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
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

	public Demographic importDemographic(GenericFile importFile) throws Exception
	{
		CDSFileParser parser = new CDSFileParser();

		OmdCds elem = parser.parse(importFile);
		return cdsImportMapper.importToJuno(elem);
	}

	@Override
	public List<GenericFile> getAdditionalFiles(ExportPreferences preferences)
	{
		return new ArrayList<>();
	}
}
