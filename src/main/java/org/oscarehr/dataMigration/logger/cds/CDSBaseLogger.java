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
package org.oscarehr.dataMigration.logger.cds;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.logger.BaseLogger;
import org.oscarehr.dataMigration.model.PatientRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public abstract class CDSBaseLogger implements BaseLogger
{
	private static final Logger applicationLogger = Logger.getLogger(CDSBaseLogger.class);
	protected static final int SUMMARY_LOG_COLUMN_WIDTH = 14;
	protected static final int SUMMARY_LOG_ITEM_COUNT = 15;

	protected final GenericFile summaryLogFile;
	protected final GenericFile eventLogFile;

	public CDSBaseLogger() throws IOException, InterruptedException
	{
		summaryLogFile = initSummaryLogFile();
		eventLogFile = initEventLogFile();
	}

	protected abstract GenericFile initSummaryLogFile() throws IOException, InterruptedException;
	protected abstract GenericFile initEventLogFile() throws IOException, InterruptedException;

	public synchronized void log(GenericFile logFile, String message)
	{
		String messageLine = message + "\n";
		try
		{
			Files.write(Paths.get(logFile.getFileObject().getPath()), messageLine.getBytes(), StandardOpenOption.APPEND);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public GenericFile getSummaryLogFile()
	{
		return this.summaryLogFile;
	}

	@Override
	public GenericFile getEventLogFile()
	{
		return this.eventLogFile;
	}

	@Override
	public synchronized void logEvent(String message)
	{
		applicationLogger.warn("[Event-Log]: " + message);
		log(eventLogFile, message);
	}

	@Override
	public synchronized void logSummaryHeader()
	{
		String summaryLine = buildSummaryLine("Patient ID", "Family", "Past Health", "Problem List",
				"Risk Factor", "Allergy &", "Medication", "Immunization",
				"Labs", "Appointments", "Clinical", "Reports", "Reports",
				"Care Elements", "Alerts and");
		String summaryLine2 = buildSummaryLine("", "History", "", "",
				"", "Adv. Reaction", "", "",
				"", "", "Notes", "Text", "Binary",
				"", "Special Needs");

		this.logSummaryLine(summaryLine);
		this.logSummaryLine(summaryLine2);

		this.logSummaryLine(StringUtils.rightPad("-", (SUMMARY_LOG_COLUMN_WIDTH * SUMMARY_LOG_ITEM_COUNT) + SUMMARY_LOG_ITEM_COUNT, "-"));
	}

	@Override
	public void logSummaryFooter()
	{
		this.logSummaryLine(StringUtils.rightPad("-", (SUMMARY_LOG_COLUMN_WIDTH * SUMMARY_LOG_ITEM_COUNT) + SUMMARY_LOG_ITEM_COUNT, "-") + "\n");
	}

	@Override
	public void logSummaryLine(String message)
	{
		log(summaryLogFile, message);
	}

	@Override
	public void logSummaryLine(PatientRecord patientRecord)
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
		this.logSummaryLine(summaryLine);
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
				paddedSummaryItem(alerts);
	}

	private String paddedSummaryItem(String name)
	{
		return StringUtils.rightPad(name, SUMMARY_LOG_COLUMN_WIDTH) + "|";
	}
}
