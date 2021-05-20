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

import org.oscarehr.dataMigration.model.measurement.diabetesComplicationsScreening.DiabetesComplicationsScreeningEyeMeasurement;
import org.oscarehr.dataMigration.model.measurement.diabetesComplicationsScreening.DiabetesComplicationsScreeningFootMeasurement;
import org.oscarehr.dataMigration.model.measurement.diabetesComplicationsScreening.DiabetesComplicationsScreeningNeurologicalExamMeasurement;
import org.oscarehr.dataMigration.model.measurement.diabetesMotivationalCounseling.DiabetesMotivationalCounselingExerciseMeasurement;
import org.oscarehr.dataMigration.model.measurement.diabetesMotivationalCounseling.DiabetesMotivationalCounselingNutritionMeasurement;
import org.oscarehr.dataMigration.model.measurement.diabetesMotivationalCounseling.DiabetesMotivationalCounselingOtherMeasurement;
import org.oscarehr.dataMigration.model.measurement.diabetesMotivationalCounseling.DiabetesMotivationalCounselingSmokingMeasurement;

import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_BLOOD_PRESSURE;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_COLLABORATIVE_GOAL_SETTING;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_DIABETES_EDUCATION;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_DIABETES_MOTIVATION_COUNSELING_COMPLETED_EXERCISE;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_DIABETES_MOTIVATION_COUNSELING_COMPLETED_NUTRITION;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_DIABETES_MOTIVATION_COUNSELING_COMPLETED_OTHER;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_DIABETES_MOTIVATION_COUNSELING_COMPLETED_SMOKING;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_DIABETES_SELF_MANAGEMENT_CHALLENGES;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_DILATED_EYE_EXAM;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_FOOT_EXAM;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_FOOT_EXAM_TEST_LOSS_OF_SENSATION;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_HEIGHT;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_HYPOGLYCEMIC_EPISODES;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_SELF_MONITOR_BLOOD_GLUCOSE;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_SMOKING_PACKS_PER_DAY;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_SMOKING_STATUS;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_WAIST;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_WC;
import static org.oscarehr.common.model.Measurement.MEASUREMENT_TYPE_WEIGHT;

public class MeasurementFactory
{
	public static Measurement getMeasurement(org.oscarehr.common.model.Measurement dbModel)
	{
		String measurementType = dbModel.getType();
		switch(measurementType)
		{
			case MEASUREMENT_TYPE_SMOKING_STATUS:
				return new SmokingStatusMeasurement(dbModel);
			case MEASUREMENT_TYPE_SMOKING_PACKS_PER_DAY:
				return new SmokingPacksMeasurement(dbModel);
			case MEASUREMENT_TYPE_HEIGHT:
				return new HeightMeasurement(dbModel);
			case MEASUREMENT_TYPE_WEIGHT:
				return new WeightMeasurement(dbModel);
			case MEASUREMENT_TYPE_WAIST:
			case MEASUREMENT_TYPE_WC:
				return new WaistCircumferenceMeasurement(dbModel);
			case MEASUREMENT_TYPE_BLOOD_PRESSURE:
				return new BloodPressureMeasurement(dbModel);
			case MEASUREMENT_TYPE_DILATED_EYE_EXAM:
				return new DiabetesComplicationsScreeningEyeMeasurement(dbModel);
			case MEASUREMENT_TYPE_FOOT_EXAM:
				return new DiabetesComplicationsScreeningFootMeasurement(dbModel);
			case MEASUREMENT_TYPE_FOOT_EXAM_TEST_LOSS_OF_SENSATION:
				return new DiabetesComplicationsScreeningNeurologicalExamMeasurement(dbModel);
			case MEASUREMENT_TYPE_DIABETES_MOTIVATION_COUNSELING_COMPLETED_NUTRITION:
				return new DiabetesMotivationalCounselingNutritionMeasurement(dbModel);
			case MEASUREMENT_TYPE_DIABETES_MOTIVATION_COUNSELING_COMPLETED_EXERCISE:
				return new DiabetesMotivationalCounselingExerciseMeasurement(dbModel);
			case MEASUREMENT_TYPE_DIABETES_MOTIVATION_COUNSELING_COMPLETED_SMOKING:
				return new DiabetesMotivationalCounselingSmokingMeasurement(dbModel);
			case MEASUREMENT_TYPE_DIABETES_MOTIVATION_COUNSELING_COMPLETED_OTHER:
				return new DiabetesMotivationalCounselingOtherMeasurement(dbModel);
			case MEASUREMENT_TYPE_COLLABORATIVE_GOAL_SETTING:
				return new DiabetesSelfManagementCollaborativeMeasurement(dbModel);
			case MEASUREMENT_TYPE_DIABETES_SELF_MANAGEMENT_CHALLENGES:
				return new DiabetesSelfManagementChallengesMeasurement(dbModel);
			case MEASUREMENT_TYPE_DIABETES_EDUCATION:
				return new DiabetesSelfManagementEducationalMeasurement(dbModel);
			case MEASUREMENT_TYPE_HYPOGLYCEMIC_EPISODES:
				return new HypoglycemicEpisodesMeasurement(dbModel);
			case MEASUREMENT_TYPE_SELF_MONITOR_BLOOD_GLUCOSE:
				return new SelfMonitoringBloodGlucoseMeasurement(dbModel);
			default:
				return new CustomMeasurement(dbModel);
		}
	}
}
