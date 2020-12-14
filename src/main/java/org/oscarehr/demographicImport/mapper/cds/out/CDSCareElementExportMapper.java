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

import java.math.BigDecimal;
import java.math.BigInteger;

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
		if(exportStructure instanceof SmokingStatusMeasurement)
		{
			careElements.getSmokingStatus().add(getSmokingStatus((SmokingStatusMeasurement) exportStructure));
		}
		else if(exportStructure instanceof SmokingPacksMeasurement)
		{
			careElements.getSmokingPacks().add(getSmokingPacks((SmokingPacksMeasurement) exportStructure));
		}
		else if(exportStructure instanceof WeightMeasurement)
		{
			careElements.getWeight().add(getWeight((WeightMeasurement) exportStructure));
		}
		else if(exportStructure instanceof HeightMeasurement)
		{
			careElements.getHeight().add(getHeight((HeightMeasurement) exportStructure));
		}
		else if(exportStructure instanceof WaistCircumferenceMeasurement)
		{
			careElements.getWaistCircumference().add(getWaist((WaistCircumferenceMeasurement) exportStructure));
		}
		else if(exportStructure instanceof BloodPressureMeasurement)
		{
			careElements.getBloodPressure().add(getBloodPressure((BloodPressureMeasurement) exportStructure));
		}
		else if(exportStructure instanceof DiabetesComplicationsScreeningMeasurement)
		{
			careElements.getDiabetesComplicationsScreening().add(getComplicationsScreening((DiabetesComplicationsScreeningMeasurement) exportStructure));
		}
		else if(exportStructure instanceof DiabetesMotivationalCounselingMeasurement)
		{
			careElements.getDiabetesMotivationalCounselling().add(getMotivationsCounseling((DiabetesMotivationalCounselingMeasurement) exportStructure));
		}
		else if(exportStructure instanceof DiabetesSelfManagementCollaborativeMeasurement)
		{
			careElements.getDiabetesSelfManagementCollaborative().add(getSelfManagementCollaborative((DiabetesSelfManagementCollaborativeMeasurement) exportStructure));
		}
		else if(exportStructure instanceof DiabetesSelfManagementChallengesMeasurement)
		{
			careElements.getDiabetesSelfManagementChallenges().add(getSelfManagementChallenges((DiabetesSelfManagementChallengesMeasurement) exportStructure));
		}
		else if(exportStructure instanceof DiabetesSelfManagementEducationalMeasurement)
		{
			careElements.getDiabetesEducationalSelfManagement().add(getEducationalSelfManagement((DiabetesSelfManagementEducationalMeasurement) exportStructure));
		}
		else if(exportStructure instanceof HypoglycemicEpisodesMeasurement)
		{
			careElements.getHypoglycemicEpisodes().add(getHypoglycemicEpisodes((HypoglycemicEpisodesMeasurement) exportStructure));
		}
		else if(exportStructure instanceof SelfMonitoringBloodGlucoseMeasurement)
		{
			careElements.getSelfMonitoringBloodGlucose().add(getSelfMonitoringBloodGlucose((SelfMonitoringBloodGlucoseMeasurement) exportStructure));
		}

		return careElements;
	}

	protected SmokingStatus getSmokingStatus(SmokingStatusMeasurement exportStructure)
	{
		SmokingStatus smokingStatus = objectFactory.createSmokingStatus();
		smokingStatus.setStatus(exportStructure.getMeasurementValue());
		smokingStatus.setDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getObservationDateTime()));
		return smokingStatus;
	}

	protected SmokingPacks getSmokingPacks(SmokingPacksMeasurement exportStructure)
	{
		SmokingPacks smokingPacks = objectFactory.createSmokingPacks();
		smokingPacks.setPerDay(BigDecimal.valueOf(Double.parseDouble(exportStructure.getMeasurementValue())));
		smokingPacks.setDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getObservationDateTime()));
		return smokingPacks;
	}

	protected Weight getWeight(WeightMeasurement exportStructure)
	{
		Weight weight = objectFactory.createWeight();
		weight.setWeight(exportStructure.getMeasurementValue());
		weight.setDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getObservationDateTime()));
		weight.setWeightUnit(exportStructure.getMeasurementUnit());
		return weight;
	}

	protected Height getHeight(HeightMeasurement exportStructure)
	{
		Height height = objectFactory.createHeight();
		height.setHeight(exportStructure.getMeasurementValue());
		height.setDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getObservationDateTime()));
		height.setHeightUnit(exportStructure.getMeasurementUnit());
		return height;
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

	protected DiabetesComplicationScreening getComplicationsScreening(DiabetesComplicationsScreeningMeasurement exportStructure)
	{
		DiabetesComplicationScreening complicationScreening = objectFactory.createDiabetesComplicationScreening();
		complicationScreening.setExamCode(exportStructure.getMeasurementValue());
		complicationScreening.setDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getObservationDateTime()));
		return complicationScreening;
	}

	protected DiabetesMotivationalCounselling getMotivationsCounseling(DiabetesMotivationalCounselingMeasurement exportStructure)
	{
		DiabetesMotivationalCounselling motivationalCounselling = objectFactory.createDiabetesMotivationalCounselling();
		motivationalCounselling.setCounsellingPerformed(exportStructure.getMeasurementValue());
		motivationalCounselling.setDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getObservationDateTime()));
		return motivationalCounselling;
	}

	protected DiabetesSelfManagementCollaborative getSelfManagementCollaborative(DiabetesSelfManagementCollaborativeMeasurement exportStructure)
	{
		DiabetesSelfManagementCollaborative selfManagementCollaborative = objectFactory.createDiabetesSelfManagementCollaborative();
		selfManagementCollaborative.setDocumentedGoals(exportStructure.getMeasurementValue());
		selfManagementCollaborative.setCodeValue(exportStructure.getMeasurementCode());
		selfManagementCollaborative.setDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getObservationDateTime()));
		return selfManagementCollaborative;
	}

	protected DiabetesSelfManagementChallenges getSelfManagementChallenges(DiabetesSelfManagementChallengesMeasurement exportStructure)
	{
		DiabetesSelfManagementChallenges selfManagementChallenges = objectFactory.createDiabetesSelfManagementChallenges();
		selfManagementChallenges.setChallengesIdentified(exportStructure.getMeasurementValue());
		selfManagementChallenges.setCodeValue(exportStructure.getMeasurementCode());
		selfManagementChallenges.setDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getObservationDateTime()));
		return selfManagementChallenges;
	}

	protected DiabetesEducationalSelfManagement getEducationalSelfManagement(DiabetesSelfManagementEducationalMeasurement exportStructure)
	{
		DiabetesEducationalSelfManagement educationalSelfManagement = objectFactory.createDiabetesEducationalSelfManagement();
		educationalSelfManagement.setEducationalTrainingPerformed(exportStructure.getMeasurementValue());
		educationalSelfManagement.setDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getObservationDateTime()));
		return educationalSelfManagement;
	}

	protected HypoglycemicEpisodes getHypoglycemicEpisodes(HypoglycemicEpisodesMeasurement exportStructure)
	{
		HypoglycemicEpisodes hypoglycemicEpisodes = objectFactory.createHypoglycemicEpisodes();
		hypoglycemicEpisodes.setNumOfReportedEpisodes(BigInteger.valueOf(Long.parseLong(exportStructure.getMeasurementValue())));
		hypoglycemicEpisodes.setDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getObservationDateTime()));
		return hypoglycemicEpisodes;
	}

	protected SelfMonitoringBloodGlucose getSelfMonitoringBloodGlucose(SelfMonitoringBloodGlucoseMeasurement exportStructure)
	{
		SelfMonitoringBloodGlucose selfMonitoringBloodGlucose = objectFactory.createSelfMonitoringBloodGlucose();
		selfMonitoringBloodGlucose.setSelfMonitoring(exportStructure.getMeasurementValue());
		selfMonitoringBloodGlucose.setDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getObservationDateTime()));
		return selfMonitoringBloodGlucose;
	}
}
