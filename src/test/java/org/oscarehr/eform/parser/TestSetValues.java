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

@RunWith(Parameterized.class)
@SpringBootTest
public class TestSetValues extends DaoTestFixtures
{
	// Manually boot the Spring test environment because it's not possible to use:
	// @RunWith(SpringRunner.class)
	@ClassRule
	public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	private String inputHtml;
	private List<String> allNames;
	private List<String> allValues;


	public TestSetValues(String html, List<String> allNames, List<String> allValues)
	{
		inputHtml = html;
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
							"<input type=\"hidden\" id=\"itineraryrows\" name=\"itineraryrows\" value=\"trip_,2,1,10\">",
							Arrays.asList("itineraryrows"),
							Arrays.asList("myValue")
						},
						{
							"<input type=\"hidden\" id=\"itineraryrows\"  value=\"trip_,2,1,10\" name=\"itineraryrows\">",
							Arrays.asList("itineraryrows"),
							Arrays.asList("diffValue123")
						},
						{
							"<input value=\"trip_,2,1,10\" type=\"hidden\" id=\"itineraryrows\" name=\"itineraryrows\" >",
							Arrays.asList("itineraryrows"),
							Arrays.asList("fizz-bang")
						},
						{
								"<input value=\"trip_,2,1,10\" type=\"hidden\" id=\"itineraryrows\" name=\"itineraryrows\" >" +
								"<input type=\"hidden\" id=\"vaccinerows\" name=\"vaccinerows\" value=\"extra_vaccine_,1,0,4\">" +
								"<input type=\"hidden\" id=\"prescriptionrows\" name=\"prescriptionrows\" value=\"extra_prescription_,1,0,4\">" +
								"<input type=\"hidden\" id=\"counsellingrows\" name=\"counsellingrows\" value=\"extra_counselling_,1,0,4\">" +
								"<input type=\"hidden\" id=\"communicationrows\" name=\"communicationrows\" value=\"extra_communication_,1,0,12\">",
								Arrays.asList("vaccinerows", "prescriptionrows", "communicationrows"),
								Arrays.asList("foo", "bar", "fiz")
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

		for (int z = 0; z < this.allNames.size(); z++)
		{
			Matcher match = Pattern.compile("name=['\"]" + this.allNames.get(z) +
					"[^>]+value=['\"]" + this.allValues.get(z)).matcher(outHTML);
			boolean found = match.find();

			if (!found)
			{
				match = Pattern.compile("value=['\"]" + this.allValues.get(z) +
						"[^>]+name=['\"]" + this.allNames.get(z) ).matcher(outHTML);
				found = match.find();
			}

			Assert.assertTrue("did not find value attribute in html: \n" + outHTML + "with inputHTML: \n" + this.inputHtml, found);
			Assert.assertFalse("found more that one value attribute in html: \n" + outHTML + "with inputHTML: \n" + this.inputHtml, match.find());
		}


	}
}