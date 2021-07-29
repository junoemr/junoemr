/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package org.oscarehr.measurements.service;


import org.oscarehr.common.dao.MeasurementDao;
import org.oscarehr.common.model.Measurement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MeasurementsService
{
	@Autowired
	protected MeasurementDao measurementDao;

	public MeasurementsService() {}

	/**
	 * create a new measurement
	 * @param demographicNo - the demographic to which this measurement belongs
	 * @param providerNo - the provider to which this measurement belongs
	 * @param type - the type of the measurement
	 * @param observation - the observation value of this measurement
	 * @param measuringInstruction - a instruction string on how to take the measurement
	 * @param obsDate - the observation date of this measurement
	 * @param comment - a comment for this measurement
	 * @return - a new measurement
	 */
	public Measurement createNewMeasurement(Integer demographicNo, String providerNo, String type, String observation, String measuringInstruction, Date obsDate, String comment)
	{
		Measurement newMeasurement = new Measurement();
		newMeasurement.setCreateDate(new Date());
		newMeasurement.setDateObserved(obsDate);
		newMeasurement.setComments(comment);
		newMeasurement.setDataField(observation);
		newMeasurement.setMeasuringInstruction(measuringInstruction);
		newMeasurement.setDemographicId(demographicNo);
		newMeasurement.setProviderNo(providerNo);
		newMeasurement.setType(type);
		return newMeasurement;
	}

	/**
	 * create a new measurement (with blank comment and measuring instruction)
	 * @param demographicNo - demographic to which this measurement belongs
	 * @param type - type of measurement
	 * @param observation - observation value
	 * @param obsDate - observation date
	 * @param comment - comment
	 * @return - a new measurement
	 */
	public Measurement createNewMeasurement(Integer demographicNo, String providerNo, String type, String observation, Date obsDate, String comment)
	{
		if (comment == null)
		{
			comment = org.oscarehr.dataMigration.model.measurement.Measurement.DEFAULT_COMMENT;
		}
		return createNewMeasurement(demographicNo, providerNo, type, observation, "", obsDate, comment);
	}

	/**
	 * create a new measurement (with blank comment and measuring instruction)
	 * @param demographicNo - demographic to which this measurement belongs
	 * @param type - type of measurement
	 * @param observation - observation value
	 * @param obsDate - observation date
	 * @return - a new measurement
	 */
	public Measurement createNewMeasurement(Integer demographicNo, String providerNo, String type, String observation, Date obsDate)
	{
		return createNewMeasurement(demographicNo, providerNo, type, observation, "", obsDate, org.oscarehr.dataMigration.model.measurement.Measurement.DEFAULT_COMMENT);
	}
}
