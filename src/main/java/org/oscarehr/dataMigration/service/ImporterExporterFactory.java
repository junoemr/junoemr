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
package org.oscarehr.dataMigration.service;

import org.oscarehr.common.io.FileFactory;
import org.oscarehr.dataMigration.logger.ExportLogger;
import org.oscarehr.dataMigration.logger.ImportLogger;
import org.oscarehr.dataMigration.logger.cds.CDSExportLogger;
import org.oscarehr.dataMigration.logger.cds.CDSImportLogger;
import org.oscarehr.dataMigration.logger.topd.TOPDImportLogger;
import org.oscarehr.dataMigration.pref.ExportPreferences;
import org.oscarehr.dataMigration.pref.ImportPreferences;
import org.oscarehr.dataMigration.service.cds.CDSExporter;
import org.oscarehr.dataMigration.service.cds.CDSImporter;
import org.oscarehr.dataMigration.service.context.PatientExportContext;
import org.oscarehr.dataMigration.service.context.PatientImportContext;
import org.oscarehr.dataMigration.service.hrm.HRMExporter;
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
	private HRMExporter hrmExporter;
	
	@Autowired
	private CoPDImportService coPDImportService;

	public enum IMPORTER_TYPE
	{
		CDS_5,
		ToPD,
	}

	public enum EXPORTER_TYPE
	{
		CDS_5,
		HRM_4,
	}

	public enum IMPORT_SOURCE
	{
		JUNO,
		WOLF,
		MEDIPLAN,
		MEDACCESS,
		ACCURO,
		HEALTHQUEST,
		UNKNOWN
	}

	public PatientImportContext initializeImportContext(IMPORTER_TYPE type, ImportPreferences importPreferences, int size) throws IOException, InterruptedException
	{
		PatientImportContext patientImportContext = new PatientImportContext();
		patientImportContext.initialize(size);
		patientImportContext.setImportType(type);
		patientImportContext.setImportLogger(getImportLogger(type));
		patientImportContext.setImportPreferences(importPreferences);
		patientImportContext.setImporter(getImporter(type));
		return patientImportContext;
	}

	public DemographicImporter getImporter(IMPORTER_TYPE type)
	{
		switch(type)
		{
			case CDS_5: 
			case ToPD: return cdsImporter; // TODO implement own importer
			default: throw new RuntimeException(type + " importer not implemented");
		}
	}

	public ImportLogger getImportLogger(IMPORTER_TYPE type) throws IOException, InterruptedException
	{
		switch(type)
		{
			case CDS_5: return new CDSImportLogger();
			case ToPD: return new TOPDImportLogger();
			default: throw new RuntimeException(type + " logger not implemented");
		}
	}

	public PatientExportContext initializeExportContext(EXPORTER_TYPE type, ExportPreferences exportPreferences, int size) throws IOException, InterruptedException
	{
		PatientExportContext patientExportContext = new PatientExportContext();
		patientExportContext.initialize(size);
		patientExportContext.setExportPreferences(exportPreferences);
		patientExportContext.setExportLogger(getExportLogger(type));
		patientExportContext.setExporter(getExporter(type));
		patientExportContext.setTempDirectory(FileFactory.createTempDirectory());
		return patientExportContext;
	}

	public DemographicExporter getExporter(EXPORTER_TYPE type)
	{
		switch(type)
		{
			case CDS_5: return cdsExporter;
			case HRM_4: return hrmExporter;
			default: throw new RuntimeException(type + " exporter not implemented");
		}
	}

	public ExportLogger getExportLogger(EXPORTER_TYPE type) throws IOException, InterruptedException
	{
		switch(type)
		{
			case CDS_5: return new CDSExportLogger();
			default: throw new RuntimeException(type + " logger not implemented");
		}
	}
}
