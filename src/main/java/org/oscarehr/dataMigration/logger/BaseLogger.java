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
package org.oscarehr.dataMigration.logger;

import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.model.PatientRecord;

public interface BaseLogger
{
	/**
	 * 	adds a log statement to the event log
	 * @param message - the statement to log
	 */
	void logEvent(String message);

	/**
	 * log a summary header
	 * this is the first thing in the summary file
	 */
	void logSummaryHeader();

	/**
	 * create a summary line from the given string
	 * @param message - the statement to log
	 */
	void logSummaryLine(String message);
	/**
	 * create a summary line from the given patient record
	 * @param patientRecord - the record to summarize
	 */
	void logSummaryLine(PatientRecord patientRecord);

	/**
	 * log a summary footer
	 * this is the last thing in the summary file
	 */
	void logSummaryFooter();

	/**
	 * @return - the summary log file
	 */
	GenericFile getSummaryLogFile();

	/**
	 * @return - the event log file
	 */
	GenericFile getEventLogFile();
}
