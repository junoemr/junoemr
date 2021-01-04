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
import org.oscarehr.common.xml.cds.v5_0.model.Reports;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.model.measurement.Measurement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CDSImportMapper extends AbstractCDSImportMapper<OmdCds, Demographic>
{
	@Autowired
	private CDSDemographicImportMapper cdsDemographicImportMapper;
	@Autowired
	private CDSPersonalHistoryImportMapper cdsPersonalHistoryImportMapper;
	@Autowired
	private CDSFamilyHistoryImportMapper cdsFamilyHistoryImportMapper;
	@Autowired
	private CDSPastHealthImportMapper cdsPastHealthImportMapper;
	@Autowired
	private CDSProblemImportMapper cdsProblemImportMapper;
	@Autowired
	private CDSRiskFactorImportMapper cdsRiskFactorImportMapper;
	@Autowired
	private CDSAllergyImportMapper cdsAllergyImportMapper;
	@Autowired
	private CDSMedicationImportMapper cdsMedicationImportMapper;
	@Autowired
	private CDSImmunizationImportMapper cdsImmunizationImportMapper;
	@Autowired
	private CDSLabImportMapper cdsLabImportMapper;
	@Autowired
	private CDSAppointmentImportMapper cdsAppointmentImportMapper;
	@Autowired
	private CDSEncounterNoteImportMapper cdsEncounterNoteImportMapper;
	@Autowired
	private CDSReportDocumentImportMapper cdsReportDocumentImportMapper;
	@Autowired
	private CDSReportHrmImportMapper cdsReportHrmImportMapper;
	@Autowired
	private CDSCareElementImportMapper cdsCareElementImportMapper;
	@Autowired
	private CDSAlertImportMapper cdsAlertImportMapper;

	public CDSImportMapper()
	{
		super();
	}

	@Override
	public Demographic importToJuno(OmdCds importStructure)
	{
		PatientRecord patientRecord = importStructure.getPatientRecord();
		Demographic demographic = cdsDemographicImportMapper.importToJuno(patientRecord.getDemographics());

		demographic.setSocialHistoryNoteList(cdsPersonalHistoryImportMapper.importAll(patientRecord.getPersonalHistory()));
		demographic.setFamilyHistoryNoteList(cdsFamilyHistoryImportMapper.importAll(patientRecord.getFamilyHistory()));
		demographic.setMedicalHistoryNoteList(cdsPastHealthImportMapper.importAll(patientRecord.getPastHealth()));
		demographic.setConcernNoteList(cdsProblemImportMapper.importAll(patientRecord.getProblemList()));
		demographic.setRiskFactorNoteList(cdsRiskFactorImportMapper.importAll(patientRecord.getRiskFactors()));
		demographic.setAllergyList(cdsAllergyImportMapper.importAll(patientRecord.getAllergiesAndAdverseReactions()));
		demographic.setMedicationList(cdsMedicationImportMapper.importAll(patientRecord.getMedicationsAndTreatments()));
		demographic.setImmunizationList(cdsImmunizationImportMapper.importAll(patientRecord.getImmunizations()));
		demographic.setLabList(cdsLabImportMapper.importToJuno(patientRecord.getLaboratoryResults()));
		demographic.setAppointmentList(cdsAppointmentImportMapper.importAll(patientRecord.getAppointments()));
		demographic.setEncounterNoteList(cdsEncounterNoteImportMapper.importAll(patientRecord.getClinicalNotes()));
		demographic.setDocumentList(cdsReportDocumentImportMapper.importAll(getDocumentReports(patientRecord.getReports())));
		demographic.setHrmDocumentList(cdsReportHrmImportMapper.importAll(getHrmReports(patientRecord.getReports())));
		demographic.setMeasurementList(getMeasurementsList(patientRecord.getCareElements()));
		demographic.setReminderNoteList(cdsAlertImportMapper.importAll(patientRecord.getAlertsAndSpecialNeeds()));

		return demographic;
	}

	private List<Measurement> getMeasurementsList(List<CareElements> careElements)
	{
		// because we get a list back from the base converter, we need to flatten the list of lists
		List<List<Measurement>> measurementLists = cdsCareElementImportMapper.importAll(careElements);
		return measurementLists.stream().flatMap(List::stream).collect(Collectors.toList());
	}

	private List<Reports> getDocumentReports(List<Reports> reports)
	{
		// include all reports that do not contain the HRM status
		return reports.stream().filter(report -> report.getHRMResultStatus() == null).collect(Collectors.toList());
	}
	private List<Reports> getHrmReports(List<Reports> reports)
	{
		// include all reports that contain the HRM status
		return reports.stream().filter(report -> report.getHRMResultStatus() != null).collect(Collectors.toList());
	}
}
