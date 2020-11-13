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
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographicImport.converter.DemographicModelToExportConverter;
import org.oscarehr.demographicImport.exception.InvalidImportFileException;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ImportExportService
{
	private static final Logger logger = Logger.getLogger(ImportExportService.class);

	@Autowired
	DemographicDao demographicDao;

	@Autowired
	DemographicModelToExportConverter modelToExportConverter;

	public void importDemographic(ImporterExporterFactory.IMPORTER_TYPE importType,
	                                      ImporterExporterFactory.IMPORT_SOURCE importSource,
	                                      ImportLogger importLogger,
	                                      GenericFile importFile,
	                                      String documentLocation,
	                                      boolean skipMissingDocs,
	                                      boolean mergeDemographics) throws IOException, InvalidImportFileException
	{
		DemographicImporter importer = ImporterExporterFactory.getImporter(importType, importSource, importLogger, documentLocation, skipMissingDocs);
		importer.verifyFileFormat(importFile);
		Demographic demographic = importer.importDemographic(importFile);

		// TODO persist the transient object structure
		// TODO handle demographic merging
		logger.info(ReflectionToStringBuilder.toString(demographic));
	}

	public List<GenericFile> exportDemographics(ImporterExporterFactory.IMPORTER_TYPE importType,
	                                            List<Demographic> demographicList,
	                                            ExportPreferences preferences) throws IOException
	{
		DemographicExporter exporter = ImporterExporterFactory.getExporter(importType);
		List<GenericFile> fileList = new ArrayList<>(demographicList.size() + 2);

		for (Demographic demographic : demographicList)
		{
			GenericFile file = exporter.exportDemographic(demographic, preferences);
			fileList.add(file);
		}
		fileList.addAll(exporter.getAdditionalFiles(preferences));

		return fileList;
	}

	public List<GenericFile> exportDemographicsWithLookup(ImporterExporterFactory.IMPORTER_TYPE importType,
	                                                      List<String> demographicIdList,
	                                                      ExportPreferences preferences) throws IOException
	{
		//TODO batch query get demographics
		List<Demographic> demographicList = new ArrayList<>(demographicIdList.size());
		for(String demographicIdStr : demographicIdList)
		{
			Integer demographicId = Integer.parseInt(demographicIdStr);
			org.oscarehr.demographic.model.Demographic demographic = demographicDao.find(demographicId);
			Demographic exportDemographic = modelToExportConverter.convert(demographic);
			demographicList.add(exportDemographic);
		}

		return exportDemographics(importType, demographicList, preferences);
	}
}
