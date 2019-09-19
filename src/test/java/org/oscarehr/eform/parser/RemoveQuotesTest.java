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
package org.oscarehr.eform.parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.oscarehr.common.dao.DaoTestFixtures;
import oscar.eform.EFormUtil;
import oscar.eform.data.EForm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/*
	Test a bug, where the user enters single quotes in to their string. This causes html parsing errors.
 */
@RunWith(Parameterized.class)
public class RemoveQuotesTest
{
	private String inputHtml;
	private String expectHtml;
	private List<String> allNames;
	private List<String> allValues;


	public RemoveQuotesTest(String html, List<String> allNames, List<String> allValues, String expectHtml)
	{
		this.inputHtml = html;
		this.expectHtml = expectHtml;
		this.allNames = allNames;
		this.allValues = allValues;
	}

	@BeforeClass
	public static void loadBeans()
	{
		DaoTestFixtures.setupBeanFactory();
	}

	@Parameterized.Parameters
	public static Collection testData()
	{
		return Arrays.asList(new Object[][]
				{
						{
								"<div name=\"quote\">\n</div>",
								Arrays.asList("quote"),
								Arrays.asList("'"),
								"<div name=\"quote\" value=\"'\" >\n</div>"
						},
						{
								"<div name=\"quote\">\n</div>",
								Arrays.asList("quote"),
								Arrays.asList("'fizbang'"),
								"<div name=\"quote\" value=\"fizbang\" >\n</div>"
						},
						{
								"<div name=\"quote\">\n</div>",
								Arrays.asList("quote"),
								Arrays.asList("\"'\""),
								"<div name=\"quote\" value=\"'\" >\n</div>"
						},
				});
	}

	/**
	 * test the single quote parsing error bug.
	 */
	@Test
	public void testQuoteParsingError()
	{
		EForm eform = new EForm();
		eform.setFormHtml(this.inputHtml);

		ArrayList<String> valNoQuotes = new ArrayList<>();
		for(String val : this.allValues)
		{
			valNoQuotes.add(EFormUtil.removeQuotes(val));
		}
		eform.setValues(this.allNames, valNoQuotes);
		String outHTML = eform.getFormHtml();

		Assert.assertEquals("expected: \n" + this.expectHtml + "\n But got: \n" + outHTML + "\n", outHTML, this.expectHtml);
	}
}