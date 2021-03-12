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
package org.oscarehr.dataMigration.model.measurement;

import lombok.Data;

import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_BLOOD_PRESSURE;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_UNIT_MMHG;

@Data
public class BloodPressureMeasurement extends Measurement
{
	private String systolic;
	private String diastolic;

	public BloodPressureMeasurement()
	{
		super();
	}
	public BloodPressureMeasurement(org.oscarehr.common.model.Measurement dbModel)
	{
		super(dbModel);
		setMeasurementValue(dbModel.getDataField());
	}

	@Override
	public String getTypeCode()
	{
		return MEASUREMENT_TYPE_BLOOD_PRESSURE;
	}

	public String getMeasurementUnit()
	{
		return MEASUREMENT_UNIT_MMHG;
	}

	/**
	 * for blood pressure, this will split on the '/' character. if it is not provided, null will be set
	 * @param value the string value
	 */
	@Override
	public void setMeasurementValue(String value)
	{
		String splitChar = "/";
		if(value != null && value.contains(splitChar))
		{
			String[] systolicDiastolicBloodPressure = value.split(splitChar, 2);
			setMeasurementValue(systolicDiastolicBloodPressure[0], systolicDiastolicBloodPressure[1]);
		}
		else
		{
			setMeasurementValue(null, null);
		}
	}

	@Override
	public String getMeasurementValue()
	{
		if(systolic != null && diastolic != null)
		{
			return this.getSystolic() + "/" + this.getDiastolic();
		}
		else if (systolic != null)
		{
			return systolic;
		}
		else if (diastolic != null)
		{
			return diastolic;
		}
		return null;
	}

	public void setMeasurementValue(String systolic, String diastolic)
	{
		setSystolic(systolic);
		setDiastolic(diastolic);
	}

	public void setSystolic(String systolic)
	{
		this.systolic = systolic;
	}

	public void setDiastolic(String diastolic)
	{
		this.diastolic = diastolic;
	}
}
