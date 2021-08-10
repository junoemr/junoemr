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
package org.oscarehr.dataMigration.logger.topd;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.logger.BaseLogger;
import org.oscarehr.dataMigration.model.PatientRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public abstract class TOPDBaseLogger implements BaseLogger
{
	private static final Logger applicationLogger = Logger.getLogger(TOPDBaseLogger.class);

	protected final GenericFile summaryLogFile;
	protected final GenericFile eventLogFile;

	public TOPDBaseLogger() throws IOException, InterruptedException
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
		applicationLogger.info("[Event-Log]: " + message);
		log(eventLogFile, message);
	}

	@Override
	public synchronized void logSummaryHeader()
	{
		this.logSummaryLine("ToPD Import Summary");
		this.logSummaryLine("-------------------");
	}

	@Override
	public void logSummaryFooter()
	{
		this.logSummaryLine("------------------");
		this.logSummaryLine("END OF ToPD IMPORT");
	}

	@Override
	public void logSummaryLine(String message)
	{
		log(summaryLogFile, message);
	}

	@Override
	public void logSummaryLine(PatientRecord patientRecord) throws NotImplementedException
	{
		// unused for current version of topd importer but required for BaseLogger interface
		throw new NotImplementedException();
	}
	
}
