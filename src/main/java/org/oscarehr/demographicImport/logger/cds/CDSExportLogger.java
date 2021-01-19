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
package org.oscarehr.demographicImport.logger.cds;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.demographicImport.logger.ExportLogger;
import org.oscarehr.demographicImport.model.PatientRecord;
import org.oscarehr.util.MiscUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CDSExportLogger implements ExportLogger
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final int LOG_COLUMN_WIDTH = 14;

	private final GenericFile logFile;

	public CDSExportLogger() throws IOException
	{
		logFile = FileFactory.createTempFile(".log");
		logFile.rename("ExportEvent.log");
	}

	@Override
	public void log(String message) throws IOException
	{
		logger.info(message);
		Files.write(Paths.get(logFile.getFileObject().getPath()), message.getBytes(), StandardOpenOption.APPEND);
	}

	@Override
	public GenericFile getLogFile()
	{
		return this.logFile;
	}


	public void logSummaryHeaderLine() throws IOException
	{
		String summaryLine = buildSummaryLine("Patient ID", "Family", "Past Health", "Problem List",
				"Risk Factor", "Allergy &", "Medication", "Immunization",
				"Labs", "Appointments", "Clinical", "Reports", "Reports",
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

	@Override
	public void logSummaryLine(PatientRecord patientRecord) throws IOException
	{
		String summaryLine = buildSummaryLine(
				String.valueOf(patientRecord.getDemographic().getId()),
				String.valueOf(patientRecord.getFamilyHistoryNoteList().size()),
				String.valueOf(patientRecord.getMedicalHistoryNoteList().size()),
				String.valueOf(patientRecord.getConcernNoteList().size()),
				String.valueOf(patientRecord.getRiskFactorNoteList().size()),
				String.valueOf(patientRecord.getAllergyList().size()),
				String.valueOf(patientRecord.getMedicationList().size()),
				String.valueOf(patientRecord.getImmunizationList().size()),
				String.valueOf(patientRecord.getLabList().size()),
				String.valueOf(patientRecord.getAppointmentList().size()),
				String.valueOf(patientRecord.getEncounterNoteList().size()),
				String.valueOf(patientRecord.getDocumentList().size()),
				String.valueOf(patientRecord.getDocumentList().size()),
				String.valueOf(patientRecord.getMeasurementList().size()),
				String.valueOf(patientRecord.getReminderNoteList().size()));
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
}
