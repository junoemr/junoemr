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
import org.oscarehr.dataMigration.logger.ImportLogger;
import org.oscarehr.dataMigration.pref.ImportPreferences;
import org.oscarehr.dataMigration.service.DemographicImporter;
import org.oscarehr.dataMigration.service.ImporterExporterFactory;
import org.oscarehr.dataMigration.transfer.ImportTransferOutbound;

import java.time.LocalDate;

@Data
public class PatientImportContext extends PollableContext
{
	private DemographicImporter importer;
	private ImportLogger importLogger;
	private ImportPreferences importPreferences;
	private ImporterExporterFactory.IMPORTER_TYPE importType;

	private ImportTransferOutbound result;

	/**
	 * The date to use when you need to assign a date but there is none provided
	 * @return - the partial date object
	 */
	public LocalDate getDefaultDate()
	{
		return LocalDate.of(1900, 1, 1);
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
			return "Importing Patient " + (getProcessed() + 1) + " of " + getTotal();
		}
		else
		{
			return "Finalizing Import";
		}
	}
}
