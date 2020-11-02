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
import org.oscarehr.common.xml.cds.v5_0.model.OmdCds;
import org.oscarehr.demographicImport.service.ExportPreferences;
import org.oscarehr.demographicImport.mapper.cds.out.CDSExportMapper;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.parser.cds.CDSFileParser;
import org.oscarehr.demographicImport.service.DemographicExporter;

import java.io.IOException;

public class CDSExporter implements DemographicExporter
{
	public GenericFile exportDemographic(Demographic demographic, ExportPreferences preferences) throws IOException
	{
		CDSFileParser parser = new CDSFileParser();
		CDSExportMapper mapper = new CDSExportMapper(preferences);
		OmdCds omdCds = mapper.exportFromJuno(demographic);

		return parser.write(omdCds);
	}
}
