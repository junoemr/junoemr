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

import org.oscarehr.demographicImport.service.cds.CDSExporter;
import org.oscarehr.demographicImport.service.cds.CDSImportLogger;
import org.oscarehr.demographicImport.service.cds.CDSImporter;

import java.io.IOException;

public class ImporterExporterFactory
{
	public enum IMPORTER_TYPE
	{
		CDS_5,
		ToPD,
	}

	public enum IMPORT_SOURCE
	{
		WOLF,
		MEDIPLAN,
		MEDACCESS,
		ACCURO,
		UNKNOWN
	}

	public static ImportLogger getImportLogger(IMPORTER_TYPE type)
	{
		switch(type)
		{
			case CDS_5: return new CDSImportLogger();
			case ToPD: // TODO
			default: throw new RuntimeException(type + " logger not implemented");
		}
	}

	public static DemographicImporter getImporter(IMPORTER_TYPE type,
	                                              IMPORT_SOURCE importSource,
	                                              ImportLogger importLogger,
	                                              String documentLocation,
	                                              boolean skipMissingDocs)
	{
		switch(type)
		{
			case CDS_5: return new CDSImporter();
			case ToPD: // TODO
			default: throw new RuntimeException(type + " importer not implemented");
		}
	}

	public static DemographicExporter getExporter(IMPORTER_TYPE type) throws IOException
	{
		switch(type)
		{
			case CDS_5: return new CDSExporter();
			case ToPD: // TODO
			default: throw new RuntimeException(type + " exporter not implemented");
		}
	}
}
