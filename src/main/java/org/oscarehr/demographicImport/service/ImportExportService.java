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
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographicImport.converter.DemographicModelToExportConverter;
import org.oscarehr.demographicImport.mapper.cds.in.CDSDemographicImportMapper;
import org.oscarehr.demographicImport.mapper.cds.out.CDSExportMapper;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.parser.cds.CDSFileParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ImportExportService
{
	private static final Logger logger = Logger.getLogger(ImportExportService.class);

	@Autowired
	DemographicDao demographicDao;

	@Autowired
	DemographicModelToExportConverter modelToExportConverter;

	public void importDemographic(GenericFile importFile) throws IOException
	{
		CDSFileParser parser = new CDSFileParser();
		CDSDemographicImportMapper mapper = new CDSDemographicImportMapper();

		OmdCds elem = parser.parse(importFile);
		logger.info(ReflectionToStringBuilder.toString(elem));
		Demographic junoImportObject = mapper.importToJuno(elem.getPatientRecord().getDemographics());

		logger.info(ReflectionToStringBuilder.toString(junoImportObject));

//		I parsedImportObject = parser.parse(importFile);
//		E junoTransientObject = mapper.importToJuno(parsedImportObject);
//		// TODO persist the transient object structure
	}

	public GenericFile exportDemographic(Demographic junoTransientObject) throws IOException
	{
//		// TODO load the transient object structure
//		I formatObject = mapper.exportFromJuno(junoTransientObject);
//		parser.write(formatObject);

		CDSFileParser parser = new CDSFileParser();
		CDSExportMapper mapper = new CDSExportMapper();
		OmdCds omdCds = mapper.exportFromJuno(junoTransientObject);

		return parser.write(omdCds);
	}

	public GenericFile exportDemographic(Integer demographicId) throws IOException
	{
		org.oscarehr.demographic.model.Demographic demographic = demographicDao.find(demographicId);
		Demographic exportDemographic = modelToExportConverter.convert(demographic);
		return this.exportDemographic(exportDemographic);
	}
}
