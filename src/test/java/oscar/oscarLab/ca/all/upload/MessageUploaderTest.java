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
package oscar.oscarLab.ca.all.upload;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.oscarehr.common.model.Demographic;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MessageUploaderTest
{
	private final String lastName;
	private final String firstName;
	private final String sex;
	private final String dob;
	private final String hin;
	private final Demographic expectedResult;

	public MessageUploaderTest(String lastName, String firstName, String sex, String dob, String hin, Demographic expectedResult)
	{
		this.lastName = lastName;
		this.firstName = firstName;
		this.sex = sex;
		this.dob = dob;
		this.hin = hin;
		this.expectedResult = expectedResult;
	}

	@Parameterized.Parameters
	public static Collection<?> testData()
	{
		return Arrays.asList(new Object[][]
				{
						{null, null, null, null, null, null},
						{"", "", "", "", "", null},
				});
	}

	@Test
	public void runTest() throws ParseException
	{
		Demographic result = MessageUploader.getDemographicFromLabInfo(lastName, firstName, sex, dob, hin);
		assertEquals(result, this.expectedResult);
	}

}
