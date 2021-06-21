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
package org.oscarehr.dataMigration.mapper.cds.out;

import org.oscarehr.dataMigration.model.lab.Lab;
import org.oscarehr.dataMigration.pref.ExportPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.log.LogAction;
import xml.cds.v5_0.LaboratoryResults;
import xml.cds.v5_0.OmdCds;
import xml.cds.v5_0.PatientRecord;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CDSExportMapper extends AbstractCDSExportMapper<OmdCds, org.oscarehr.dataMigration.model.PatientRecord>
{
	@Autowired
	private  CDSDemographicExportMapper cdsDemographicExportMapper;
	@Autowired
	private  CDSPersonalHistoryExportMapper cdsPersonalHistoryExportMapper;
	@Autowired
	private  CDSFamilyHistoryExportMapper cdsFamilyHistoryExportMapper;
	@Autowired
	private  CDSPastHealthExportMapper cdsPastHealthExportMapper;
	@Autowired
	private CDSProblemNoteExportMapper cdsProblemNoteExportMapper;
	@Autowired
	private CDSProblemDxExportMapper cdsProblemDxExportMapper;
	@Autowired
	private  CDSRiskFactorExportMapper cdsRiskFactorExportMapper;
	@Autowired
	private  CDSAllergyExportMapper cdsAllergyExportMapper;
	@Autowired
	private  CDSMedicationExportMapper cdsMedicationExportMapper;
	@Autowired
	private  CDSImmunizationExportMapper cdsImmunizationExportMapper;
	@Autowired
	private  CDSLabExportMapper cdsLabExportMapper;
	@Autowired
	private  CDSAppointmentExportMapper cdsAppointmentExportMapper;
	@Autowired
	private  CDSEncounterNoteExportMapper cdsEncounterNoteExportMapper;
	@Autowired
	private CDSReportDocumentExportMapper cdsReportDocumentExportMapper;
	@Autowired
	private CDSReportHrmExportMapper cdsReportHRMExportMapper;
	@Autowired
	private CDSReportEFormExportMapper cdsReportEFormExportMapper;
	@Autowired
	private  CDSCareElementExportMapper cdsCareElementExportMapper;
	@Autowired
	private  CDSAlertExportMapper cdsAlertExportMapper;

	public CDSExportMapper()
	{
		super();
	}

	@Override
	public OmdCds exportFromJuno(org.oscarehr.dataMigration.model.PatientRecord exportStructure) throws Exception
	{
		Instant instant = Instant.now();
		OmdCds omdCds = objectFactory.createOmdCds();
		PatientRecord patientRecord = objectFactory.createPatientRecord();
		ExportPreferences exportPreferences = patientExportContextService.getContext().getExportPreferences();

		patientRecord.setDemographics(
				cdsDemographicExportMapper.exportFromJuno(exportStructure));
		instant = LogAction.printDuration(instant, "ExportMapper: demographics");

		if(exportPreferences.isExportPersonalHistory())
		{
			patientRecord.getPersonalHistory().addAll(
					cdsPersonalHistoryExportMapper.exportAll(exportStructure.getSocialHistoryNoteList()));
			instant = LogAction.printDuration(instant, "ExportMapper: personal history");
		}
		if(exportPreferences.isExportFamilyHistory())
		{
			patientRecord.getFamilyHistory().addAll(
					cdsFamilyHistoryExportMapper.exportAll(exportStructure.getFamilyHistoryNoteList()));
			instant = LogAction.printDuration(instant, "ExportMapper: family history");
		}
		if(exportPreferences.isExportPastHealth())
		{
			patientRecord.getPastHealth().addAll(
					cdsPastHealthExportMapper.exportAll(exportStructure.getMedicalHistoryNoteList()));
			instant = LogAction.printDuration(instant, "ExportMapper: past health");
		}
		if(exportPreferences.isExportProblemList())
		{
			patientRecord.getProblemList().addAll(
					cdsProblemDxExportMapper.exportAll(exportStructure.getDxList()));
			patientRecord.getProblemList().addAll(
					cdsProblemNoteExportMapper.exportAll(exportStructure.getConcernNoteList()));
			instant = LogAction.printDuration(instant, "ExportMapper: problems");
		}
		if(exportPreferences.isExportRiskFactors())
		{
			patientRecord.getRiskFactors().addAll(
					cdsRiskFactorExportMapper.exportAll(exportStructure.getRiskFactorNoteList()));
			instant = LogAction.printDuration(instant, "ExportMapper: risk factors");
		}
		if(exportPreferences.isExportAllergiesAndAdverseReactions())
		{
			patientRecord.getAllergiesAndAdverseReactions().addAll(
					cdsAllergyExportMapper.exportAll(exportStructure.getAllergyList()));
			instant = LogAction.printDuration(instant, "ExportMapper: allergies");
		}
		if(exportPreferences.isExportMedicationsAndTreatments())
		{
			patientRecord.getMedicationsAndTreatments().addAll(
					cdsMedicationExportMapper.exportAll(exportStructure.getMedicationList()));
			instant = LogAction.printDuration(instant, "ExportMapper: medications");
		}
		if(exportPreferences.isExportImmunizations())
		{
			patientRecord.getImmunizations().addAll(
					cdsImmunizationExportMapper.exportAll(exportStructure.getImmunizationList()));
			instant = LogAction.printDuration(instant, "ExportMapper: immunizations");
		}
		if(exportPreferences.isExportLaboratoryResults())
		{
			patientRecord.getLaboratoryResults().addAll(
					getLabList(exportStructure.getLabList()));
			instant = LogAction.printDuration(instant, "ExportMapper: labs");
		}
		if(exportPreferences.isExportAppointments())
		{
			patientRecord.getAppointments().addAll(
					cdsAppointmentExportMapper.exportAll(exportStructure.getAppointmentList()));
			instant = LogAction.printDuration(instant, "ExportMapper: appointments");
		}
		if(exportPreferences.isExportClinicalNotes())
		{
			patientRecord.getClinicalNotes().addAll(
					cdsEncounterNoteExportMapper.exportAll(exportStructure.getEncounterNoteList()));
			instant = LogAction.printDuration(instant, "ExportMapper: encounter notes");
		}
		if(exportPreferences.isExportReportsReceived())
		{
			//TODO add more properties for these to export individually
			patientRecord.getReports().addAll(
					cdsReportDocumentExportMapper.exportAll(exportStructure.getDocumentList()));
			instant = LogAction.printDuration(instant, "ExportMapper: documents");
			patientRecord.getReports().addAll(
					cdsReportHRMExportMapper.exportAll(exportStructure.getHrmDocumentList()));
			instant = LogAction.printDuration(instant, "ExportMapper: hrm");
			patientRecord.getReports().addAll(
					cdsReportEFormExportMapper.exportAll(exportStructure.getEFormListList()));
			instant = LogAction.printDuration(instant, "ExportMapper: eforms");
		}
		if(exportPreferences.isExportCareElements())
		{
			patientRecord.getCareElements().addAll(
					cdsCareElementExportMapper.exportAll(exportStructure.getMeasurementList()));
			instant = LogAction.printDuration(instant, "ExportMapper: care elements (measurements)");
		}
		if(exportPreferences.isExportAlertsAndSpecialNeeds())
		{
			patientRecord.getAlertsAndSpecialNeeds().addAll(
					cdsAlertExportMapper.exportAll(exportStructure.getReminderNoteList()));
			instant = LogAction.printDuration(instant, "ExportMapper: alerts");
		}

		omdCds.setPatientRecord(patientRecord);
		return omdCds;
	}

	private List<LaboratoryResults> getLabList(List<Lab> labs) throws Exception
	{
		// because we get a list back from the base converter, we need to flatten the list of lists
		List<List<LaboratoryResults>> labsLists = cdsLabExportMapper.exportAll(labs);
		return labsLists.stream().flatMap(List::stream).collect(Collectors.toList());
	}
}
