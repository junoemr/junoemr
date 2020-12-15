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
package org.oscarehr.demographicImport.mapper.cds.in;

import org.oscarehr.common.xml.cds.v5_0.model.BloodPressure;
import org.oscarehr.common.xml.cds.v5_0.model.CareElements;
import org.oscarehr.common.xml.cds.v5_0.model.DiabetesComplicationScreening;
import org.oscarehr.common.xml.cds.v5_0.model.DiabetesEducationalSelfManagement;
import org.oscarehr.common.xml.cds.v5_0.model.DiabetesMotivationalCounselling;
import org.oscarehr.common.xml.cds.v5_0.model.DiabetesSelfManagementChallenges;
import org.oscarehr.common.xml.cds.v5_0.model.DiabetesSelfManagementCollaborative;
import org.oscarehr.common.xml.cds.v5_0.model.Height;
import org.oscarehr.common.xml.cds.v5_0.model.HypoglycemicEpisodes;
import org.oscarehr.common.xml.cds.v5_0.model.SelfMonitoringBloodGlucose;
import org.oscarehr.common.xml.cds.v5_0.model.SmokingPacks;
import org.oscarehr.common.xml.cds.v5_0.model.SmokingStatus;
import org.oscarehr.common.xml.cds.v5_0.model.WaistCircumference;
import org.oscarehr.common.xml.cds.v5_0.model.Weight;
import org.oscarehr.demographicImport.model.measurement.BloodPressureMeasurement;
import org.oscarehr.demographicImport.model.measurement.DiabetesComplicationsScreeningFootMeasurement;
import org.oscarehr.demographicImport.model.measurement.DiabetesComplicationsScreeningMeasurement;
import org.oscarehr.demographicImport.model.measurement.DiabetesMotivationalCounselingMeasurement;
import org.oscarehr.demographicImport.model.measurement.DiabetesSelfManagementChallengesMeasurement;
import org.oscarehr.demographicImport.model.measurement.DiabetesSelfManagementCollaborativeMeasurement;
import org.oscarehr.demographicImport.model.measurement.DiabetesSelfManagementEducationalMeasurement;
import org.oscarehr.demographicImport.model.measurement.HeightMeasurement;
import org.oscarehr.demographicImport.model.measurement.HypoglycemicEpisodesMeasurement;
import org.oscarehr.demographicImport.model.measurement.Measurement;
import org.oscarehr.demographicImport.model.measurement.SelfMonitoringBloodGlucoseMeasurement;
import org.oscarehr.demographicImport.model.measurement.SmokingPacksMeasurement;
import org.oscarehr.demographicImport.model.measurement.SmokingStatusMeasurement;
import org.oscarehr.demographicImport.model.measurement.WaistCircumferenceMeasurement;
import org.oscarehr.demographicImport.model.measurement.WeightMeasurement;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class CDSCareElementImportMapper extends AbstractCDSImportMapper<CareElements, List<Measurement>>
{
	public CDSCareElementImportMapper()
	{
		super();
	}

	@Override
	public List<Measurement> importToJuno(CareElements importStructure)
	{
		List<Measurement> measurements = new ArrayList<>();

		for(SmokingStatus smokingStatus : importStructure.getSmokingStatus())
		{
			SmokingStatusMeasurement measurement = new SmokingStatusMeasurement();
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(smokingStatus.getDate()).atStartOfDay());
			measurement.setMeasurementValue(smokingStatus.getStatus());
			measurements.add(measurement);
		}

		for(SmokingPacks smokingPacks : importStructure.getSmokingPacks())
		{
			SmokingPacksMeasurement measurement = new SmokingPacksMeasurement();
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(smokingPacks.getDate()).atStartOfDay());
			measurement.setMeasurementValue(String.valueOf(smokingPacks.getPerDay().longValue()));
			measurements.add(measurement);
		}

		for(Weight weight : importStructure.getWeight())
		{
			WeightMeasurement measurement = new WeightMeasurement();
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(weight.getDate()).atStartOfDay());
			measurement.setMeasurementValue(weight.getWeight());
			measurements.add(measurement);
		}

		for(Height height : importStructure.getHeight())
		{
			HeightMeasurement measurement = new HeightMeasurement();
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(height.getDate()).atStartOfDay());
			measurement.setMeasurementValue(height.getHeight());
			measurements.add(measurement);
		}

		for(WaistCircumference waistCircumference : importStructure.getWaistCircumference())
		{
			WaistCircumferenceMeasurement measurement = new WaistCircumferenceMeasurement();
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(waistCircumference.getDate()).atStartOfDay());
			measurement.setMeasurementValue(waistCircumference.getWaistCircumference());
			measurements.add(measurement);
		}

		for(BloodPressure bloodPressure : importStructure.getBloodPressure())
		{
			BloodPressureMeasurement measurement = new BloodPressureMeasurement();
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(bloodPressure.getDate()).atStartOfDay());
			measurement.setSystolic(bloodPressure.getSystolicBP());
			measurement.setDiastolic(bloodPressure.getDiastolicBP());
			measurements.add(measurement);
		}

		for(DiabetesComplicationScreening complicationScreening : importStructure.getDiabetesComplicationsScreening())
		{
			//TODO which sub-class to use? can it be based on code?
			DiabetesComplicationsScreeningMeasurement measurement = new DiabetesComplicationsScreeningFootMeasurement();
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(complicationScreening.getDate()).atStartOfDay());
			measurement.setMeasurementValue(complicationScreening.getExamCode());
			measurements.add(measurement);
		}

		for(DiabetesMotivationalCounselling motivationalCounselling : importStructure.getDiabetesMotivationalCounselling())
		{
			DiabetesMotivationalCounselingMeasurement measurement = new DiabetesMotivationalCounselingMeasurement();
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(motivationalCounselling.getDate()).atStartOfDay());
			measurement.setMeasurementValue(motivationalCounselling.getCounsellingPerformed());
			measurements.add(measurement);
		}

		for(DiabetesSelfManagementCollaborative selfManagementCollaborative : importStructure.getDiabetesSelfManagementCollaborative())
		{
			DiabetesSelfManagementCollaborativeMeasurement measurement = new DiabetesSelfManagementCollaborativeMeasurement();
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(selfManagementCollaborative.getDate()).atStartOfDay());
			measurement.setMeasurementValue(selfManagementCollaborative.getDocumentedGoals());
			measurements.add(measurement);
		}

		for(DiabetesSelfManagementChallenges selfManagementChallenges : importStructure.getDiabetesSelfManagementChallenges())
		{
			DiabetesSelfManagementChallengesMeasurement measurement = new DiabetesSelfManagementChallengesMeasurement();
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(selfManagementChallenges.getDate()).atStartOfDay());
			measurement.setMeasurementValue(selfManagementChallenges.getChallengesIdentified());
			measurements.add(measurement);
		}

		for(DiabetesEducationalSelfManagement educationalSelfManagement : importStructure.getDiabetesEducationalSelfManagement())
		{
			DiabetesSelfManagementEducationalMeasurement measurement = new DiabetesSelfManagementEducationalMeasurement();
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(educationalSelfManagement.getDate()).atStartOfDay());
			measurement.setMeasurementValue(educationalSelfManagement.getEducationalTrainingPerformed());
			measurements.add(measurement);
		}

		for(HypoglycemicEpisodes hypoglycemicEpisodes : importStructure.getHypoglycemicEpisodes())
		{
			HypoglycemicEpisodesMeasurement measurement = new HypoglycemicEpisodesMeasurement();
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(hypoglycemicEpisodes.getDate()).atStartOfDay());
			measurement.setMeasurementValue(String.valueOf(hypoglycemicEpisodes.getNumOfReportedEpisodes().longValue()));
			measurements.add(measurement);
		}

		for(SelfMonitoringBloodGlucose bloodGlucose : importStructure.getSelfMonitoringBloodGlucose())
		{
			SelfMonitoringBloodGlucoseMeasurement measurement = new SelfMonitoringBloodGlucoseMeasurement();
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(bloodGlucose.getDate()).atStartOfDay());
			measurement.setMeasurementValue(bloodGlucose.getSelfMonitoring());
			measurements.add(measurement);
		}

		return measurements;
	}
}
