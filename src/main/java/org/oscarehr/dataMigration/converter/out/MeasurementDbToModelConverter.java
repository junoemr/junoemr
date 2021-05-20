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
package org.oscarehr.dataMigration.converter.out;

import org.oscarehr.common.model.Measurement;
import org.oscarehr.dataMigration.model.measurement.MeasurementFactory;
import org.springframework.stereotype.Component;

@Component
public class MeasurementDbToModelConverter extends
		BaseDbToModelConverter<Measurement, org.oscarehr.dataMigration.model.measurement.Measurement>
{

	@Override
	public org.oscarehr.dataMigration.model.measurement.Measurement convert(Measurement input)
	{
		// TODO is the factory how we want to convert this?
		return MeasurementFactory.getMeasurement(input);
	}
}
