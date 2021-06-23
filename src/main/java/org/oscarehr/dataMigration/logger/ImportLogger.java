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

import java.util.List;

public interface ImportLogger extends BaseLogger
{
	/**
	 * forces all unwritten log data to be written to the log file.
	 * This may be useful for loggers that need to collect amalgamated data before writing it to the log.
	 */
	void flush();

	/**
	 * Get the log as a list. this may be the same as the log file contents, but it can be delivered to users etc.
	 * @return - a list of messages.
	 */
	List<String> getMessages();
}
