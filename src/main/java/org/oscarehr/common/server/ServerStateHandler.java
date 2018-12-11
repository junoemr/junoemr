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
package org.oscarehr.common.server;

import oscar.OscarProperties;

import java.io.File;

/**
 * Responsible for handling server state information.
 * This is where other classes should be able to determine if the server is a master/slave server, in readonly mode, etc.
 */
public class ServerStateHandler
{
	private static final OscarProperties props = OscarProperties.getInstance();
	private static final String masterCheckLocation = props.getProperty("common.server.master_check_file.location");
	private static final String masterCheckFilename = props.getProperty("common.server.master_check_file.filename");

	/**
	 * checks if the server is running in a 'master' state
	 * @return true if the server is a master server, false if it is not a master
	 * @throws IllegalStateException if the master/slave status cannot be determined
	 */
	public static boolean isThisServerMaster() throws IllegalStateException
	{
		boolean isMaster;
		try
		{
			File file = new File(masterCheckLocation, masterCheckFilename);
			isMaster = (file.exists() && file.isFile());
		}
		catch(Exception e)
		{
			throw new IllegalStateException("Invalid master/slave state");
		}
		return isMaster;
	}
}
