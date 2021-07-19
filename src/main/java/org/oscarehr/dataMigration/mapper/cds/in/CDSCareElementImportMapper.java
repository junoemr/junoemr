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
package org.oscarehr.dataMigration.mapper.cds.in;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.model.measurement.BloodPressureMeasurement;
import org.oscarehr.dataMigration.model.measurement.DiabetesSelfManagementChallengesMeasurement;
import org.oscarehr.dataMigration.model.measurement.DiabetesSelfManagementCollaborativeMeasurement;
import org.oscarehr.dataMigration.model.measurement.DiabetesSelfManagementEducationalMeasurement;
import org.oscarehr.dataMigration.model.measurement.HeightMeasurement;
import org.oscarehr.dataMigration.model.measurement.HypoglycemicEpisodesMeasurement;
import org.oscarehr.dataMigration.model.measurement.Measurement;
import org.oscarehr.dataMigration.model.measurement.SelfMonitoringBloodGlucoseMeasurement;
import org.oscarehr.dataMigration.model.measurement.SmokingPacksMeasurement;
import org.oscarehr.dataMigration.model.measurement.SmokingStatusMeasurement;
import org.oscarehr.dataMigration.model.measurement.WaistCircumferenceMeasurement;
import org.oscarehr.dataMigration.model.measurement.WeightMeasurement;
import org.oscarehr.dataMigration.model.measurement.diabetesComplicationsScreening.DiabetesComplicationsScreeningEyeMeasurement;
import org.oscarehr.dataMigration.model.measurement.diabetesComplicationsScreening.DiabetesComplicationsScreeningFootMeasurement;
import org.oscarehr.dataMigration.model.measurement.diabetesComplicationsScreening.DiabetesComplicationsScreeningMeasurement;
import org.oscarehr.dataMigration.model.measurement.diabetesComplicationsScreening.DiabetesComplicationsScreeningNeurologicalExamMeasurement;
import org.oscarehr.dataMigration.model.measurement.diabetesMotivationalCounseling.DiabetesMotivationalCounselingExerciseMeasurement;
import org.oscarehr.dataMigration.model.measurement.diabetesMotivationalCounseling.DiabetesMotivationalCounselingMeasurement;
import org.oscarehr.dataMigration.model.measurement.diabetesMotivationalCounseling.DiabetesMotivationalCounselingNutritionMeasurement;
import org.oscarehr.dataMigration.model.measurement.diabetesMotivationalCounseling.DiabetesMotivationalCounselingOtherMeasurement;
import org.oscarehr.dataMigration.model.measurement.diabetesMotivationalCounseling.DiabetesMotivationalCounselingSmokingMeasurement;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;
import xml.cds.v5_0.BloodPressure;
import xml.cds.v5_0.CareElements;
import xml.cds.v5_0.DiabetesComplicationScreening;
import xml.cds.v5_0.DiabetesEducationalSelfManagement;
import xml.cds.v5_0.DiabetesMotivationalCounselling;
import xml.cds.v5_0.DiabetesSelfManagementChallenges;
import xml.cds.v5_0.DiabetesSelfManagementCollaborative;
import xml.cds.v5_0.Height;
import xml.cds.v5_0.HypoglycemicEpisodes;
import xml.cds.v5_0.SelfMonitoringBloodGlucose;
import xml.cds.v5_0.SmokingPacks;
import xml.cds.v5_0.SmokingStatus;
import xml.cds.v5_0.WaistCircumference;
import xml.cds.v5_0.Weight;

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
			measurement.setMeasurementValue(ynIndicatorToMeasurementValue(smokingStatus.getStatus()));
			measurements.add(measurement);
		}

		for(SmokingPacks smokingPacks : importStructure.getSmokingPacks())
		{
			SmokingPacksMeasurement measurement = new SmokingPacksMeasurement();
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(smokingPacks.getDate()).atStartOfDay());
			measurement.setMeasurementValue(String.valueOf(smokingPacks.getPerDay()));
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
			String examCode = StringUtils.trimToNull(complicationScreening.getExamCode());

			DiabetesComplicationsScreeningMeasurement measurement;
			if(CDSConstants.CT037.FOOT_EXAM.getCode().equalsIgnoreCase(examCode))
			{
				measurement = new DiabetesComplicationsScreeningFootMeasurement();
			}
			else if (CDSConstants.CT037.RETINAL_EXAM.getCode().equalsIgnoreCase(examCode))
			{
				measurement = new DiabetesComplicationsScreeningEyeMeasurement();
			}
			else if (CDSConstants.CT037.NEUROLOGICAL_EXAM.getCode().equalsIgnoreCase(examCode))
			{
				measurement = new DiabetesComplicationsScreeningNeurologicalExamMeasurement();
			}
			else
			{
				logEvent("'" + examCode + "' is not a valid CT037 value code, and was not imported");
				continue;
			}
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(complicationScreening.getDate()).atStartOfDay());
			measurement.setMeasurementValue(Measurement.VALUE_YES);
			measurements.add(measurement);
		}

		for(DiabetesMotivationalCounselling motivationalCounselling : importStructure.getDiabetesMotivationalCounselling())
		{
			String counselingPerformed = StringUtils.trimToNull(motivationalCounselling.getCounsellingPerformed());

			DiabetesMotivationalCounselingMeasurement measurement;
			if(CDSConstants.CT038.NUTRITION.getCode().equalsIgnoreCase(counselingPerformed))
			{
				measurement = new DiabetesMotivationalCounselingNutritionMeasurement();
			}
			else if(CDSConstants.CT038.EXERCISE.getCode().equalsIgnoreCase(counselingPerformed))
			{
				measurement = new DiabetesMotivationalCounselingExerciseMeasurement();
			}
			else if(CDSConstants.CT038.SMOKING_CESSATION.getCode().equalsIgnoreCase(counselingPerformed))
			{
				measurement = new DiabetesMotivationalCounselingSmokingMeasurement();
			}
			else if(CDSConstants.CT038.OTHER.getCode().equalsIgnoreCase(counselingPerformed))
			{
				measurement = new DiabetesMotivationalCounselingOtherMeasurement();
			}
			else
			{
				logEvent("'" + counselingPerformed + "' is not a valid CT038 value code, and was not imported");
				continue;
			}
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(motivationalCounselling.getDate()).atStartOfDay());
			measurement.setMeasurementValue(Measurement.VALUE_YES);
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
			measurement.setMeasurementValue(ynIndicatorToMeasurementValue(selfManagementChallenges.getChallengesIdentified()));
			measurements.add(measurement);
		}

		for(DiabetesEducationalSelfManagement educationalSelfManagement : importStructure.getDiabetesEducationalSelfManagement())
		{
			DiabetesSelfManagementEducationalMeasurement measurement = new DiabetesSelfManagementEducationalMeasurement();
			measurement.setObservationDateTime(ConversionUtils.toLocalDate(educationalSelfManagement.getDate()).atStartOfDay());
			measurement.setMeasurementValue(ynIndicatorToMeasurementValue(educationalSelfManagement.getEducationalTrainingPerformed()));
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
			measurement.setMeasurementValue(ynIndicatorToMeasurementValue(bloodGlucose.getSelfMonitoring()));
			measurements.add(measurement);
		}

		return measurements;
	}

	private String ynIndicatorToMeasurementValue(String ynIndicator)
	{
		return (CDSConstants.Y_INDICATOR_TRUE.equalsIgnoreCase(ynIndicator)) ? Measurement.VALUE_YES : Measurement.VALUE_NO;
	}
}
