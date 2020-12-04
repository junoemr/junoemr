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
package org.oscarehr.demographicImport.converter.out;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.allergy.dao.AllergyDao;
import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.common.dao.MeasurementDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.common.model.Measurement;
import org.oscarehr.demographic.dao.DemographicExtDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.model.DemographicCust;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.demographicImport.converter.out.note.ConcernNoteDbToModelConverter;
import org.oscarehr.demographicImport.converter.out.note.EncounterNoteDbToModelConverter;
import org.oscarehr.demographicImport.converter.out.note.FamilyHistoryNoteDbToModelConverter;
import org.oscarehr.demographicImport.converter.out.note.MedicalHistoryNoteDbToModelConverter;
import org.oscarehr.demographicImport.converter.out.note.ReminderNoteDbToModelConverter;
import org.oscarehr.demographicImport.converter.out.note.RiskFactorNoteDbToModelConverter;
import org.oscarehr.demographicImport.converter.out.note.SocialHistoryNoteDbToModelConverter;
import org.oscarehr.demographicImport.model.common.Person;
import org.oscarehr.demographicImport.model.demographic.Address;
import org.oscarehr.demographicImport.model.demographic.PhoneNumber;
import org.oscarehr.demographicImport.model.provider.Provider;
import org.oscarehr.document.dao.DocumentDao;
import org.oscarehr.document.model.Document;
import org.oscarehr.encounterNote.dao.CaseManagementNoteDao;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.search.CaseManagementNoteCriteriaSearch;
import org.oscarehr.prevention.dao.PreventionDao;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.rx.dao.DrugDao;
import org.oscarehr.rx.model.Drug;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.util.List;

import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.COUNTRY_CODE_CANADA;

