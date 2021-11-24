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

package org.oscarehr.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.oscarehr.common.dao.utils.SchemaUtils;
import java.sql.SQLException;
import java.util.Set;

public class DatabaseTestBase
{
	/**
	 * Override this method and return the tables that need to be reset before and after running an
	 * integration test.  Here is a template for the override:
	 *
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
		};
	}
	 */
	protected String[] getTablesToRestore()
	{
		return new String[0];
	}

	/**
	 * Override this method and return the tables that need to be cleared before and after running
	 * an integration test.  Here is a template for the override:
	 *
	 @Override
	 protected String[] getTablesToClear()
	 {
		 return new String[]{
		 };
	 }
	 */
	protected String[] getTablesToClear()
	{
		return new String[0];
	}

	@Before
	public void resetDatabase()
		throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
	{
		if(getTablesToRestore().length > 0)
		{
			SchemaUtils.restoreTable(getTablesToRestore());

			// XXX: Restore all tables.  Switch back to regular once it runs clean.
			//SchemaUtils.restoreAllTables();
		}

		if(getTablesToClear().length > 0)
		{
			SchemaUtils.restoreTable(false, getTablesToClear());
		}
	}

	@After
	public void resetAndCheckDatabase()
		throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
	{
		// Reset all tables changed or cleared
		SchemaUtils.restoreTable(getTablesToRestore());
		SchemaUtils.restoreTable(getTablesToClear());

		// Make sure there are no residual db changes
		Set<String> errors = SchemaUtils.getFailedChecksums();

		if(errors.size() > 0)
		{
			Assert.fail(String.join("\n", errors));
		}
	}
}
