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
package org.oscarehr.demographicImport.service;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.xml.cds.v5_0.model.OmdCds;
import org.oscarehr.demographicImport.mapper.cds.CDSDemographicImportExportMapper;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.parser.cds.CDSFileParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ImportExportService
{
	private static final Logger logger = Logger.getLogger(ImportExportService.class);

	public void importDemographic(GenericFile importFile) throws IOException
	{
		CDSFileParser parser = new CDSFileParser();
		CDSDemographicImportExportMapper mapper = new CDSDemographicImportExportMapper();

		OmdCds elem = parser.parse(importFile);
		logger.info(ReflectionToStringBuilder.toString(elem));
		Demographic junoImportObject = mapper.importToJuno(elem);

		logger.info(ReflectionToStringBuilder.toString(junoImportObject));

//		I parsedImportObject = parser.parse(importFile);
//		E junoTransientObject = mapper.importToJuno(parsedImportObject);
//		// TODO persist the transient object structure
	}

	public void exportDemographic(Demographic junoTransientObject) throws IOException
	{
//		// TODO load the transient object structure
//		I formatObject = mapper.exportFromJuno(junoTransientObject);
//		parser.write(formatObject);
	}
}
