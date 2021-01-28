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
import org.oscarehr.demographicImport.model.document.Document;
import org.oscarehr.demographicImport.model.measurement.Measurement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CDSImportMapper extends AbstractCDSImportMapper<OmdCds, org.oscarehr.demographicImport.model.PatientRecord>
{
	@Autowired
	private CDSDemographicImportMapper cdsDemographicImportMapper;
	@Autowired
	private CDSContactImportMapper cdsContactImportMapper;
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
	@Autowired
	private CDSPharmacyImportMapper cdsPharmacyImportMapper;

	public CDSImportMapper()
	{
		super();
	}

	@Override
	public org.oscarehr.demographicImport.model.PatientRecord importToJuno(OmdCds importStructure) throws Exception
	{
		org.oscarehr.demographicImport.model.PatientRecord patientModel = new org.oscarehr.demographicImport.model.PatientRecord();

		PatientRecord patientRecord = importStructure.getPatientRecord();
		patientModel.setDemographic(cdsDemographicImportMapper.importToJuno(patientRecord.getDemographics()));
		patientModel.setPreferredPharmacy(cdsPharmacyImportMapper.importToJuno(patientRecord.getDemographics().getPreferredPharmacy()));
		patientModel.setContactList(cdsContactImportMapper.importAll(patientRecord.getDemographics().getContact()));
		patientModel.setSocialHistoryNoteList(cdsPersonalHistoryImportMapper.importAll(patientRecord.getPersonalHistory()));
		patientModel.setFamilyHistoryNoteList(cdsFamilyHistoryImportMapper.importAll(patientRecord.getFamilyHistory()));
		patientModel.setMedicalHistoryNoteList(cdsPastHealthImportMapper.importAll(patientRecord.getPastHealth()));
		patientModel.setConcernNoteList(cdsProblemImportMapper.importAll(patientRecord.getProblemList()));
		patientModel.setRiskFactorNoteList(cdsRiskFactorImportMapper.importAll(patientRecord.getRiskFactors()));
		patientModel.setAllergyList(cdsAllergyImportMapper.importAll(patientRecord.getAllergiesAndAdverseReactions()));
		patientModel.setMedicationList(cdsMedicationImportMapper.importAll(patientRecord.getMedicationsAndTreatments()));
		patientModel.setImmunizationList(cdsImmunizationImportMapper.importAll(patientRecord.getImmunizations()));
		patientModel.setLabList(cdsLabImportMapper.importToJuno(patientRecord.getLaboratoryResults()));
		patientModel.setAppointmentList(cdsAppointmentImportMapper.importAll(patientRecord.getAppointments()));
		patientModel.setEncounterNoteList(cdsEncounterNoteImportMapper.importAll(patientRecord.getClinicalNotes()));
		patientModel.setDocumentList(getDocuments(patientRecord.getReports()));
		patientModel.setHrmDocumentList(cdsReportHrmImportMapper.importAll(getHrmReports(patientRecord.getReports())));
		patientModel.setMeasurementList(getMeasurementsList(patientRecord.getCareElements()));
		patientModel.setReminderNoteList(cdsAlertImportMapper.importAll(patientRecord.getAlertsAndSpecialNeeds()));

		return patientModel;
	}

	private List<Measurement> getMeasurementsList(List<CareElements> careElements) throws Exception
	{
		// because we get a list back from the base converter, we need to flatten the list of lists
		List<List<Measurement>> measurementLists = cdsCareElementImportMapper.importAll(careElements);
		return measurementLists.stream().flatMap(List::stream).collect(Collectors.toList());
	}

	private List<Document> getDocuments(List<Reports> reports) throws Exception
	{
		List<Reports> documentReports = getDocumentReports(reports);
		List<Document> documentList = new ArrayList<>(documentReports.size());
		for(Reports report : documentReports)
		{
			try
			{
				documentList.add(cdsReportDocumentImportMapper.importToJuno(report));
			}
			catch(FileNotFoundException e)
			{
				importProperties.getImportLogger().logEvent("Missing External Document: " + report.getFilePath());
				if(!importProperties.isSkipMissingDocs())
				{
					throw e;
				}
			}
		}
		return documentList;
	}

	private List<Reports> getDocumentReports(List<Reports> reports)
	{
		// include all reports that do not contain the HRM status
		return reports.stream().filter(report -> !isHrmDocument(report)).collect(Collectors.toList());
	}
	private List<Reports> getHrmReports(List<Reports> reports)
	{
		// include all reports that contain the HRM status
		return reports.stream().filter(this::isHrmDocument).collect(Collectors.toList());
	}

	private boolean isHrmDocument(Reports report)
	{
		return (report.getHRMResultStatus() != null || !report.getOBRContent().isEmpty());
	}
}
