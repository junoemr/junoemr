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
package org.oscarehr.dataMigration.model.measurement.diabetesMotivationalCounseling;

import lombok.Data;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;

import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_DIABETES_MOTIVATION_COUNSELING_COMPLETED_OTHER;

@Data
public class DiabetesMotivationalCounselingOtherMeasurement extends DiabetesMotivationalCounselingMeasurement
{
	public DiabetesMotivationalCounselingOtherMeasurement()
	{
		super();
	}
	public DiabetesMotivationalCounselingOtherMeasurement(org.oscarehr.common.model.Measurement dbModel)
	{
		super(dbModel);
	}

	@Override
	public String getTypeCode()
	{
		return MEASUREMENT_TYPE_DIABETES_MOTIVATION_COUNSELING_COMPLETED_OTHER;
	}

	@Override
	public String getCT038CodeValue()
	{
		return CDSConstants.CT038.OTHER.getCode();
	}
}
