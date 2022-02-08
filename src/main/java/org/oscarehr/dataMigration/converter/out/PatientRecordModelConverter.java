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
package org.oscarehr.dataMigration.converter.out;

import org.apache.log4j.Logger;
import org.oscarehr.allergy.dao.AllergyDao;
import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.contact.dao.DemographicContactDao;
import org.oscarehr.common.dao.DemographicPharmacyDao;
import org.oscarehr.common.dao.DxresearchDAO;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.dao.PharmacyInfoDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.contact.entity.DemographicContact;
import org.oscarehr.common.model.DemographicPharmacy;
import org.oscarehr.common.model.Dxresearch;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.common.model.PharmacyInfo;
import org.oscarehr.dataMigration.converter.out.contact.DemographicContactDbToModelConverter;
import org.oscarehr.dataMigration.converter.out.hrm.HrmDocumentDbToModelConverter;
import org.oscarehr.dataMigration.converter.out.note.ConcernNoteDbToModelConverter;
import org.oscarehr.dataMigration.converter.out.note.EncounterNoteDbToModelConverter;
import org.oscarehr.dataMigration.converter.out.note.FamilyHistoryNoteDbToModelConverter;
import org.oscarehr.dataMigration.converter.out.note.MedicalHistoryNoteDbToModelConverter;
import org.oscarehr.dataMigration.converter.out.note.ReminderNoteDbToModelConverter;
import org.oscarehr.dataMigration.converter.out.note.RiskFactorNoteDbToModelConverter;
import org.oscarehr.dataMigration.converter.out.note.SocialHistoryNoteDbToModelConverter;
import org.oscarehr.dataMigration.model.PatientRecord;
import org.oscarehr.demographic.converter.DemographicDbToModelConverter;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.document.dao.DocumentDao;
import org.oscarehr.document.model.Document;
import org.oscarehr.encounterNote.dao.CaseManagementNoteDao;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.search.CaseManagementNoteCriteriaSearch;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentDao;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.measurements.service.MeasurementsService;
import org.oscarehr.prevention.dao.PreventionDao;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.rx.dao.DrugDao;
import org.oscarehr.rx.model.Drug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.log.LogAction;

import java.time.Instant;
import java.util.List;

