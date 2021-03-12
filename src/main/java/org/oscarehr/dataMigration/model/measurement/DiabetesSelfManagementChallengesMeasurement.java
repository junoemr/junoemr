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

import static org.oscarehr.common.model.Measurement.MEASUREMENT_CODE_DIABETES_SELF_MANAGEMENT_CHALLENGES;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_DIABETES_SELF_MANAGEMENT_CHALLENGES;

@Data
public class DiabetesSelfManagementChallengesMeasurement extends Measurement
{
	public DiabetesSelfManagementChallengesMeasurement()
	{
		super();
	}
	public DiabetesSelfManagementChallengesMeasurement(org.oscarehr.common.model.Measurement dbModel)
	{
		super(dbModel);
	}

	@Override
	public String getTypeCode()
	{
		return MEASUREMENT_TYPE_DIABETES_SELF_MANAGEMENT_CHALLENGES;
	}

	public String getMeasurementCode()
	{
		return MEASUREMENT_CODE_DIABETES_SELF_MANAGEMENT_CHALLENGES;
	}
}
