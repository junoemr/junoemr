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

import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.logger.ExportLogger;
import oscar.util.ConversionUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class CDSExportLogger extends CDSBaseLogger implements ExportLogger
{
	public CDSExportLogger() throws IOException, InterruptedException
	{
		super();
	}
	protected GenericFile initSummaryLogFile() throws IOException, InterruptedException
	{
		String header = "CDS Export Summary " + ConversionUtils.toDateTimeString(LocalDateTime.now()) + "\n";
		return initLogFile(header, "-ExportSummary.log");
	}
	protected GenericFile initEventLogFile() throws IOException, InterruptedException
	{
		String header = "CDS Export Events " + ConversionUtils.toDateTimeString(LocalDateTime.now()) + "\n";
		return initLogFile(header, "-ExportEvent.log");
	}

	protected GenericFile initLogFile(String header, String fileName) throws IOException, InterruptedException
	{
		InputStream stream = new ByteArrayInputStream(header.getBytes(StandardCharsets.UTF_8));
		return FileFactory.createExportLogFile(stream, fileName, Thread.currentThread().getName());
	}
}
