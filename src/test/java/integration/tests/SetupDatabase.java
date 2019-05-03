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
package integration.tests;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.util.MiscUtils;

import java.io.IOException;
import java.sql.SQLException;

// "hack" test to initialize the database to a workable state before embedded tomcat is run
public class SetupDatabase
{
	private static Logger logger= MiscUtils.getLogger();

	//initialize database and make all tables available for the subsequent embedded Tomcat invocation.
	@Test
	public void setupDatabase() throws SQLException, InstantiationException, ClassNotFoundException, IllegalAccessException, IOException
	{
		long start = System.currentTimeMillis();
		if (!SchemaUtils.inited)
		{
			logger.info("dropAndRecreateDatabase");
			SchemaUtils.dropAndRecreateDatabase();
		}

		long end = System.currentTimeMillis();
		long secsTaken = (end - start) / 1000;
		if (secsTaken > 30)
		{
			logger.info("Setting up db took " + secsTaken + " seconds.");
		}
	}
}
