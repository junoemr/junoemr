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
package org.oscarehr.report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.oscarehr.util.MiscUtils;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class SQLReportHelperTest_applyEnforcedLimit
{
	private static final Integer maxLimit = 1000;

	private String rawSQL;
	private String expectedSQL;

	public SQLReportHelperTest_applyEnforcedLimit(String rawSQL, String expectedSQL)
	{
		this.rawSQL = rawSQL;
		this.expectedSQL = expectedSQL;
	}

	@Parameterized.Parameters
	public static Collection testData()
	{
		return Arrays.asList(new Object[][]
				{
						{"",
								" LIMIT " + maxLimit},
						{"SELECT x FROM provider",
								"SELECT x FROM provider LIMIT " + maxLimit},
						{"SELECT x FROM provider;",
								"SELECT x FROM provider LIMIT " + maxLimit},
						{"SELECT x FROM provider   \n \t ; \n \t  ",
								"SELECT x FROM provider LIMIT " + maxLimit},
						{"SELECT x FROM provider ORDER BY provider_no",
								"SELECT x FROM provider ORDER BY provider_no LIMIT " + maxLimit},
						{"SELECT x FROM provider ORDER BY provider_no;",
								"SELECT x FROM provider ORDER BY provider_no LIMIT " + maxLimit},
						{"SELECT x FROM provider ORDER BY provider_no LIMIT " + (maxLimit+100),
								"SELECT x FROM provider ORDER BY provider_no LIMIT " + maxLimit},
						{"SELECT x FROM provider LIMIT " + 1,
								"SELECT x FROM provider LIMIT " + 1},
						{"SELECT x FROM provider LIMIT " + 1 + ";",
								"SELECT x FROM provider LIMIT " + 1 + ";"},
						{"SELECT x FROM provider LIMIT " + 1 + ", 2",
								"SELECT x FROM provider LIMIT " + 1 + ", 2"},
						{"SELECT x FROM provider LIMIT " + (maxLimit+100) + ", 2",
								"SELECT x FROM provider LIMIT " + maxLimit + ", 2"},
						{"SELECT x FROM provider LIMIT " + 1 + " OFFSET 2",
								"SELECT x FROM provider LIMIT " + 1 + " OFFSET 2"},
						{"SELECT x FROM provider LIMIT " + (maxLimit+100) + " OFFSET 2",
								"SELECT x FROM provider LIMIT " + maxLimit + " OFFSET 2"},
						{"SELECT x FROM provider OFFSET 2",
								"SELECT x FROM provider LIMIT " + maxLimit + " OFFSET 2"},
						{"SELECT x FROM (select x FROM provider LIMIT 1) AS A;",
								"SELECT x FROM (select x FROM provider LIMIT 1) AS A LIMIT " + maxLimit},
				});
	}


	@Test
	public void testApplyEnforcedLimit()
	{
		String actualResult = SQLReportHelper.applyEnforcedLimit(rawSQL, maxLimit);
		MiscUtils.getLogger().info("testApplyEnforcedLimit\nExpected: '" + expectedSQL +"'\nActual:   '" + actualResult + "'");
		assertThat(actualResult.toLowerCase(), is(expectedSQL.toLowerCase()));
	}
}
