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
package org.oscarehr.demographic.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class HinValidatorBCTest
{
	private String hin;
	private boolean expectedResult;

	public HinValidatorBCTest(String hin, boolean result)
	{
		this.hin = hin;
		this.expectedResult = result;
	}

	@Parameterized.Parameters
	public static Collection testData()
	{
		return Arrays.asList(new Object[][]
				{
						// invalid
						{null, false},
						{"", false},
						{"bubbles", false},
						{"123456789", false},
						// valid phn
						{"9012372173", true},
						// invalid phn with leading 9
						{"9999999999", false},
						// valid phns from test teleplan submissions file
						{"9151247483", true},
						{"9151210417", true},
						{"9151274799", true},
						// Valid Correctional
						// Legacy format for prisoners that had temporary/replacement PHNs assigned
						{"618622658", true},
						{"12345674", true},
				});
	}

	@Test
	public void testHinBritishColumbia()
	{
		boolean actualResult = HinValidator.isValid(hin,"BC");
		assertEquals(expectedResult, actualResult);
	}
}
