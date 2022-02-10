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
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import oscar.eform.data.EForm;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
	Test a bug where buy the inserted value="" tag escapes its tag, getting injected in to the HTML body instead.
 */
@RunWith(Parameterized.class)
@SpringBootTest
public class TestSetValuesBlackBoxTest extends DaoTestFixtures
{
	// Manually boot the Spring test environment because it's not possible to use:
	// @RunWith(SpringRunner.class)
	@ClassRule
	public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	private String inputHtml;
	private String expectHtml;
	private List<String> allNames;
	private List<String> allValues;


	public TestSetValuesBlackBoxTest(String html, List<String> allNames, List<String> allValues, String expectHtml)
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
								"<div name=\"demographics\">\n</div>",
								Arrays.asList("demographics"),
								Arrays.asList("myValue"),
								"<div name=\"demographics\" value=\"myValue\" >\n</div>"
						},
						{
								"<div name=\"quote\">\n</div>",
								Arrays.asList("quote"),
								Arrays.asList("'"),
								"<div name=\"quote\" value=\"'\" >\n</div>"
						},
						{
								"<div name=\"quote\">\n</div>",
								Arrays.asList("quote"),
								Arrays.asList("''''''''''"),
								"<div name=\"quote\" value=\"''''''''''\" >\n</div>"
						}
				});
	}

	/**
	 * test that html value attributes are set correctly
	 */
	@Test
	public void testSetValues()
	{
		EForm eform = new EForm();
		eform.setFormHtml(this.inputHtml);
		//twice to attempt to cause duplicate bug
		eform.setValues(this.allNames, this.allValues);
		eform.setValues(this.allNames, this.allValues);

		String outHTML = eform.getFormHtml();

		Assert.assertEquals("expected: \n" + this.expectHtml + "\n But got: \n" + outHTML + "\n", outHTML, this.expectHtml);
	}
}