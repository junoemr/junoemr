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
public class TestSetupInputFields extends DaoTestFixtures
{
	// Manually boot the Spring test environment because it's not possible to use:
	// @RunWith(SpringRunner.class)
	@ClassRule
	public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	private String inputHtml;
	private List<String> expectedOutput;

	public TestSetupInputFields(String html, List<String> expect)
	{
		inputHtml = html;
		expectedOutput = expect;
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
						{"<input name=\"one\" type=\"text\" value=\"foobar\" oscarDBinput=\"one\"> </input>", Arrays.asList("one")},
						{"<input oscarDBinput=\"one\" name=\"one\" type=\"text\" value=\"foobar\"> </input>", Arrays.asList("one")},
						{"<input name=\"one\" type=\"text\" oscarDBinput=\"one\" value=\"foobar\"> </input>", Arrays.asList("one")},
						{"<input name=\"one\" type=\"text\" oscarDBinput=\"one\" value=\"foobar\"></input>" +
							"<input name=\"two\" type=\"text\" oscarDBinput=\"two\" value=\"fiz\"></input>" +
							"<p> HELLO </p>" +
							"<input name=\"three\" type=\"text\" oscarDBinput=\"three\" value=\"bang\"></input>", Arrays.asList("one", "two", "three")}
				});
	}

	/**
	 * test the html output of the eform setInputFields function
	 */
	@Test
	public void testSetupInputFields()
	{
		Pattern lookForInputFields = Pattern.compile("id='_oscarupdatefields' name='_oscarupdatefields' value='([\\w\\d_%]+)");

		EForm eform = new EForm();
		eform.setFormHtml(this.inputHtml);
		eform.setupInputFields();

		String outputHTML = eform.getFormHtml();
		Matcher match = lookForInputFields.matcher(outputHTML);
		Assert.assertTrue("did not find eform fields list in html: \n" + outputHTML + " with inputHTML: \n" + this.inputHtml, match.find());
		String[] fields = match.group(1).split("%");

		int found = 0;
		for (String field : fields)
		{
			if (this.expectedOutput.contains(field))
			{
				found ++;
			}
		}

		Assert.assertEquals("all input fields not present in the output field list. html: \n" + outputHTML + "with inputHTML: \n" + this.inputHtml
				, this.expectedOutput.size(), found);


	}
}
