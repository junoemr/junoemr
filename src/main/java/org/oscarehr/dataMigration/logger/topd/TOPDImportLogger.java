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

import org.apache.log4j.Logger;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.logger.ImportLogger;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TOPDImportLogger extends TOPDBaseLogger implements ImportLogger
{
	private static final Logger log4jLogger = MiscUtils.getLogger();

	public TOPDImportLogger() throws IOException, InterruptedException
	{
		super();
	}
	protected GenericFile initSummaryLogFile() throws IOException, InterruptedException
	{
		String header = "TOPD Import Summary " + ConversionUtils.toDateTimeString(LocalDateTime.now()) + "\n";
		return initLogFile(header, "-ImportSummary.log");
	}
	protected GenericFile initEventLogFile() throws IOException, InterruptedException
	{
		String header = "TOPD Import Events " + ConversionUtils.toDateTimeString(LocalDateTime.now()) + "\n";
		return initLogFile(header, "-ImportEvent.log");
	}

	@Override
	public void flush()
	{
	}

	@Override
	public List<String> getMessages()
	{
		List<String> messages;
		try
		{
			messages = Files.readAllLines(Paths.get(getEventLogFile().getPath()), StandardCharsets.UTF_8);
		}
		catch(IOException e)
		{
			String errorMessage = "Error reading import event log";
			log4jLogger.error(errorMessage, e);
			messages = new ArrayList<>(1);
			messages.add(errorMessage);
		}
		return messages;
	}

	protected GenericFile initLogFile(String header, String fileName) throws IOException, InterruptedException
	{
		InputStream stream = new ByteArrayInputStream(header.getBytes(StandardCharsets.UTF_8));
		return FileFactory.createImportLogFile(stream, fileName, Thread.currentThread().getName());
	}
}
