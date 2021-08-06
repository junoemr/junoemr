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

package org.oscarehr.common.hl7.copd.mapper;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.hl7.Hl7Const;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.model.Measurement;
import org.oscarehr.dataMigration.service.ImporterExporterFactory;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.dataMigration.transfer.CoPDRecordData;
import org.oscarehr.dataMigration.transfer.CoPDRecordMessage;
import org.oscarehr.measurements.service.MeasurementsService;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.provider.model.ProviderData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_BLOOD_PRESSURE;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_HEIGHT;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_MINI_MENTAL_STATE_EXAM;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_WAIST;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_WEIGHT;

public class MeasurementsMapper extends AbstractMapper
{
	
	protected MeasurementsService measurementsService = SpringUtils.getBean(MeasurementsService.class);

	public MeasurementsMapper(ZPD_ZTR message, int providerRep, ImporterExporterFactory.IMPORT_SOURCE importSource)
	{
		super(message, providerRep, importSource);
	}

	public List<Measurement> getMeasurementList(Demographic demo, ProviderData providerData, CoPDRecordData recordData) throws HL7Exception
	{
		//TODO. import more measurements here
		return getZQOMeasurements(demo, providerData, recordData);
	}

	protected List<Measurement> getZQOMeasurements(Demographic demo, ProviderData providerData, CoPDRecordData recordData) throws HL7Exception
	{
		ArrayList<Measurement> measurements = new ArrayList<>();

		for (int i =0; i < provider.getZQOReps(); i++)
		{
			Date obsDate  = provider.getZQO(i).getZQO2_1_observationDate().getTs1_TimeOfAnEvent().getValueAsDate();
			if (obsDate == null)
			{
				obsDate = new Date();
			}
			String miniHealth = StringUtils.trimToNull(provider.getZQO(i).getZQO3_miniHealth().getValue());
			String systolicBP = StringUtils.trimToNull(provider.getZQO(i).getZQO4_systolicBP().getValue());
			String diastolicBP = StringUtils.trimToNull(provider.getZQO(i).getZQO5_diastolicBP().getValue());
			String height = StringUtils.trimToNull(provider.getZQO(i).getZQO6_height().getValue());
			String weight = StringUtils.trimToNull(provider.getZQO(i).getZQO7_weight().getValue());
			String waist  = StringUtils.trimToNull(provider.getZQO(i).getZQO8_waist().getValue());

			// if there is a message object corresponding to the segment id, set additional import info
			CoPDRecordMessage recordMessage = recordData.getMessage(Hl7Const.HL7_SEGMENT_ZQO, provider.getZQO(i).getZQO1_setId().getValue());
			if(recordMessage != null)
			{
				recordMessage.setDateTime(obsDate);
			}

			if (miniHealth != null)
			{
				measurements.add(measurementsService.createNewMeasurement(demo.getDemographicId(), providerData.getProviderNo().toString(), MEASUREMENT_TYPE_MINI_MENTAL_STATE_EXAM, miniHealth, obsDate));
			}

			if (systolicBP != null && diastolicBP != null)
			{
				measurements.add(measurementsService.createNewMeasurement(demo.getDemographicId(), providerData.getProviderNo().toString(), MEASUREMENT_TYPE_BLOOD_PRESSURE, systolicBP + "/" + diastolicBP, obsDate));
			}

			if (height != null && !height.equals("0.0"))
			{
				measurements.add(measurementsService.createNewMeasurement(demo.getDemographicId(), providerData.getProviderNo().toString(), MEASUREMENT_TYPE_HEIGHT, height, obsDate));
			}

			if (weight != null && !weight.equals("0.0"))
			{
				measurements.add(measurementsService.createNewMeasurement(demo.getDemographicId(), providerData.getProviderNo().toString(), MEASUREMENT_TYPE_WEIGHT, weight, obsDate));
			}

			if (waist != null)
			{
				measurements.add(measurementsService.createNewMeasurement(demo.getDemographicId(), providerData.getProviderNo().toString(), MEASUREMENT_TYPE_WAIST, waist, obsDate));
			}
		}

		return measurements;
	}

}
