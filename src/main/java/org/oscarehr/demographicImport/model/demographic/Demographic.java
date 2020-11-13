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
package org.oscarehr.demographicImport.model.demographic;

import lombok.Data;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.oscarehr.demographicImport.model.AbstractTransientModel;
import org.oscarehr.demographicImport.model.Allergy;
import org.oscarehr.demographicImport.model.Immunization;
import org.oscarehr.demographicImport.model.Medication;
import org.oscarehr.demographicImport.model.encounterNote.ConcernNote;
import org.oscarehr.demographicImport.model.document.Document;
import org.oscarehr.demographicImport.model.encounterNote.RiskFactorNote;
import org.oscarehr.demographicImport.model.appointment.Appointment;
import org.oscarehr.demographicImport.model.encounterNote.EncounterNote;
import org.oscarehr.demographicImport.model.encounterNote.FamilyHistoryNote;
import org.oscarehr.demographicImport.model.encounterNote.MedicalHistoryNote;
import org.oscarehr.demographicImport.model.encounterNote.ReminderNote;
import org.oscarehr.demographicImport.model.encounterNote.SocialHistoryNote;
import org.oscarehr.demographicImport.model.lab.Lab;
import org.oscarehr.demographicImport.model.measurement.Measurement;
import org.oscarehr.demographicImport.model.provider.Provider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class Demographic extends AbstractTransientModel
{
	private Integer id;

	// base info
	private String firstName;
	private String lastName;
	private String title;
	private LocalDate dateOfBirth;
	private String sex;
	private String healthNumber;
	private String healthNumberVersion;
	private String healthNumberProvinceCode;
	private LocalDate healthNumberEffectiveDate;
	private LocalDate healthNumberRenewDate;
	private String chartNumber;
	private String sin;
	private String patientStatus;
	private LocalDate patientStatusDate;
	private LocalDate dateJoined;
	private LocalDate dateEnded;

	//contact info
	private List<Address> addressList;
	private String email;
	private PhoneNumber homePhoneNumber;
	private PhoneNumber workPhoneNumber;
	private PhoneNumber cellPhoneNumber;

	// physician info
	private Provider mrpProvider;
	private Provider referralDoctor;
	private Provider familyDoctor;

	// roster info
	private String rosterStatus;
	private LocalDate rosterDate;
	private LocalDate rosterTerminationDate;
	private String rosterTerminationReason;

	// other info
	private String lastUpdateProviderId;
	private LocalDateTime lastUpdateDateTime;

	private String alias;
	private String citizenship;
	private String spokenLanguage;
	private String officialLanguage;
	private String countryOfOrigin;
	private String newsletter;
	private String nameOfMother;
	private String nameOfFather;
	private String veteranNumber;

	// associations
	private List<Appointment> appointmentList;

	private List<FamilyHistoryNote> familyHistoryNoteList;
	private List<SocialHistoryNote> socialHistoryNoteList;
	private List<MedicalHistoryNote> medicalHistoryNoteList;
	private List<EncounterNote> encounterNoteList;

	private List<ReminderNote> reminderNoteList;
	private List<Allergy> allergyList;
	private List<Lab> labList;
	private List<Document> documentList;
	private List<Medication> medicationList;
	private List<Immunization> immunizationList;
	private List<ConcernNote> concernNoteList;
	private List<RiskFactorNote> riskFactorNoteList;
	private List<Measurement> measurementList;

	public Demographic()
	{
		this.addressList = new ArrayList<>();
		this.appointmentList = new ArrayList<>();
		this.familyHistoryNoteList = new ArrayList<>();
		this.socialHistoryNoteList = new ArrayList<>();
		this.medicalHistoryNoteList = new ArrayList<>();
		this.encounterNoteList = new ArrayList<>();
		this.reminderNoteList = new ArrayList<>();
		this.labList = new ArrayList<>();
		this.documentList = new ArrayList<>();
		this.medicationList = new ArrayList<>();
		this.immunizationList = new ArrayList<>();
		this.allergyList = new ArrayList<>();
		this.concernNoteList = new ArrayList<>();
		this.riskFactorNoteList = new ArrayList<>();
		this.measurementList = new ArrayList<>();
	}

	public void addAddress(Address address)
	{
		this.addressList.add(address);
	}

	public void addAppointment(Appointment appointment)
	{
		this.appointmentList.add(appointment);
	}

	public void addAppointments(Collection<Appointment> appointments)
	{
		this.appointmentList.addAll(appointments);
	}

	public void addFamilyHistoryNote(FamilyHistoryNote note)
	{
		this.familyHistoryNoteList.add(note);
	}

	public void addSocialHistoryNote(SocialHistoryNote note)
	{
		this.socialHistoryNoteList.add(note);
	}

	public void addMedicalHistoryNote(MedicalHistoryNote note)
	{
		this.medicalHistoryNoteList.add(note);
	}

	public void addEncounterNote(EncounterNote note)
	{
		this.encounterNoteList.add(note);
	}

	public void addReminderNote(ReminderNote note)
	{
		this.reminderNoteList.add(note);
	}

	public void addRiskFactorNote(RiskFactorNote note)
	{
		this.riskFactorNoteList.add(note);
	}

	public void addConcernNote(ConcernNote note)
	{
		this.concernNoteList.add(note);
	}

	public void addLab(Lab lab)
	{
		this.labList.add(lab);
	}

	@Override
	public String toString()
	{
		return new ReflectionToStringBuilder(this).toString();
	}
}
