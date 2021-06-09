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
package org.oscarehr.dataMigration.service.cds;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.mapper.cds.out.CDSExportMapper;
import org.oscarehr.dataMigration.model.PatientRecord;
import org.oscarehr.dataMigration.model.demographic.Demographic;
import org.oscarehr.dataMigration.parser.cds.CDSFileParser;
import org.oscarehr.dataMigration.pref.ExportPreferences;
import org.oscarehr.dataMigration.service.DemographicExporter;
import org.oscarehr.dataMigration.service.context.PatientExportContext;
import org.oscarehr.dataMigration.service.context.PatientExportContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.OscarProperties;
import oscar.log.LogAction;
import oscar.oscarClinic.ClinicData;
import oscar.util.ConversionUtils;
import xml.cds.v5_0.OmdCds;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static oscar.util.ConversionUtils.DATE_PATTERN_DAY;
import static oscar.util.ConversionUtils.DATE_PATTERN_MONTH;
import static oscar.util.ConversionUtils.DATE_PATTERN_YEAR;

@Service
public class CDSExporter implements DemographicExporter
{
	private static final OscarProperties oscarProperties = OscarProperties.getInstance();

	@Autowired
	private CDSExportMapper cdsExportMapper;

	@Autowired
	private PatientExportContextService patientExportContextService;

	public CDSExporter()
	{
	}

	public GenericFile exportDemographic(PatientRecord patientRecord) throws Exception
	{
		Instant instant = Instant.now();
		Demographic demographic = patientRecord.getDemographic();
		CDSFileParser parser = new CDSFileParser();

		PatientExportContext context = patientExportContextService.getContext();
		context.getExportLogger().logSummaryLine(patientRecord);
		context.incrementProviderExportCount(demographic.getMrpProvider());
		OmdCds omdCds = cdsExportMapper.exportFromJuno(patientRecord);
		instant = LogAction.printDuration(instant, "Exporter: model to CDS structure conversion");

		GenericFile exportFile = parser.write(omdCds, context.getTempDirectory());
		exportFile.rename(createExportFilename(demographic));

		instant = LogAction.printDuration(instant, "Exporter: file write and rename");
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
		return CDSFileParser.SCHEMA_VERSION;
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

	protected GenericFile createEventLog() throws IOException
	{
		PatientExportContext context = patientExportContextService.getContext();
		GenericFile eventLog = FileFactory.createTempFile(context.getTempDirectory(), ".txt");
		eventLog.rename("ExportEventLog.txt");

		eventLog.appendContents(
				context.getExportLogger().getSummaryLogFile(),
				context.getExportLogger().getEventLogFile());

		return eventLog;
	}

	protected GenericFile createReadme() throws IOException
	{
		PatientExportContext context = patientExportContextService.getContext();
		GenericFile readme = FileFactory.createTempFile(context.getTempDirectory(), ".txt");
		FileOutputStream fos = new FileOutputStream(readme.getFileObject());
		OutputStreamWriter streamWriter = new OutputStreamWriter(fos);

		streamWriter.write(paddedReadmeLine("Physician Group", new ClinicData().getClinicName()));
		streamWriter.write(paddedReadmeLine("CDS Vendor, Product & Version",
				StringUtils.trimToEmpty(oscarProperties.getProperty("Vendor_Product")) + " " + OscarProperties.getBuildTag()));
		streamWriter.write(paddedReadmeLine("Application Support Contact", oscarProperties.getProperty("Support_Contact")));
		streamWriter.write(paddedReadmeLine("Date and Time", ConversionUtils.toDateTimeString(ZonedDateTime.now())));

		// CDS requires the readme to display a count of how many demographics are exported for each provider.
		for(Map.Entry<String, Integer> entry : context.getProviderExportCountHash().entrySet())
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
