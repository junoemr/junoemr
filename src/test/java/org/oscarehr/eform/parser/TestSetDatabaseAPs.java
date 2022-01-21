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
import oscar.eform.EFormLoader;
import oscar.eform.data.DatabaseAP;
import oscar.eform.data.EForm;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(Parameterized.class)
@SpringBootTest
public class TestSetDatabaseAPs extends DaoTestFixtures
{
	// Manually boot the Spring test environment because it's not possible to use:
	// @RunWith(SpringRunner.class)
	@ClassRule
	public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	private String inputHtml;
	private String expected;
	private Integer expectedCount;

	public TestSetDatabaseAPs(String html, String expect, Integer expectCount)
	{
		this.inputHtml = html;
		this.expected = expect;
		this.expectedCount = expectCount;
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
					{"<input type=\"text\" name=\"test1\" oscarDB=\"test1\"></input>", "foobar", 1},
					{"<input type=\"text\" oscarDB=\"test1\" name=\"test1\" ></input>", "foobar", 1},
					{"<input oscarDB=\"test1\" type=\"text\" name=\"test1\" ></input>", "foobar", 1},
					{
						"<input value=\"deleteMe\" type=\"hidden\" id=\"itineraryrows\" name=\"itineraryrows\" oscarDB=\"test1\">" +
						"<input type=\"hidden\" id=\"vaccinerows\" name=\"vaccinerows\" value=\"deleteMe\" oscarDB=\"test1\">" +
						"<input type=\"hidden\" id=\"prescriptionrows\" name=\"prescriptionrows\" value=\"deleteMe\" oscarDB=\"test1\">" +
						"<input type=\"hidden\" id=\"counsellingrows\" name=\"counsellingrows\" value=\"deleteMe\" oscarDB=\"test1\">" +
						"<input type=\"hidden\" id=\"communicationrows\" name=\"communicationrows\" value=\"deleteMe\" oscarDB=\"test1\">",
						"foobar",
						5
					}
				});
	}

	/**
	 * 	test pull values from the DB and placing them in html. aka the process used for the tags found in apconfig.xml.
	 * 	fake ap tag used here (no db data used).
	 */
	@Test
	public void testSetDatabaseAPs()
	{
		DatabaseAP myAp = new DatabaseAP("test1", "SELECT \"foobar\" as output", "${output}");

		EForm eform = new EForm();
		eform.setFormHtml(this.inputHtml);
		EFormLoader.clearDatabaseAPs();
		EFormLoader.addDatabaseAP(myAp);
		eform.setDatabaseAPs();

		String outputHTML = eform.getFormHtml();

		Matcher match = Pattern.compile("value=['\"]" + this.expected).matcher(outputHTML);
		for (int z =0; z < this.expectedCount; z ++)
		{
			Assert.assertTrue("did not find expected value in html output: \n" + outputHTML + "looking for: value=" +this.expected + "" +
							" inputHTML: \n" + this.inputHtml,
					match.find());
		}
		Assert.assertFalse("found more attributes than expected in html: \n" + outputHTML + " inputHTML: \n" + this.inputHtml,
				match.find());


		Assert.assertFalse("found attribute value that should have been deleted in html: \n" + outputHTML + " inputHTML: \n" + this.inputHtml,
				Pattern.compile("value=['\"]deleteMe").matcher(outputHTML).find());
	}
}
