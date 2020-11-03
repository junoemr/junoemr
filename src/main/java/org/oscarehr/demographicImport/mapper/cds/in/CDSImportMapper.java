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

import org.oscarehr.common.xml.cds.v5_0.model.OmdCds;
import org.oscarehr.common.xml.cds.v5_0.model.PatientRecord;
import org.oscarehr.demographicImport.model.demographic.Demographic;

public class CDSImportMapper extends AbstractCDSImportMapper<OmdCds, Demographic>
{
	public CDSImportMapper()
	{
		super();
	}

	@Override
	public Demographic importToJuno(OmdCds importStructure)
	{
		PatientRecord patientRecord = importStructure.getPatientRecord();
		Demographic demographic = new CDSDemographicImportMapper().importToJuno(patientRecord.getDemographics());

		demographic.setSocialHistoryNoteList(new CDSPersonalHistoryImportMapper().importAll(patientRecord.getPersonalHistory()));
		demographic.setFamilyHistoryNoteList(new CDSFamilyHistoryImportMapper().importAll(patientRecord.getFamilyHistory()));
		demographic.setMedicalHistoryNoteList(new CDSPastHealthImportMapper().importAll(patientRecord.getPastHealth()));
		demographic.setProblemList(new CDSProblemImportMapper().importAll(patientRecord.getProblemList()));
		demographic.setRiskFactorList(new CDSRiskFactorImportMapper().importAll(patientRecord.getRiskFactors()));
		demographic.setAllergyList(new CDSAllergyImportMapper().importAll(patientRecord.getAllergiesAndAdverseReactions()));
		demographic.setMedicationList(new CDSMedicationImportMapper().importAll(patientRecord.getMedicationsAndTreatments()));
		demographic.setImmunizationList(new CDSImmunizationImportMapper().importAll(patientRecord.getImmunizations()));
		demographic.setLabList(new CDSLabImportMapper().importAll(patientRecord.getLaboratoryResults()));
		demographic.setAppointmentList(new CDSAppointmentImportMapper().importAll(patientRecord.getAppointments()));
		demographic.setEncounterNoteList(new CDSEncounterNoteImportMapper().importAll(patientRecord.getClinicalNotes()));
		demographic.setReportList(new CDSReportImportMapper().importAll(patientRecord.getReports()));
		demographic.setCareElementList(new CDSCareElementImportMapper().importAll(patientRecord.getCareElements()));
		demographic.setAlertList(new CDSAlertImportMapper().importAll(patientRecord.getAlertsAndSpecialNeeds()));

		return demographic;
	}
}