@Component
public class DemographicDbToModelConverter extends
		BaseDbToModelConverter<Demographic, org.oscarehr.demographicImport.model.demographic.Demographic>
{
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
	private DemographicExtDao demographicExtDao;

	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private DocumentDbToModelConverter documentDbToModelConverter;

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
	private MeasurementDao measurementDao;

	@Autowired
	private MeasurementDbToModelConverter measurementDbToModelConverter;

	@Autowired
	private MedicalHistoryNoteDbToModelConverter medicalHistoryNoteConverter;

	@Autowired
	private ReminderNoteDbToModelConverter reminderNoteModelConverter;

	@Autowired
	private RiskFactorNoteDbToModelConverter riskFactorNoteModelConverter;

	@Autowired
	private ConcernNoteDbToModelConverter concernNoteModelConverter;

	@Autowired
	private PreventionDao preventionDao;

	@Autowired
	private PreventionDbToModelConverter preventionConverter;

	@Autowired
	private DrugDao drugDao;

	@Autowired
	private MedicationDbToModelConverter medicationDbToModelConverter;

	@Override
	public org.oscarehr.demographicImport.model.demographic.Demographic convert(Demographic input)
	{
		if(input == null)
		{
			return null;
		}

		org.oscarehr.demographicImport.model.demographic.Demographic exportDemographic = new org.oscarehr.demographicImport.model.demographic.Demographic();
		BeanUtils.copyProperties(input, exportDemographic, "address", "dateOfBirth", "title");

		exportDemographic.setId(input.getDemographicId());
		exportDemographic.setDateOfBirth(input.getDateOfBirth());
		exportDemographic.setTitle(Person.TITLE.fromStringIgnoreCase(input.getTitle()));
		exportDemographic.setSex(Person.SEX.getIgnoreCase(input.getSex()));

		exportDemographic.setHealthNumber(input.getHin());
		exportDemographic.setHealthNumberVersion(input.getVer());
		exportDemographic.setHealthNumberProvinceCode(input.getHcType());
		exportDemographic.setHealthNumberRenewDate(ConversionUtils.toNullableLocalDate(input.getHcRenewDate()));
		exportDemographic.setHealthNumberEffectiveDate(ConversionUtils.toNullableLocalDate(input.getHcEffectiveDate()));
		exportDemographic.setDateJoined(ConversionUtils.toNullableLocalDate(input.getDateJoined()));
		exportDemographic.setDateEnded(ConversionUtils.toNullableLocalDate(input.getEndDate()));
		exportDemographic.setChartNumber(input.getChartNo());
		exportDemographic.setRosterDate(ConversionUtils.toNullableLocalDate(input.getRosterDate()));
		exportDemographic.setRosterTerminationDate(ConversionUtils.toNullableLocalDate(input.getRosterTerminationDate()));
		exportDemographic.setMrpProvider(findProvider(input.getProviderNo()));
		exportDemographic.setReferralDoctor(getReferralProvider(input));
		exportDemographic.setFamilyDoctor(getFamilyProvider(input));
		exportDemographic.setPatientStatusDate(ConversionUtils.toNullableLocalDate(input.getPatientStatusDate()));

		Address address = new Address();
		address.setAddressLine1(input.getAddress());
		address.setCity(input.getCity());
		address.setRegionCode(input.getProvince());
		address.setCountryCode(COUNTRY_CODE_CANADA); //TODO do we even store this with demographics in juno
		address.setPostalCode(StringUtils.deleteWhitespace(input.getPostal()));
		address.setResidencyStatusCurrent();
		exportDemographic.addAddress(address);

		// phone conversions
		if(input.getPhone() != null)
		{
			DemographicExt homePhoneExtensionExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_DEMO_H_PHONE_EXT);
			String homePhoneExtension = (homePhoneExtensionExt != null) ? StringUtils.trimToNull(homePhoneExtensionExt.getValue()) : null;
			exportDemographic.setHomePhoneNumber(buildPhoneNumber(input.getPhone(), homePhoneExtension));
		}
		if(input.getPhone2() != null)
		{
			DemographicExt workPhoneExtensionExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_DEMO_W_PHONE_EXT);
			String workPhoneExtension = (workPhoneExtensionExt != null) ? StringUtils.trimToNull(workPhoneExtensionExt.getValue()) : null;
			exportDemographic.setWorkPhoneNumber(buildPhoneNumber(input.getPhone2(), workPhoneExtension));
		}

		DemographicExt cellNoExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_DEMO_CELL);
		String cellPhoneNumber = (cellNoExt != null) ? StringUtils.trimToNull(cellNoExt.getValue()) : null;
		if(cellPhoneNumber != null)
		{
			exportDemographic.setCellPhoneNumber(buildPhoneNumber(cellPhoneNumber, null));
		}

		DemographicCust demographicCustom = input.getDemographicCust();
		if(demographicCustom != null)
		{
			exportDemographic.setPatientNote(StringUtils.trimToNull(demographicCustom.getParsedNotes()));
			exportDemographic.setPatientAlert(StringUtils.trimToNull(demographicCustom.getAlert()));
			//TODO midwife/nurse,resident providers ?
		}

		//TODO how to handle lazy loading etc.?
		List<Appointment> appointments = appointmentDao.getAllByDemographicNo(input.getDemographicId());
		exportDemographic.setAppointmentList(appointmentConverter.convert(appointments));

		setNotes(input, exportDemographic);

		List<Measurement> measurements = measurementDao.findByDemographicId(input.getDemographicId());
		exportDemographic.setMeasurementList(measurementDbToModelConverter.convert(measurements));

		setLabs(input, exportDemographic);

		List<Document> documents = documentDao.findByDemographicId(String.valueOf(input.getDemographicId()));
		exportDemographic.setDocumentList(documentDbToModelConverter.convert(documents));

		List<Allergy> allergies = allergyDao.findActiveAllergies(input.getDemographicId());
		exportDemographic.setAllergyList(allergyDbToModelConverter.convert(allergies));

		List<Prevention> preventions = preventionDao.findActiveByDemoId(input.getDemographicId());
		exportDemographic.setImmunizationList(preventionConverter.convert(preventions));

		List<Drug> drugs = drugDao.findByDemographicId(input.getDemographicId());
		exportDemographic.setMedicationList(medicationDbToModelConverter.convert(drugs));

		return exportDemographic;
	}

	private PhoneNumber buildPhoneNumber(String phoneNumber, String extension)
	{
		boolean primaryPhone = phoneNumber.endsWith("*");
		String formattedPhoneNumber = phoneNumber.replaceAll("[^a-zA-Z0-9]", "");
		return new PhoneNumber(formattedPhoneNumber, extension, primaryPhone);
	}

	private void setNotes(Demographic input, org.oscarehr.demographicImport.model.demographic.Demographic exportDemographic)
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
				exportDemographic.addEncounterNote(encounterNoteConverter.convert(note));
			}
		}

		// get social history notes by demographic
		criteriaSearch.setIssueCodeSocialHistory();
		List<CaseManagementNote> socialHistoryNotes = caseManagementNoteDao.criteriaSearch(criteriaSearch);
		for(CaseManagementNote note : socialHistoryNotes)
		{
			exportDemographic.addSocialHistoryNote(socialHistoryNoteMapper.convert(note));
		}

		// get family history notes by demographic
		criteriaSearch.setIssueCodeFamilyHistory();
		List<CaseManagementNote> familyHistoryNotes = caseManagementNoteDao.criteriaSearch(criteriaSearch);
		for(CaseManagementNote note : familyHistoryNotes)
		{
			exportDemographic.addFamilyHistoryNote(familyHistoryNoteConverter.convert(note));
		}

		// get medical history notes by demographic
		criteriaSearch.setIssueCodeMedicalHistory();
		List<CaseManagementNote> medicalHistoryNotes = caseManagementNoteDao.criteriaSearch(criteriaSearch);
		for(CaseManagementNote note : medicalHistoryNotes)
		{
			exportDemographic.addMedicalHistoryNote(medicalHistoryNoteConverter.convert(note));
		}

		// get reminder notes by demographic
		criteriaSearch.setIssueCodeReminders();
		List<CaseManagementNote> reminderNotes = caseManagementNoteDao.criteriaSearch(criteriaSearch);
		for(CaseManagementNote note : reminderNotes)
		{
			exportDemographic.addReminderNote(reminderNoteModelConverter.convert(note));
		}

		// get risk factor notes by demographic
		criteriaSearch.setIssueCodeRiskFactors();
		List<CaseManagementNote> riskFactorNotes = caseManagementNoteDao.criteriaSearch(criteriaSearch);
		for(CaseManagementNote note : riskFactorNotes)
		{
			exportDemographic.addRiskFactorNote(riskFactorNoteModelConverter.convert(note));
		}

		// get concerns notes by demographic
		criteriaSearch.setIssueCodeConcerns();
		List<CaseManagementNote> concernNotes = caseManagementNoteDao.criteriaSearch(criteriaSearch);
		for(CaseManagementNote note : concernNotes)
		{
			exportDemographic.addConcernNote(concernNoteModelConverter.convert(note));
		}
	}

	private Provider getReferralProvider(Demographic input)
	{
		return getReferralProvider(input.getReferralDoctorName(), input.getReferralDoctorNumber());
	}

	private Provider getFamilyProvider(Demographic input)
	{
		return getReferralProvider(input.getFamilyDoctorName(), input.getFamilyDoctorNumber());
	}

	private Provider getReferralProvider(String referralProviderName, String referralProviderNumber)
	{
		Provider referralProvider = null;
		if(referralProviderName != null && referralProviderName.contains(","))
		{
			String[] nameArray = referralProviderName.split(",", 2);
			String firstName = StringUtils.trimToNull(nameArray[1]);
			String lastName = StringUtils.trimToNull(nameArray[0]);

			referralProvider = new Provider();
			referralProvider.setFirstName((firstName != null) ? firstName : "Missing");
			referralProvider.setLastName((lastName != null) ? lastName : "Missing");
			referralProvider.setOhipNumber(StringUtils.trimToNull(referralProviderNumber));
		}
		return referralProvider;
	}

	private void setLabs(Demographic input, org.oscarehr.demographicImport.model.demographic.Demographic exportDemographic)
	{
		//TODO this getter needs an update
		List<Object[]> infos = hl7TextInfoDao.findByDemographicId(input.getDemographicId());
		for (Object[] info : infos)
		{
			Hl7TextInfo hl7TxtInfo = (Hl7TextInfo) info[0];
			exportDemographic.addLab(labDbToModelConverter.convert(hl7TxtInfo));
		}
	}
}
