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
import org.oscarehr.demographicImport.model.allergy.Allergy;
import org.oscarehr.demographicImport.model.appointment.Appointment;
import org.oscarehr.demographicImport.model.common.Address;
import org.oscarehr.demographicImport.model.common.Person;
import org.oscarehr.demographicImport.model.common.PhoneNumber;
import org.oscarehr.demographicImport.model.contact.Contact;
import org.oscarehr.demographicImport.model.contact.DemographicContact;
import org.oscarehr.demographicImport.model.document.Document;
import org.oscarehr.demographicImport.model.document.HrmDocument;
import org.oscarehr.demographicImport.model.encounterNote.ConcernNote;
import org.oscarehr.demographicImport.model.encounterNote.EncounterNote;
import org.oscarehr.demographicImport.model.encounterNote.FamilyHistoryNote;
import org.oscarehr.demographicImport.model.encounterNote.MedicalHistoryNote;
import org.oscarehr.demographicImport.model.encounterNote.ReminderNote;
import org.oscarehr.demographicImport.model.encounterNote.RiskFactorNote;
import org.oscarehr.demographicImport.model.encounterNote.SocialHistoryNote;
import org.oscarehr.demographicImport.model.form.EForm;
import org.oscarehr.demographicImport.model.immunization.Immunization;
import org.oscarehr.demographicImport.model.lab.Lab;
import org.oscarehr.demographicImport.model.measurement.Measurement;
import org.oscarehr.demographicImport.model.medication.Medication;
import org.oscarehr.demographicImport.model.provider.Provider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class Demographic extends AbstractTransientModel implements Person, Contact
{
	private Integer id;

	// base info
	private String firstName;
	private String lastName;
	private TITLE title;
	private LocalDate dateOfBirth;
	private SEX sex;
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
	private PhoneNumber homePhone;
	private PhoneNumber workPhone;
	private PhoneNumber cellPhone;

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
	private String patientNote;
	private String patientAlert;

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
	private List<HrmDocument> hrmDocumentList;
	private List<EForm> eFormListList;
	private List<Medication> medicationList;
	private List<Immunization> immunizationList;
	private List<ConcernNote> concernNoteList;
	private List<RiskFactorNote> riskFactorNoteList;
	private List<Measurement> measurementList;
	private List<DemographicContact> contactList;

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
		this.hrmDocumentList = new ArrayList<>();
		this.eFormListList = new ArrayList<>();
		this.medicationList = new ArrayList<>();
		this.immunizationList = new ArrayList<>();
		this.allergyList = new ArrayList<>();
		this.concernNoteList = new ArrayList<>();
		this.riskFactorNoteList = new ArrayList<>();
		this.measurementList = new ArrayList<>();
		this.contactList = new ArrayList<>();
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

	public void addContact(DemographicContact contact)
	{
		this.contactList.add(contact);
	}

	@Override
	public String getIdString()
	{
		return String.valueOf(getId());
	}

	@Override
	public String getTitleString()
	{
		if(this.title != null)
		{
			return this.title.name();
		}
		return null;
	}

	@Override
	public String getSexString()
	{
		if(this.sex != null)
		{
			return this.sex.getValue();
		}
		return null;
	}

	public Address getAddress()
	{
		if(this.addressList != null && !this.addressList.isEmpty())
		{
			for(Address address : addressList)
			{
				// return the first current address found
				if(address.isCurrentAddress())
				{
					return address;
				}
			}
		}
		return null;
	}

	public void setAddress(Address address)
	{
		this.addAddress(address);
	}

	@Override
	public TYPE getContactType()
	{
		return TYPE.DEMOGRAPHIC;
	}

	@Override
	public String toString()
	{
		return new ReflectionToStringBuilder(this).toString();
	}
}
