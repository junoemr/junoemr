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

import org.oscarehr.common.xml.cds.v5_0.model.CareElements;
import org.oscarehr.common.xml.cds.v5_0.model.OmdCds;
import org.oscarehr.common.xml.cds.v5_0.model.PatientRecord;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.model.measurement.Measurement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
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
		demographic.setConcernNoteList(new CDSProblemImportMapper().importAll(patientRecord.getProblemList()));
		demographic.setRiskFactorNoteList(new CDSRiskFactorImportMapper().importAll(patientRecord.getRiskFactors()));
		demographic.setAllergyList(new CDSAllergyImportMapper().importAll(patientRecord.getAllergiesAndAdverseReactions()));
		demographic.setMedicationList(new CDSMedicationImportMapper().importAll(patientRecord.getMedicationsAndTreatments()));
		demographic.setImmunizationList(new CDSImmunizationImportMapper().importAll(patientRecord.getImmunizations()));
		demographic.setLabList(new CDSLabImportMapper().importToJuno(patientRecord.getLaboratoryResults()));
		demographic.setAppointmentList(new CDSAppointmentImportMapper().importAll(patientRecord.getAppointments()));
		demographic.setEncounterNoteList(new CDSEncounterNoteImportMapper().importAll(patientRecord.getClinicalNotes()));
		demographic.setDocumentList(new CDSReportImportMapper().importAll(patientRecord.getReports()));
		demographic.setMeasurementList(getMeasurementsList(patientRecord.getCareElements()));
		demographic.setReminderNoteList(new CDSAlertImportMapper().importAll(patientRecord.getAlertsAndSpecialNeeds()));

		return demographic;
	}

	private List<Measurement> getMeasurementsList(List<CareElements> careElements)
	{
		// because we get a list back from the base converter, we need to flatten the list of lists
		List<List<Measurement>> measurementLists = new CDSCareElementImportMapper().importAll(careElements);
		return measurementLists.stream().flatMap(List::stream).collect(Collectors.toList());
	}
}
