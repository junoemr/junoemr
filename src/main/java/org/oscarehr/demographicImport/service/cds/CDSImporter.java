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

import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.xml.cds.v5_0.model.OmdCds;
import org.oscarehr.demographicImport.mapper.cds.in.CDSImportMapper;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.parser.cds.CDSFileParser;
import org.oscarehr.demographicImport.service.DemographicImporter;
import org.oscarehr.demographicImport.service.ExportPreferences;
import org.oscarehr.demographicImport.service.ImportLogger;
import org.oscarehr.util.MiscUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CDSImporter implements DemographicImporter, ImportLogger
{
	private static final Logger logger = MiscUtils.getLogger();

	public CDSImporter()
	{
	}

	public Demographic importDemographic(GenericFile importFile) throws IOException
	{
		CDSFileParser parser = new CDSFileParser();
		CDSImportMapper mapper = new CDSImportMapper();

		OmdCds elem = parser.parse(importFile);
		return mapper.importToJuno(elem);
	}

	@Override
	public List<GenericFile> getAdditionalFiles(ExportPreferences preferences) throws IOException
	{
		return new ArrayList<>();
	}

	@Override
	public ImportLogger getImportLogger()
	{
		return this;
	}

	@Override
	public void log(String message) throws IOException
	{
		logger.info(message);
	}

	@Override
	public GenericFile getLogFile()
	{
		return null;
	}
}
