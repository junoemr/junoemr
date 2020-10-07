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

package oscar.oscarEncounter.oscarMeasurements.util;

import org.junit.Assert;
import org.junit.Test;
import org.oscarehr.common.model.Measurement;
import oscar.oscarEncounter.oscarMeasurements.util.MeasurementDSHelper;
import oscar.util.ConversionUtils;

public class MeasurementDSHelperTest
{
	@Test
	public void getMeasurementValue_testNull()
	{
		Measurement measurement = null;
		Assert.assertNull(MeasurementDSHelper.getMeasurementValue(measurement));
	}

	@Test
	public void getMeasurementValue_notValidMeasurement()
	{
		Measurement measurement = new Measurement();
		measurement.setType("TEST");
		measurement.setDataField("Not a double!");
		Assert.assertNull(MeasurementDSHelper.getMeasurementValue(measurement));
	}

	@Test
	public void getMeasurementValue_validMeasurement()
	{
		Double expectedResult = 2.3;
		Measurement measurement = new Measurement();
		measurement.setType("TEST");
		measurement.setDataField("2.3");
		Assert.assertEquals(expectedResult, MeasurementDSHelper.getMeasurementValue(measurement));
	}
}