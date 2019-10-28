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

package oscar.oscarEncounter.oscarMeasurements.pageUtil;

import org.junit.Assert;
import org.junit.Test;

public class MeasurementGraphAction2Test
{
	@Test
	public void getParameters_nullRange_expectEmptyArray()
	{
		double[] expectedReturnType = {};
		double[] returnedParams = MeasurementGraphAction2.getParameters(null, "");
		assert returnedParams != null;
		Assert.assertEquals(expectedReturnType.length, returnedParams.length);
	}

	@Test
	public void getParameters_emptyRange_expectEmptyArray()
	{
		double[] expectedReturnType = {};
		double[] returnedParams = MeasurementGraphAction2.getParameters("", "");
		assert returnedParams != null;
		Assert.assertEquals(expectedReturnType.length, returnedParams.length);
	}

	@Test
	public void getParameters_validRangeNullUnits_expectDoubleArrayWithFiveParams()
	{
		String range = "2.3 - 4.6";
		Assert.assertEquals(5, MeasurementGraphAction2.getParameters(range, null).length);
	}


	@Test
	public void getParameters_spacePaddedDashAsRange_expectEmptyArray()
	{
		double[] expectedReturnType = {};
		double[] returnedParams = MeasurementGraphAction2.getParameters(" - ", null);
		assert returnedParams != null;
		Assert.assertEquals(expectedReturnType.length, returnedParams.length);
	}

	@Test
	public void reformatRange_nullString_expectEmptyString()
	{
		Assert.assertEquals("", MeasurementGraphAction2.reformatRange(null));
	}

	@Test
	public void reformatRange_emptyString_expectEmptyString()
	{
		Assert.assertEquals("", MeasurementGraphAction2.reformatRange(""));
	}

	@Test
	public void reformatRange_lessThanEqualToNumber_expectRangeString()
	{
		Assert.assertEquals("0 - 2", MeasurementGraphAction2.reformatRange(" <= 2"));
	}

	@Test
	public void reformatRange_numberLessThanEqualTo_expectRangeString()
	{
		Assert.assertEquals("2.0 - 4.0", MeasurementGraphAction2.reformatRange("2 <="));
	}

	@Test
	public void reformatRange_lessThanNumber_expectRangeString()
	{
		Assert.assertEquals("0 - 2", MeasurementGraphAction2.reformatRange(" < 2"));
	}

	@Test
	public void reformatRange_numberLessThan_expectRangeString()
	{
		Assert.assertEquals("0 - 2", MeasurementGraphAction2.reformatRange("2 < "));
	}

	@Test
	public void reformatRange_dashSplitAndNumber_expectRangeString()
	{
		Assert.assertEquals("0 - 2", MeasurementGraphAction2.reformatRange(" - 2"));
	}

	@Test
	public void reformatRange_greaterThanEqualToNumber_expectRangeString()
	{
		Assert.assertEquals("2.0 - 4.0", MeasurementGraphAction2.reformatRange(">= 2"));
	}

	@Test
	public void reformatRange_greaterThanEqualToNotNumber_expectSameString()
	{
		Assert.assertEquals(">= string", MeasurementGraphAction2.reformatRange(">= string"));
	}

	@Test
	public void reformatRange_numberGreaterThanEqualTo_expectRangeString()
	{
		Assert.assertEquals("0 - 2", MeasurementGraphAction2.reformatRange("2 >="));
	}

	@Test
	public void reformatRange_greaterThanNumber_expectRangeString()
	{
		Assert.assertEquals("2.0 - 4.0", MeasurementGraphAction2.reformatRange("> 2"));
	}

	@Test
	public void reformatRange_greaterThanNonNumber_expectSameString()
	{
		Assert.assertEquals("> string", MeasurementGraphAction2.reformatRange("> string"));
	}

	@Test
	public void reformatRange_numberGreaterThan_expectRangeString()
	{
		Assert.assertEquals("0 - 2", MeasurementGraphAction2.reformatRange("2 >"));
	}

	@Test
	public void reformatRange_numberAndDashSplit_expectRangeString()
	{
		Assert.assertEquals("2.0 - 4.0", MeasurementGraphAction2.reformatRange("2 -"));
	}

	@Test
	public void reformatRange_nonNumberAndDashSplit_expectSameString()
	{
		Assert.assertEquals("string -", MeasurementGraphAction2.reformatRange("string -"));
	}
}
