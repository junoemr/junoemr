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

import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.demographicImport.mapper.cds.out.CDSExportMapper;
import org.oscarehr.demographicImport.model.PatientRecord;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.model.provider.Provider;
import org.oscarehr.demographicImport.parser.cds.CDSFileParser;
import org.oscarehr.demographicImport.service.DemographicExporter;
import org.oscarehr.demographicImport.service.ImportExportService;
import org.oscarehr.demographicImport.util.ExportPreferences;
import org.oscarehr.demographicImport.util.ExportProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.OscarProperties;
import oscar.oscarClinic.ClinicData;
import oscar.util.ConversionUtils;
import xml.cds.v5_0.OmdCds;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static oscar.util.ConversionUtils.DATE_PATTERN_DAY;
import static oscar.util.ConversionUtils.DATE_PATTERN_MONTH;
import static oscar.util.ConversionUtils.DATE_PATTERN_YEAR;

@Service
public class CDSExporter implements DemographicExporter
{
	private static final OscarProperties oscarProperties = OscarProperties.getInstance();

	private final HashMap<String, Integer> providerExportCountHash;

	@Autowired
	private CDSExportMapper cdsExportMapper;

	@Autowired
	private ExportProperties exportProperties;

	public CDSExporter()
	{
		providerExportCountHash = new HashMap<>();
	}

	public GenericFile exportDemographic(PatientRecord patientRecord) throws Exception
	{
		Instant instant = Instant.now();
		Demographic demographic = patientRecord.getDemographic();
		CDSFileParser parser = new CDSFileParser();

		exportProperties.getExportLogger().logSummaryLine(patientRecord);
		incrementProviderExportCount(demographic);
		OmdCds omdCds = cdsExportMapper.exportFromJuno(patientRecord);
		instant = ImportExportService.printDuration(instant, "Exporter: model to CDS structure conversion");

		GenericFile exportFile = parser.write(omdCds);
		exportFile.rename(createExportFilename(demographic));

		instant = ImportExportService.printDuration(instant, "Exporter: file write and rename");
		return exportFile;
	}

	@Override
	public List<GenericFile> getAdditionalFiles(ExportPreferences preferences) throws IOException
	{
		List<GenericFile> additionalExportFiles = new ArrayList<>(2);

		additionalExportFiles.add(createEventLog());
		additionalExportFiles.add(createReadme());

		return additionalExportFiles;
	}

	@Override
	public String getSchemaVersion()
	{
		return "5.1";
	}

	/**
	 * creates the filename string according to CDS requirement PatientFN_PatientLN_PatientUniqueID_DOB
	 */
	protected String createExportFilename(Demographic demographic)
	{
		String filename =
				demographic.getFirstName().replace("_", "-") + "_" +
				demographic.getLastName().replace("_", "-") + "_" +
				demographic.getId() + "_" +
				ConversionUtils.toDateString(demographic.getDateOfBirth(), DATE_PATTERN_DAY + DATE_PATTERN_MONTH + DATE_PATTERN_YEAR);
		return filename.replaceAll("[\\s,.]", "-") + ".xml";
	}

	protected void incrementProviderExportCount(Demographic demographic)
	{
		Provider provider = demographic.getMrpProvider();
		String providerKey = "Provider Unassigned";
		if(provider != null)
		{
			providerKey = StringUtils.trimToEmpty(
					StringUtils.trimToEmpty(provider.getTitleString()) + " " + provider.getFirstName() + " " + provider.getLastName());
		}
		if(providerExportCountHash.containsKey(providerKey))
		{
			providerExportCountHash.put(providerKey, providerExportCountHash.get(providerKey) + 1);
		}
		else
		{
			providerExportCountHash.put(providerKey, 1);
		}
	}

	protected GenericFile createEventLog() throws IOException
	{
		GenericFile eventLog = FileFactory.createTempFile(".txt");
		eventLog.rename("ExportEventLog.txt");

		eventLog.appendContents(
				exportProperties.getExportLogger().getSummaryLogFile(),
				exportProperties.getExportLogger().getEventLogFile());

		return eventLog;
	}

	protected GenericFile createReadme() throws IOException
	{
		GenericFile readme = FileFactory.createTempFile(".txt");
		FileOutputStream fos = new FileOutputStream(readme.getFileObject());
		OutputStreamWriter streamWriter = new OutputStreamWriter(fos);

		streamWriter.write(paddedReadmeLine("Physician Group", new ClinicData().getClinicName()));
		streamWriter.write(paddedReadmeLine("CDS Vendor, Product & Version",
				StringUtils.trimToEmpty(oscarProperties.getProperty("Vendor_Product")) + " " + OscarProperties.getBuildTag()));
		streamWriter.write(paddedReadmeLine("Application Support Contact", oscarProperties.getProperty("Support_Contact")));
		streamWriter.write(paddedReadmeLine("Date and Time", ConversionUtils.toDateTimeString(ZonedDateTime.now())));

		// CDS requires the readme to display a count of how many demographics are exported for each provider.
		for(Map.Entry<String, Integer> entry : providerExportCountHash.entrySet())
		{
			String providerName = entry.getKey();
			Object exportCounter = entry.getValue();
			streamWriter.write(paddedReadmeLine(providerName + " (total patients exported)", String.valueOf(exportCounter)));
		}
		streamWriter.close();

		readme.rename("ReadMe.txt");
		return readme;
	}

	private String paddedReadmeLine(String name, String value)
	{
		return StringUtils.rightPad(name + ":", 64) + " " + StringUtils.trimToEmpty(value) + "\n";
	}

}
