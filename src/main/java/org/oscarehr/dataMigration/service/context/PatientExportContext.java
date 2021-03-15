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
package org.oscarehr.dataMigration.service.context;

import lombok.Data;
import org.oscarehr.common.io.ZIPFile;
import org.oscarehr.dataMigration.logger.ExportLogger;
import org.oscarehr.dataMigration.pref.ExportPreferences;
import org.oscarehr.dataMigration.service.DemographicExporter;
import org.oscarehr.util.MiscUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class PatientExportContext extends PollableContext
{
	private DemographicExporter exporter;
	private ExportLogger exportLogger;
	private ExportPreferences exportPreferences;

	private ZIPFile result;
	private String exportName;
	private Path tempDirectory;

	@Override
	public synchronized void clean()
	{
		if(tempDirectory != null)
		{
			try
			{
				MiscUtils.getLogger().info("remove temp directory: " + tempDirectory.toString());
				Files.deleteIfExists(tempDirectory);
			}
			catch(IOException e)
			{
				MiscUtils.getLogger().error("Error cleaning export temp files", e);
			}
		}
	}

	@Override
	protected synchronized String getPollingMessage()
	{
		if(getTotal() <= 0)
		{
			return "Initializing...";
		}
		else if(getTotal() > getProcessed())
		{
			return "Exporting Patient " + (getProcessed() + 1) + " of " + getTotal();
		}
		else
		{
			return "Packaging Export Files";
		}
	}
}
