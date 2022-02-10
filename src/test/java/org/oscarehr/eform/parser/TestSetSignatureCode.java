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

@RunWith(Parameterized.class)
@SpringBootTest
public class TestSetSignatureCode extends DaoTestFixtures
{
	// Manually boot the Spring test environment because it's not possible to use:
	// @RunWith(SpringRunner.class)
	@ClassRule
	public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	private String inputHtml;

	public TestSetSignatureCode(String html)
	{
		inputHtml = html;
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
					{"${oscar_signature_code}"},

					{
						"<h1> hello I'm a doctor </h1>" +
						"${oscar_signature_code} " +
						"<h2> and this is my sig </h2> "
					}
				});
	}

	@Test
	public void testSetSignatureCode()
	{
		EForm eform = new EForm();
		eform.setFormHtml(this.inputHtml);
		eform.setSignatureCode("test","firefox", "1", "1");

		String outputHTML = eform.getFormHtml();
		Assert.assertTrue("could not find signature java script in output html: \n" + outputHTML + " inputHTML: \n" + this.inputHtml,
				outputHTML.contains("<script") &&
						outputHTML.contains("_signatureRequestId") &&
						outputHTML.contains("_contextPath = 'test'")
		);
	}

}
