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
package org.oscarehr.ws.rest.filter;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.oscarehr.ws.common.annotation.MaskParameter;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class LoggingFilterPasswordFilterTest
{
	private static final String filterText = MaskParameter.MASK;
	private static final Logger logger = Logger.getLogger(LoggingFilterPasswordFilterTest.class);

	private String inputStr;
	private String expectedStr;
	private String[] fields;

	public LoggingFilterPasswordFilterTest(String inputStr, String expectedStr, String...fields)
	{
		this.inputStr = inputStr;
		this.expectedStr = expectedStr;
		this.fields = fields;
	}

	@Parameterized.Parameters
	public static Collection testData()
	{
		return Arrays.asList(new Object[][]
				{
						{null, null, new String[] {"password"}}, // test null case
						{"", "", new String[] {"password"}}, // test empty case
						{"{}", "{}", new String[] {"password"}}, // empty json case
						{"test string", "test string", new String[] {}}, // test missing param case
						{"{\"test\":true}", "{\"test\":true}", new String[] {"password"}}, // json no password case
						{"{\"test\":\"yes\"}", "{\"test\":\"yes\"}", new String[] {"password"}}, // json no password case with quotes
						//test password filter cases
						{"{\"password\":\"secret\"}",
								"{\"password\":\""+filterText+"\"}",
								new String[] {"password"}},
						{"{\"password\" : \"secret\"}",
								"{\"password\" : \""+filterText+"\"}",
								new String[] {"password"}},
						{"{\"password\":\"secret1\", \"otherPassword\":\"secret2\"}",
								"{\"password\":\""+filterText+"\", \"otherPassword\":\""+filterText+"\"}",
								new String[] {"password", "otherPassword"}},
						{"{\"test\":\"yes\", \"password\":\"secret1\", [{\"test2\":\"some crazy values '?)(*&^\\\"%$#@!\"}, {\"test3\":\"asdasd\"}, {\"otherPassword\":\"secret2\"}]}",
								"{\"test\":\"yes\", \"password\":\""+filterText+"\", [{\"test2\":\"some crazy values '?)(*&^\\\"%$#@!\"}, {\"test3\":\"asdasd\"}, {\"otherPassword\":\""+filterText+"\"}]}",
								new String[] {"password", "otherPassword"}},
						{"{\"password\":\"s@e%c&r\\\"e't\"}",
								"{\"password\":\""+filterText+"\"}",
								new String[] {"password"}},
						{"{\"password\":\"s@e%c&r\\\"e't123\", \"otherPassword\":\"s@e%c&r\\\"e't654\"}",
								"{\"password\":\""+filterText+"\", \"otherPassword\":\""+filterText+"\"}",
								new String[] {"password", "otherPassword"}},

				});
	}

	@Test
	public void testPassword()
	{
		String filteredResult = LoggingFilter.removePasswordData(inputStr, fields);
		logger.info("\n" + inputStr + "\n" + filteredResult);
		assertThat(expectedStr, equalTo(filteredResult));
	}
}
