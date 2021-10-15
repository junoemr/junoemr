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

import org.oscarehr.dataMigration.exception.InvalidImportDataException;
import org.oscarehr.dataMigration.exception.InvalidDocumentException;
import org.oscarehr.dataMigration.model.appointment.Appointment;
import org.oscarehr.dataMigration.model.document.Document;
import org.oscarehr.dataMigration.model.measurement.Measurement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.log.LogAction;
import xml.cds.v5_0.Appointments;
import xml.cds.v5_0.CareElements;
import xml.cds.v5_0.OmdCds;
import xml.cds.v5_0.PatientRecord;
import xml.cds.v5_0.Reports;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CDSImportMapper extends AbstractCDSImportMapper<OmdCds, org.oscarehr.dataMigration.model.PatientRecord>
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
	public org.oscarehr.dataMigration.model.PatientRecord importToJuno(OmdCds importStructure) throws Exception
	{
		Instant instant = Instant.now();
		org.oscarehr.dataMigration.model.PatientRecord patientModel = new org.oscarehr.dataMigration.model.PatientRecord();

		PatientRecord patientRecord = importStructure.getPatientRecord();
		patientModel.setDemographic(cdsDemographicImportMapper.importToJuno(patientRecord.getDemographics()));
		instant = LogAction.printDuration(instant, "ImportMapper: demographics");

		patientModel.setPreferredPharmacy(cdsPharmacyImportMapper.importToJuno(patientRecord.getDemographics().getPreferredPharmacy()));
		instant = LogAction.printDuration(instant, "ImportMapper: pharmacy");

		patientModel.setContactList(cdsContactImportMapper.importAll(patientRecord.getDemographics().getContact()));
		instant = LogAction.printDuration(instant, "ImportMapper: contacts");

		patientModel.setSocialHistoryNoteList(cdsPersonalHistoryImportMapper.importAll(patientRecord.getPersonalHistory()));
		instant = LogAction.printDuration(instant, "ImportMapper: personal history");

		patientModel.setFamilyHistoryNoteList(cdsFamilyHistoryImportMapper.importAll(patientRecord.getFamilyHistory()));
		instant = LogAction.printDuration(instant, "ImportMapper: family history");

		patientModel.setMedicalHistoryNoteList(cdsPastHealthImportMapper.importAll(patientRecord.getPastHealth()));
		instant = LogAction.printDuration(instant, "ImportMapper: past health");

		patientModel.setConcernNoteList(cdsProblemImportMapper.importAll(patientRecord.getProblemList()));
		instant = LogAction.printDuration(instant, "ImportMapper: problems");

		patientModel.setRiskFactorNoteList(cdsRiskFactorImportMapper.importAll(patientRecord.getRiskFactors()));
		instant = LogAction.printDuration(instant, "ImportMapper: risk factors");

		patientModel.setAllergyList(cdsAllergyImportMapper.importAll(patientRecord.getAllergiesAndAdverseReactions()));
		instant = LogAction.printDuration(instant, "ImportMapper: allergies");

		patientModel.setMedicationList(cdsMedicationImportMapper.importAll(patientRecord.getMedicationsAndTreatments()));
		instant = LogAction.printDuration(instant, "ImportMapper: medications");

		patientModel.setImmunizationList(cdsImmunizationImportMapper.importAll(patientRecord.getImmunizations()));
		instant = LogAction.printDuration(instant, "ImportMapper: immunizations");

		patientModel.setLabList(cdsLabImportMapper.importToJuno(patientRecord.getLaboratoryResults()));
		instant = LogAction.printDuration(instant, "ImportMapper: labs");

		patientModel.setAppointmentList(getAppointments(patientRecord.getAppointments()));
		instant = LogAction.printDuration(instant, "ImportMapper: appointments");

		patientModel.setEncounterNoteList(cdsEncounterNoteImportMapper.importAll(patientRecord.getClinicalNotes()));
		instant = LogAction.printDuration(instant, "ImportMapper: clinical notes");

		patientModel.setDocumentList(getDocuments(patientRecord.getReports()));
		instant = LogAction.printDuration(instant, "ImportMapper: document reports");

		patientModel.setHrmDocumentList(cdsReportHrmImportMapper.importAll(getHrmReports(patientRecord.getReports())));
		instant = LogAction.printDuration(instant, "ImportMapper: HRM reports");

		patientModel.setMeasurementList(getMeasurementsList(patientRecord.getCareElements()));
		instant = LogAction.printDuration(instant, "ImportMapper: care elements");

		patientModel.setReminderNoteList(cdsAlertImportMapper.importAll(patientRecord.getAlertsAndSpecialNeeds()));
		instant = LogAction.printDuration(instant, "ImportMapper: alerts");

		return patientModel;
	}

	private List<Measurement> getMeasurementsList(List<CareElements> careElements) throws Exception
	{
		// because we get a list back from the base converter, we need to flatten the list of lists
		List<List<Measurement>> measurementLists = cdsCareElementImportMapper.importAll(careElements);
		return measurementLists.stream().flatMap(List::stream).collect(Collectors.toList());
	}

	private List<Appointment> getAppointments(List<Appointments> appointments) throws Exception
	{
		boolean forceSkipInvalidData = patientImportContextService.getContext().getImportPreferences().isForceSkipInvalidData();
		List<Appointment> appointmentList = new ArrayList<>(appointments.size());
		for(Appointments appointment : appointments)
		{
			try
			{
				appointmentList.add(cdsAppointmentImportMapper.importToJuno(appointment));
			}
			catch(InvalidImportDataException e)
			{
				String message = e.getMessage() == null ? "Invalid Appointment" : e.getMessage();
				if(forceSkipInvalidData)
				{
					logEvent(message + " [SKIPPED]");
				}
				else
				{
					logEvent(message);
					throw e;
				}
			}
		}
		return appointmentList;
	}

	private List<Document> getDocuments(List<Reports> reports) throws Exception
	{
		boolean skipMissingDocs = patientImportContextService.getContext().getImportPreferences().isSkipMissingDocs();
		List<Reports> documentReports = getDocumentReports(reports);
		List<Document> documentList = new ArrayList<>(documentReports.size());
		for(Reports report : documentReports)
		{
			try
			{
				documentList.add(cdsReportDocumentImportMapper.importToJuno(report));
			}
			catch(InvalidDocumentException e)
			{
				String message = e.getMessage() == null ? "Invalid Document" : e.getMessage();
				if(skipMissingDocs)
				{
					logEvent(message  +" [SKIPPED]");
				}
				else
				{
					logEvent(message);
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
		// Any of these fields are specific to HRM Reports
		return (report.getHRMResultStatus() != null ||
			report.getSendingFacilityId() != null ||
			report.getSendingFacilityReport() != null);
	}
}