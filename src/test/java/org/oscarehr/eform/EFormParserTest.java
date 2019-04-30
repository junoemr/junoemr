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

package org.oscarehr.eform;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oscarehr.common.dao.DaoTestFixtures;
import oscar.eform.EFormLoader;
import oscar.eform.data.DatabaseAP;
import oscar.eform.data.EForm;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EFormParserTest
{
	@BeforeClass
	public static void loadBeans()
	{
		DaoTestFixtures.setupBeanFactory();
	}


	/**
	 * test the html output of the eform setInputFields function.
	 */
	@Test
	public void testSetupInputFields()
	{
		String[] inputHTML = {
				// attribute position. (start, middle, end)
				"<input name=\"one\" type=\"text\" value=\"foobar\" oscarDBinput=\"one\"> </input>",
				"<input oscarDBinput=\"one\" name=\"one\" type=\"text\" value=\"foobar\"> </input>",
				"<input name=\"one\" type=\"text\" oscarDBinput=\"one\" value=\"foobar\"> </input>",

				// multi tag
				"<input name=\"one\" type=\"text\" oscarDBinput=\"one\" value=\"foobar\"></input>" +
				"<input name=\"two\" type=\"text\" oscarDBinput=\"two\" value=\"fiz\"></input>" +
				"<p> HELLO </p>" +
				"<input name=\"three\" type=\"text\" oscarDBinput=\"three\" value=\"bang\"></input>"
		};
		List<List<String>> inputFieldList = Arrays.asList(
				Arrays.asList("one"),
				Arrays.asList("one"),
				Arrays.asList("one"),
				Arrays.asList("one", "two", "three")
		);

		Pattern lookForInputFields = Pattern.compile("id='_oscarupdatefields' name='_oscarupdatefields' value='([\\w\\d_%]+)");

		int i = 0;
		for (String html : inputHTML)
		{
			EForm eform = new EForm();
			eform.setFormHtml(html);
			eform.setupInputFields();

			String outputHTML = eform.getFormHtml();
			Matcher match = lookForInputFields.matcher(outputHTML);
			Assert.assertTrue("did not find eform fields list in html", match.find());
			String[] fields = match.group(1).split("%");

			int found = 0;
			for (String field : fields)
			{
				if (inputFieldList.get(i).contains(field))
				{
					found ++;
				}
			}

			Assert.assertTrue("all input fields not present in the output field list", inputFieldList.get(i).size() == found);

			i ++;
		}
	}


	/**
	 * test that html value attributes are set correctly
	 */
	@Test
	public void testSetValues()
	{
		String[] inputHTML = {
				// value attribute position (start, middle, end)
				"<input type=\"hidden\" id=\"itineraryrows\" name=\"itineraryrows\" value=\"trip_,2,1,10\">",
				"<input type=\"hidden\" id=\"itineraryrows\"  value=\"trip_,2,1,10\" name=\"itineraryrows\">",
				"<input value=\"trip_,2,1,10\" type=\"hidden\" id=\"itineraryrows\" name=\"itineraryrows\" >",

				// more tags
				"<input value=\"trip_,2,1,10\" type=\"hidden\" id=\"itineraryrows\" name=\"itineraryrows\" >" +
				"<input type=\"hidden\" id=\"vaccinerows\" name=\"vaccinerows\" value=\"extra_vaccine_,1,0,4\">" +
				"<input type=\"hidden\" id=\"prescriptionrows\" name=\"prescriptionrows\" value=\"extra_prescription_,1,0,4\">" +
				"<input type=\"hidden\" id=\"counsellingrows\" name=\"counsellingrows\" value=\"extra_counselling_,1,0,4\">" +
				"<input type=\"hidden\" id=\"communicationrows\" name=\"communicationrows\" value=\"extra_communication_,1,0,12\">"

		};
		List<List<String>> inputAllNames = Arrays.asList(
				Arrays.asList("itineraryrows"),
				Arrays.asList("itineraryrows"),
				Arrays.asList("itineraryrows"),
				Arrays.asList("vaccinerows", "prescriptionrows", "communicationrows")
		);
		List<List<String>> inputAllValues = Arrays.asList(
				Arrays.asList("myValue"),
				Arrays.asList("diffValue123"),
				Arrays.asList("fizz-bang"),
				Arrays.asList("foo", "bar", "fiz")
		);


		int i =0;
		for (String html : inputHTML)
		{
			EForm eform = new EForm();
			eform.setFormHtml(html);
			//twice to attempt to cause duplicate bug
			eform.setValues(inputAllNames.get(i), inputAllValues.get(i));
			eform.setValues(inputAllNames.get(i), inputAllValues.get(i));

			String outHTML = eform.getFormHtml();

			for (int z = 0; z < inputAllNames.get(i).size(); z++)
			{
				Matcher match = Pattern.compile("name=['\"]" + inputAllNames.get(i).get(z) +
						"[^>]+value=['\"]" + inputAllValues.get(i).get(z)).matcher(outHTML);
				boolean found = match.find();

				if (!found)
				{
					match = Pattern.compile("value=['\"]" + inputAllValues.get(i).get(z) +
							"[^>]+name=['\"]" + inputAllNames.get(i).get(z) ).matcher(outHTML);
					found = match.find();
				}

				Assert.assertTrue("did not find value attribute: " + outHTML, found);
				Assert.assertFalse("found more that one value attribute: " + outHTML, match.find());
			}


			i ++;
		}
	}

	/**
	 * 	test pull values from the DB and placing them in html. aka the process used for the tags found in apconfig.xml.
	 * 	fake ap tag used here (no db data used).
 	 */
	@Test
	public void testSetDatabaseAPs()
	{
		DatabaseAP myAp = new DatabaseAP("test1", "SELECT \"foobar\" as output", "${output}");

		String[] inputHTML = {
			// attribute position (start, middle, end)
			"<input type=\"text\" name=\"test1\" oscarDB=\"test1\"></input>",
			"<input type=\"text\" oscarDB=\"test1\" name=\"test1\" ></input>",
			"<input oscarDB=\"test1\" type=\"text\" name=\"test1\" ></input>",

			//multiple tags
			"<input value=\"deleteMe\" type=\"hidden\" id=\"itineraryrows\" name=\"itineraryrows\" oscarDB=\"test1\">" +
			"<input type=\"hidden\" id=\"vaccinerows\" name=\"vaccinerows\" value=\"deleteMe\" oscarDB=\"test1\">" +
			"<input type=\"hidden\" id=\"prescriptionrows\" name=\"prescriptionrows\" value=\"deleteMe\" oscarDB=\"test1\">" +
			"<input type=\"hidden\" id=\"counsellingrows\" name=\"counsellingrows\" value=\"deleteMe\" oscarDB=\"test1\">" +
			"<input type=\"hidden\" id=\"communicationrows\" name=\"communicationrows\" value=\"deleteMe\" oscarDB=\"test1\">"
		};

		String[] outputExpectedValues = {
				"foobar",
				"foobar",
				"foobar",
				"foobar"
		};

		int[] outputExpectedCount = {
			1,
			1,
			1,
			5
		};

		int i =0;
		for (String html: inputHTML)
		{
			EForm eform = new EForm();
			eform.setFormHtml(html);
			EFormLoader.clearDatabaseAPs();
			EFormLoader.addDatabaseAP(myAp);
			eform.setDatabaseAPs();

			String outputHTML = eform.getFormHtml();

			checkTestApResult(outputHTML, outputExpectedValues[i], outputExpectedCount[i]);

			i++;
		}
	}

	@Test
	public void testSetDatabaseUpdateAPs()
	{
		DatabaseAP myAp = new DatabaseAP("test1", "SELECT \"fizbang\" as output", "${output}");

		String[] inputHTML = {
			// attribute position (start, middle, end).
			"<input type=\"text\" name=\"test1\" oscarUpdateDB=\"test1\"></input>",
			"<input type=\"text\" oscarUpdateDB=\"test1\" name=\"test1\"></input>",
			"<input oscarUpdateDB=\"test1\" type=\"text\" name=\"test1\"></input>",

			//multiple tags
			"<input value=\"deleteMe\" type=\"hidden\" id=\"itineraryrows\" name=\"itineraryrows\" oscarUpdateDB=\"test1\">" +
			"<input type=\"hidden\" id=\"vaccinerows\" name=\"vaccinerows\" value=\"deleteMe\" oscarUpdateDB=\"test1\">" +
			"<input type=\"hidden\" id=\"prescriptionrows\" name=\"prescriptionrows\" value=\"deleteMe\" oscarUpdateDB=\"test1\">" +
			"<input type=\"hidden\" id=\"counsellingrows\" name=\"counsellingrows\" value=\"deleteMe\" oscarUpdateDB=\"test1\">" +
			"<input type=\"hidden\" id=\"communicationrows\" name=\"communicationrows\" value=\"deleteMe\" oscarUpdateDB=\"test1\">"
		};

		String[] outputExpectedValues = {
				"fizbang",
				"fizbang",
				"fizbang",
				"fizbang"
		};

		int[] outputExpectedCounts = {
				1,
				1,
				1,
				5
		};

		int i = 0;
		for (String html: inputHTML)
		{
			EForm eform = new EForm();
			eform.setFormHtml(html);
			EFormLoader.clearDatabaseAPs();
			EFormLoader.addDatabaseAP(myAp);

			//couple of times to produce duplicate bug.
			eform.setDatabaseUpdateAPs();
			eform.setDatabaseUpdateAPs();
			eform.setDatabaseUpdateAPs();

			String outputHTML = eform.getFormHtml();
			checkTestApResult(outputHTML, outputExpectedValues[i], outputExpectedCounts[i]);
			i++;
		}
	}

	@Test
	public void testSetSignatureCode()
	{
		String[] inputHTML = {
			"${oscar_signature_code}",

			"<h1> hello I'm a doctor </h1>" +
			"${oscar_signature_code} " +
			"<h2> and this is my sig </h2> "
		};

		for (String html : inputHTML)
		{
			EForm eform = new EForm();
			eform.setFormHtml(html);
			eform.setSignatureCode("test","firefox", "1", "1");

			String outputHTML = eform.getFormHtml();
			Assert.assertTrue("could not find signature java script in output: " + outputHTML,
					outputHTML.contains("<script") &&
					outputHTML.contains("_signatureRequestId") &&
					outputHTML.contains("_contextPath = 'test'")
					);
		}
	}

	@Test
	public void testSetSource()
	{
		String[] inputHTML = {
				"${source}",

				"<h1> hello I'm a doctor </h1>" +
				"${source} " +
				"<h2> and this is my source </h2> "
		};

		for (String html : inputHTML)
		{
			EForm eform = new EForm();
			eform.setFormHtml(html);
			String source = "<script> console.log('foobar') </script>";
			eform.setSource(source);

			String outputHTML = eform.getFormHtml();
			Assert.assertTrue("could not find signature java script in output: " + outputHTML,
								outputHTML.contains(source)
			);
		}
	}

	@Test
	public void testGetTemplate()
	{
		String[] inputHTML = {
				"<h1> template </h1>" +
				"<!-- <template>" +
				"<p>HELLO_WORLD</p>" +
				"</template> --> " +
				"<h1> template </h1>",

				"<!-- <template>" +
				"<input> foobar </input>" +
				"</template> --> "
		};

		String[] templateResult = {
				"<p>HELLO_WORLD</p>",
				"<input> foobar </input>"
		};

		int i =0;
		for (String html : inputHTML)
		{
			EForm eform = new EForm();
			eform.setFormHtml(html);
			String template = eform.getTemplate();
			Assert.assertTrue("template output did not contain expected output: " + template + " expecting: " + templateResult[i],
					template.contains(templateResult[i]));

			i ++;
		}
	}

	private void checkTestApResult(String outputHTML, String expectedValue, int expectedCount)
	{
		Matcher match = Pattern.compile("value=['\"]" + expectedValue).matcher(outputHTML);
		for (int z =0; z < expectedCount; z ++)
		{
			Assert.assertTrue("did not find expected value in html output: " + outputHTML + "looking for: value=" +expectedValue,
					match.find());
		}
		Assert.assertFalse("found more attributes than expected: " + outputHTML,
				match.find());


		Assert.assertFalse("found attribute value that should have been deleted: " + outputHTML,
				Pattern.compile("value=['\"]deleteMe").matcher(outputHTML).find());

	}


}
