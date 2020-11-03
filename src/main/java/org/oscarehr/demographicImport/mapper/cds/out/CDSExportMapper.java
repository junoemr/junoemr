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

import org.oscarehr.common.xml.cds.v5_0.model.OmdCds;
import org.oscarehr.common.xml.cds.v5_0.model.PatientRecord;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.service.ExportPreferences;

public class CDSExportMapper extends AbstractCDSExportMapper<OmdCds, Demographic>
{
	public CDSExportMapper(ExportPreferences exportPreferences)
	{
		super(exportPreferences);
	}

	@Override
	public OmdCds exportFromJuno(Demographic exportStructure)
	{
		OmdCds omdCds = objectFactory.createOmdCds();
		PatientRecord patientRecord = objectFactory.createPatientRecord();

		patientRecord.setDemographics(
				new CDSDemographicExportMapper().exportFromJuno(exportStructure));

		if(exportPreferences.isExportPersonalHistory())
		{
			patientRecord.getPersonalHistory().addAll(
					new CDSPersonalHistoryExportMapper().exportAll(exportStructure.getSocialHistoryNoteList()));
		}
		if(exportPreferences.isExportFamilyHistory())
		{
			patientRecord.getFamilyHistory().addAll(
					new CDSFamilyHistoryExportMapper().exportAll(exportStructure.getFamilyHistoryNoteList()));
		}
		if(exportPreferences.isExportPastHealth())
		{
			patientRecord.getPastHealth().addAll(
					new CDSPastHealthExportMapper().exportAll(exportStructure.getMedicalHistoryNoteList()));
		}
		if(exportPreferences.isExportProblemList())
		{
			patientRecord.getProblemList().addAll(
					new CDSProblemExportMapper().exportAll(exportStructure.getProblemList()));
		}
		if(exportPreferences.isExportRiskFactors())
		{
			patientRecord.getRiskFactors().addAll(
					new CDSRiskFactorExportMapper().exportAll(exportStructure.getRiskFactorList()));
		}
		if(exportPreferences.isExportAllergiesAndAdverseReactions())
		{
			patientRecord.getAllergiesAndAdverseReactions().addAll(
					new CDSAllergyExportMapper().exportAll(exportStructure.getAllergyList()));
		}
		if(exportPreferences.isExportMedicationsAndTreatments())
		{
			patientRecord.getMedicationsAndTreatments().addAll(
					new CDSMedicationExportMapper().exportAll(exportStructure.getMedicationList()));
		}
		if(exportPreferences.isExportImmunizations())
		{
			patientRecord.getImmunizations().addAll(
					new CDSImmunizationExportMapper().exportAll(exportStructure.getImmunizationList()));
		}
		if(exportPreferences.isExportLaboratoryResults())
		{
			patientRecord.getLaboratoryResults().addAll(
					new CDSLabExportMapper().exportAll(exportStructure.getLabList()));
		}
		if(exportPreferences.isExportAppointments())
		{
			patientRecord.getAppointments().addAll(
					new CDSAppointmentExportMapper().exportAll(exportStructure.getAppointmentList()));
		}
		if(exportPreferences.isExportClinicalNotes())
		{
			patientRecord.getClinicalNotes().addAll(
					new CDSEncounterNoteExportMapper().exportAll(exportStructure.getEncounterNoteList()));
		}
		if(exportPreferences.isExportReportsReceived())
		{
			patientRecord.getReports().addAll(
					new CDSReportExportMapper().exportAll(exportStructure.getReportList()));
		}
		if(exportPreferences.isExportCareElements())
		{
			patientRecord.getCareElements().addAll(
					new CDSCareElementExportMapper().exportAll(exportStructure.getMeasurementList()));
		}
		if(exportPreferences.isExportAlertsAndSpecialNeeds())
		{
			patientRecord.getAlertsAndSpecialNeeds().addAll(
					new CDSAlertExportMapper().exportAll(exportStructure.getAlertList()));
		}

		omdCds.setPatientRecord(patientRecord);
		return omdCds;
	}
}