@Component
public class PatientRecordModelConverter extends
		BaseDbToModelConverter<Demographic, PatientRecord>
{
	private static final Logger logger = Logger.getLogger(PatientRecordModelConverter.class);

	@Autowired
	private OscarAppointmentDao appointmentDao;

	@Autowired
	private AppointmentDbToModelConverter appointmentConverter;

	@Autowired
	private AllergyDao allergyDao;

	@Autowired
	private AllergyDbToModelConverter allergyDbToModelConverter;

	@Autowired
	private CaseManagementNoteDao caseManagementNoteDao;

	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private DocumentDbToModelConverter documentDbToModelConverter;

	@Autowired
	private DemographicContactDao demographicContactDao;

	@Autowired
	private DemographicContactDbToModelConverter demographicContactDbToModelConverter;

	@Autowired
	private EncounterNoteDbToModelConverter encounterNoteConverter;

	@Autowired
	private FamilyHistoryNoteDbToModelConverter familyHistoryNoteConverter;

	@Autowired
	private SocialHistoryNoteDbToModelConverter socialHistoryNoteMapper;

	@Autowired
	private LabDbToModelConverter labDbToModelConverter;

	@Autowired
	private Hl7TextInfoDao hl7TextInfoDao;

	@Autowired
	private MeasurementsService measurementsService;

	@Autowired
	private MedicalHistoryNoteDbToModelConverter medicalHistoryNoteConverter;

	@Autowired
	private ReminderNoteDbToModelConverter reminderNoteModelConverter;

	@Autowired
	private RiskFactorNoteDbToModelConverter riskFactorNoteModelConverter;

	@Autowired
	private ConcernNoteDbToModelConverter concernNoteModelConverter;

	@Autowired
	private DxDbToModelConverter dxDbToModelConverter;

	@Autowired
	private PreventionDao preventionDao;

	@Autowired
	private PreventionDbToModelConverter preventionConverter;

	@Autowired
	private DrugDao drugDao;

	@Autowired
	private DxresearchDAO dxresearchDAO;

	@Autowired
	private MedicationDbToModelConverter medicationDbToModelConverter;

	@Autowired
	private DemographicDbToModelConverter demographicDbToModelConverter;

	@Autowired
	private PharmacyInfoDao pharmacyInfoDao;

	@Autowired
	private DemographicPharmacyDao demographicPharmacyDao;

	@Autowired
	private PharmacyDbToModelConverter pharmacyDbToModelConverter;

	@Autowired
	private HRMDocumentDao hrmDocumentDao;

	@Autowired
	private HrmDocumentDbToModelConverter hrmDocumentDbToModelConverter;

	@Override
	public PatientRecord convert(Demographic input)
	{
		if(input == null)
		{
			return null;
		}

		Instant instant = Instant.now();
		PatientRecord patientRecord = new PatientRecord();
		patientRecord.setDemographic(demographicDbToModelConverter.convert(input));
		instant = LogAction.printDuration(instant, "Patient Record Model: Load Demographic");

		List<DemographicContact> demographicContacts = demographicContactDao.findActiveByDemographicNo(input.getDemographicId());
		patientRecord.setContactList(demographicContactDbToModelConverter.convert(demographicContacts));

		//TODO how to handle lazy loading etc.?
		List<Appointment> appointments = appointmentDao.getAllByDemographicNo(input.getDemographicId());
		patientRecord.setAppointmentList(appointmentConverter.convert(appointments));
		instant = LogAction.printDuration(instant, "Patient Record Model: Load appointments");

		setNotes(input, patientRecord);
		instant = LogAction.printDuration(instant, "Patient Record Model: Load notes");

		patientRecord.setMeasurementList(measurementsService.getMeasurements(input.getDemographicId()));
		instant = LogAction.printDuration(instant, "Patient Record Model: Load measurements");

		setLabs(input, patientRecord);
		instant = LogAction.printDuration(instant, "Patient Record Model: Load labs");

		List<Document> documents = documentDao.findByDemographicId(String.valueOf(input.getDemographicId()));
		patientRecord.setDocumentList(documentDbToModelConverter.convert(documents));
		instant = LogAction.printDuration(instant, "Patient Record Model: Load documents");

		List<HRMDocument> hrmDocuments = hrmDocumentDao.findByDemographicId(input.getDemographicId());
		patientRecord.setHrmDocumentList(hrmDocumentDbToModelConverter.convert(hrmDocuments));
		instant = LogAction.printDuration(instant, "Patient Record Model: Load hrm");

		List<Allergy> allergies = allergyDao.findActiveAllergies(input.getDemographicId());
		patientRecord.setAllergyList(allergyDbToModelConverter.convert(allergies));
		instant = LogAction.printDuration(instant, "Patient Record Model: Load allergies");

		List<Prevention> preventions = preventionDao.findActiveByDemoId(input.getDemographicId());
		patientRecord.setImmunizationList(preventionConverter.convert(preventions));
		instant = LogAction.printDuration(instant, "Patient Record Model: Load preventions");

		List<Drug> drugs = drugDao.findByDemographicId(input.getDemographicId());
		patientRecord.setMedicationList(medicationDbToModelConverter.convert(drugs));
		instant = LogAction.printDuration(instant, "Patient Record Model: Load drugs");

		List<Dxresearch> dxresearchList = dxresearchDAO.getDxResearchItemsByPatient(input.getDemographicId());
		patientRecord.setDxList(dxDbToModelConverter.convert(dxresearchList));
		instant = LogAction.printDuration(instant, "Patient Record Model: Load dx");

		//TODO replace with new prescribeIT system
		List<DemographicPharmacy> demographicPharmacies = demographicPharmacyDao.findByDemographicId(input.getDemographicId());
		if(demographicPharmacies != null && !demographicPharmacies.isEmpty())
		{
			PharmacyInfo preferredPharmacyInfo = pharmacyInfoDao.find(demographicPharmacies.get(0).getPharmacyId());
			patientRecord.setPreferredPharmacy(pharmacyDbToModelConverter.convert(preferredPharmacyInfo));
			instant = LogAction.printDuration(instant, "Patient Record Model: Load pharmacy");
		}

		return patientRecord;
	}

	private void setNotes(Demographic input, PatientRecord patientRecord)
	{
		CaseManagementNoteCriteriaSearch criteriaSearch = new CaseManagementNoteCriteriaSearch();
		criteriaSearch.setDemographicId(input.getId());
		criteriaSearch.setIssueCodeNone();
		criteriaSearch.setNoLimit();

		// get encounter notes by demographic
		List<CaseManagementNote> encounterNotes = caseManagementNoteDao.criteriaSearch(criteriaSearch);
		for(CaseManagementNote note : encounterNotes)
		{
			// only include unlinked notes. Notes linked to other modules are not basic encounter notes, and will be included elsewhere
			if(note.getNoteLinkList() == null || note.getNoteLinkList().isEmpty())
			{
				patientRecord.addEncounterNote(encounterNoteConverter.convert(note));
			}
		}

		// get social history notes by demographic
		criteriaSearch.setIssueCodeSocialHistory();
		List<CaseManagementNote> socialHistoryNotes = caseManagementNoteDao.criteriaSearch(criteriaSearch);
		for(CaseManagementNote note : socialHistoryNotes)
		{
			patientRecord.addSocialHistoryNote(socialHistoryNoteMapper.convert(note));
		}

		// get family history notes by demographic
		criteriaSearch.setIssueCodeFamilyHistory();
		List<CaseManagementNote> familyHistoryNotes = caseManagementNoteDao.criteriaSearch(criteriaSearch);
		for(CaseManagementNote note : familyHistoryNotes)
		{
			patientRecord.addFamilyHistoryNote(familyHistoryNoteConverter.convert(note));
		}

		// get medical history notes by demographic
		criteriaSearch.setIssueCodeMedicalHistory();
		List<CaseManagementNote> medicalHistoryNotes = caseManagementNoteDao.criteriaSearch(criteriaSearch);
		for(CaseManagementNote note : medicalHistoryNotes)
		{
			patientRecord.addMedicalHistoryNote(medicalHistoryNoteConverter.convert(note));
		}

		// get reminder notes by demographic
		criteriaSearch.setIssueCodeReminders();
		List<CaseManagementNote> reminderNotes = caseManagementNoteDao.criteriaSearch(criteriaSearch);
		for(CaseManagementNote note : reminderNotes)
		{
			patientRecord.addReminderNote(reminderNoteModelConverter.convert(note));
		}

		// get risk factor notes by demographic
		criteriaSearch.setIssueCodeRiskFactors();
		List<CaseManagementNote> riskFactorNotes = caseManagementNoteDao.criteriaSearch(criteriaSearch);
		for(CaseManagementNote note : riskFactorNotes)
		{
			patientRecord.addRiskFactorNote(riskFactorNoteModelConverter.convert(note));
		}

		// get concerns notes by demographic
		criteriaSearch.setIssueCodeConcerns();
		List<CaseManagementNote> concernNotes = caseManagementNoteDao.criteriaSearch(criteriaSearch);
		for(CaseManagementNote note : concernNotes)
		{
			patientRecord.addConcernNote(concernNoteModelConverter.convert(note));
		}
	}

	private void setLabs(Demographic input, PatientRecord patientRecord)
	{
		//TODO this getter needs an update
		List<Object[]> infos = hl7TextInfoDao.findByDemographicId(input.getDemographicId());
		for (Object[] info : infos)
		{
			Hl7TextInfo hl7TxtInfo = (Hl7TextInfo) info[0];
			patientRecord.addLab(labDbToModelConverter.convert(hl7TxtInfo));
		}
	}
}
