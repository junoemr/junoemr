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

import org.oscarehr.demographicImport.logger.ExportLogger;
import org.oscarehr.demographicImport.logger.ImportLogger;
import org.oscarehr.demographicImport.logger.cds.CDSExportLogger;
import org.oscarehr.demographicImport.service.cds.CDSExporter;
import org.oscarehr.demographicImport.logger.cds.CDSImportLogger;
import org.oscarehr.demographicImport.service.cds.CDSImporter;
import org.oscarehr.demographicImport.util.ExportPreferences;
import org.oscarehr.demographicImport.util.ExportProperties;
import org.oscarehr.demographicImport.util.ImportProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ImporterExporterFactory
{
	@Autowired
	private CDSImporter cdsImporter;

	@Autowired
	private CDSExporter cdsExporter;

	@Autowired
	private ImportProperties importProperties;

	@Autowired
	private ExportProperties exportProperties;

	public enum IMPORTER_TYPE
	{
		CDS_5,
		ToPD,
	}

	public enum EXPORTER_TYPE
	{
		CDS_5,
	}

	public enum IMPORT_SOURCE
	{
		WOLF,
		MEDIPLAN,
		MEDACCESS,
		ACCURO,
		UNKNOWN
	}

	public ImportLogger getImportLogger(IMPORTER_TYPE type)
	{
		switch(type)
		{
			case CDS_5: return new CDSImportLogger();
			case ToPD: // TODO
			default: throw new RuntimeException(type + " logger not implemented");
		}
	}

	public ExportLogger getExportLogger(EXPORTER_TYPE type) throws IOException
	{
		switch(type)
		{
			case CDS_5: return new CDSExportLogger();
			default: throw new RuntimeException(type + " logger not implemented");
		}
	}

	public DemographicImporter getImporter(IMPORTER_TYPE type,
	                                       IMPORT_SOURCE importSource,
	                                       ImportLogger importLogger,
	                                       String documentLocation,
	                                       boolean skipMissingDocs)
	{
		importProperties.setImportSource(importSource);
		importProperties.setImportLogger(importLogger);
		importProperties.setExternalDocumentPath(documentLocation);
		importProperties.setSkipMissingDocs(skipMissingDocs);

		switch(type)
		{
			case CDS_5: return cdsImporter;
			case ToPD: // TODO
			default: throw new RuntimeException(type + " importer not implemented");
		}
	}

	public DemographicExporter getExporter(EXPORTER_TYPE type, ExportLogger exportLogger, ExportPreferences exportPreferences)
	{
		exportProperties.setExportLogger(exportLogger);
		exportProperties.setExportPreferences(exportPreferences);

		switch(type)
		{
			case CDS_5: return cdsExporter;
			default: throw new RuntimeException(type + " exporter not implemented");
		}
	}
}
