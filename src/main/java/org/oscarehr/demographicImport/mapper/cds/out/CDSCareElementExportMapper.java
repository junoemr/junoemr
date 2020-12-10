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
package org.oscarehr.demographicImport.mapper.cds.out;

import org.oscarehr.common.xml.cds.v5_0.model.BloodPressure;
import org.oscarehr.common.xml.cds.v5_0.model.CareElements;
import org.oscarehr.common.xml.cds.v5_0.model.Height;
import org.oscarehr.common.xml.cds.v5_0.model.WaistCircumference;
import org.oscarehr.common.xml.cds.v5_0.model.Weight;
import org.oscarehr.demographicImport.model.measurement.BloodPressureMeasurement;
import org.oscarehr.demographicImport.model.measurement.HeightMeasurement;
import org.oscarehr.demographicImport.model.measurement.Measurement;
import org.oscarehr.demographicImport.model.measurement.WaistCircumferenceMeasurement;
import org.oscarehr.demographicImport.model.measurement.WeightMeasurement;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class CDSCareElementExportMapper extends AbstractCDSExportMapper<CareElements, Measurement>
{
	public CDSCareElementExportMapper()
	{
		super();
	}

	@Override
	public CareElements exportFromJuno(Measurement exportStructure)
	{
		CareElements careElements = objectFactory.createCareElements();

		//TODO a better pattern for this?
		if(exportStructure instanceof HeightMeasurement)
		{
			careElements.getHeight().add(getHeight((HeightMeasurement) exportStructure));
		}
		else if(exportStructure instanceof WeightMeasurement)
		{
			careElements.getWeight().add(getWeight((WeightMeasurement) exportStructure));
		}
		else if(exportStructure instanceof WaistCircumferenceMeasurement)
		{
			careElements.getWaistCircumference().add(getWaist((WaistCircumferenceMeasurement) exportStructure));
		}
		else if(exportStructure instanceof BloodPressureMeasurement)
		{
			careElements.getBloodPressure().add(getBloodPressure((BloodPressureMeasurement) exportStructure));
		}

		return careElements;
	}

	protected Height getHeight(HeightMeasurement exportStructure)
	{
		Height height = objectFactory.createHeight();
		height.setHeight(exportStructure.getMeasurementValue());
		height.setDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getObservationDateTime()));
		height.setHeightUnit(exportStructure.getMeasurementUnit());

		return height;
	}

	protected Weight getWeight(WeightMeasurement exportStructure)
	{
		Weight weight = objectFactory.createWeight();
		weight.setWeight(exportStructure.getMeasurementValue());
		weight.setDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getObservationDateTime()));
		weight.setWeightUnit(exportStructure.getMeasurementUnit());

		return weight;
	}

	protected WaistCircumference getWaist(WaistCircumferenceMeasurement exportStructure)
	{
		WaistCircumference waist = objectFactory.createWaistCircumference();
		waist.setWaistCircumference(exportStructure.getMeasurementValue());
		waist.setDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getObservationDateTime()));
		waist.setWaistCircumferenceUnit(exportStructure.getMeasurementUnit());

		return waist;
	}

	protected BloodPressure getBloodPressure(BloodPressureMeasurement exportStructure)
	{
		BloodPressure bloodPressure = objectFactory.createBloodPressure();
		bloodPressure.setSystolicBP(exportStructure.getSystolic());
		bloodPressure.setDiastolicBP(exportStructure.getDiastolic());
		bloodPressure.setDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getObservationDateTime()));
		bloodPressure.setBPUnit(exportStructure.getMeasurementUnit());

		return bloodPressure;
	}
}
