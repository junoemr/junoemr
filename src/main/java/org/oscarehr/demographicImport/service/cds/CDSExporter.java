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
import org.oscarehr.common.xml.cds.v5_0.model.OmdCds;
import org.oscarehr.demographicImport.mapper.cds.out.CDSExportMapper;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.parser.cds.CDSFileParser;
import org.oscarehr.demographicImport.service.DemographicExporter;
import org.oscarehr.demographicImport.service.ExportLogger;
import org.oscarehr.demographicImport.service.ExportPreferences;
import oscar.OscarProperties;
import oscar.oscarClinic.ClinicData;
import oscar.util.ConversionUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class CDSExporter implements DemographicExporter, ExportLogger
{
	private static final OscarProperties oscarProperties = OscarProperties.getInstance();
	private static final int LOG_COLUMN_WIDTH = 14;

	private final GenericFile logFile;

	public CDSExporter() throws IOException
	{
		logFile = FileFactory.createTempFile(".log");
		logFile.rename("ExportEvent.log");
		logSummaryHeaderLine();
	}

	public GenericFile exportDemographic(Demographic demographic, ExportPreferences preferences) throws IOException
	{
		CDSFileParser parser = new CDSFileParser();
		CDSExportMapper mapper = new CDSExportMapper(preferences);

		logSummaryLine(demographic);
		OmdCds omdCds = mapper.exportFromJuno(demographic);

		return parser.write(omdCds);
	}

	@Override
	public List<GenericFile> getAdditionalFiles(ExportPreferences preferences) throws IOException
	{
		List<GenericFile> additionalExportFiles = new ArrayList<>(2);
		additionalExportFiles.add(logFile);
		additionalExportFiles.add(createReadme());

		return additionalExportFiles;
	}

	@Override
	public void log(String message) throws IOException
	{
		Files.write(Paths.get(logFile.getFileObject().getPath()), message.getBytes(), StandardOpenOption.APPEND);
	}

	@Override
	public GenericFile getLogFile()
	{
		return this.logFile;
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
		streamWriter.close();

		readme.rename("Readme.txt");
		return readme;
	}

	protected void logSummaryHeaderLine() throws IOException
	{
		String summaryLine = buildSummaryLine("Patient ID", "Family", "Past Health", "Problem List",
				"Risk Factor", "Allergy &", "Medication", "Immunization",
				"Labs", "App", "Clinical", "Reports", "Reports",
				"Care Elements", "Alerts and");
		String summaryLine2 = buildSummaryLine("", "History", "", "",
				"", "Adv. Reaction", "", "",
				"", "", "Notes", "Text", "Binary",
				"", "Special Needs");

		this.log(summaryLine);
		this.log(summaryLine2);

		int summaryItemCount = 15; // number of columns in the summary line above
		this.log(StringUtils.rightPad("-", (LOG_COLUMN_WIDTH * summaryItemCount) + summaryItemCount, "-") + "\n");
	}

	protected void logSummaryLine(Demographic demographic) throws IOException
	{
		String summaryLine = buildSummaryLine(
				String.valueOf(demographic.getId()),
				String.valueOf(demographic.getFamilyHistoryNoteList().size()),
				String.valueOf(demographic.getMedicalHistoryNoteList().size()),
				String.valueOf(demographic.getProblemList().size()),
				String.valueOf(demographic.getRiskFactorList().size()),
				String.valueOf(demographic.getAllergyList().size()),
				String.valueOf(demographic.getMedicationList().size()),
				String.valueOf(demographic.getImmunizationList().size()),
				String.valueOf(demographic.getLabList().size()),
				String.valueOf(demographic.getAppointmentList().size()),
				String.valueOf(demographic.getEncounterNoteList().size()),
				String.valueOf(demographic.getReportList().size()),
				String.valueOf(demographic.getReportList().size()),
				String.valueOf(demographic.getMeasurementList().size()),
				String.valueOf(demographic.getAlertList().size()));
		this.log(summaryLine);
	}

	private String buildSummaryLine(String patientId,
	                                String familyHistCount,
	                                String pastHealth,
	                                String problems,
	                                String riskFactor,
	                                String allergy,
	                                String medications,
	                                String immunizations,
	                                String labs,
	                                String appointments,
	                                String clinicalNotes,
	                                String reportsText,
	                                String reportsBinary,
	                                String careElements,
	                                String alerts)
	{
		return paddedSummaryItem(patientId) +
				paddedSummaryItem(familyHistCount) +
				paddedSummaryItem(pastHealth) +
				paddedSummaryItem(problems) +
				paddedSummaryItem(riskFactor) +
				paddedSummaryItem(allergy) +
				paddedSummaryItem(medications) +
				paddedSummaryItem(immunizations) +
				paddedSummaryItem(labs) +
				paddedSummaryItem(appointments) +
				paddedSummaryItem(clinicalNotes) +
				paddedSummaryItem(reportsText) +
				paddedSummaryItem(reportsBinary) +
				paddedSummaryItem(careElements) +
				paddedSummaryItem(alerts) + "\n";
	}

	private String paddedSummaryItem(String name)
	{
		return StringUtils.rightPad(name, LOG_COLUMN_WIDTH) + "|";
	}

	private String paddedReadmeLine(String name, String value)
	{
		return StringUtils.rightPad(name + ":", 34) + " " + StringUtils.trimToEmpty(value) + "\n";
	}

}
