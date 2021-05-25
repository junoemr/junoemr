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
package org.oscarehr.dataMigration.model;

import lombok.Data;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.oscarehr.dataMigration.model.allergy.Allergy;
import org.oscarehr.dataMigration.model.appointment.Appointment;
import org.oscarehr.dataMigration.model.contact.DemographicContact;
import org.oscarehr.dataMigration.model.demographic.Demographic;
import org.oscarehr.dataMigration.model.document.Document;
import org.oscarehr.dataMigration.model.dx.DxRecord;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.dataMigration.model.encounterNote.ConcernNote;
import org.oscarehr.dataMigration.model.encounterNote.EncounterNote;
import org.oscarehr.dataMigration.model.encounterNote.FamilyHistoryNote;
import org.oscarehr.dataMigration.model.encounterNote.MedicalHistoryNote;
import org.oscarehr.dataMigration.model.encounterNote.ReminderNote;
import org.oscarehr.dataMigration.model.encounterNote.RiskFactorNote;
import org.oscarehr.dataMigration.model.encounterNote.SocialHistoryNote;
import org.oscarehr.dataMigration.model.form.EForm;
import org.oscarehr.dataMigration.model.immunization.Immunization;
import org.oscarehr.dataMigration.model.lab.Lab;
import org.oscarehr.dataMigration.model.measurement.Measurement;
import org.oscarehr.dataMigration.model.medication.Medication;
import org.oscarehr.dataMigration.model.pharmacy.Pharmacy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
/**
 * This model represents a demographic record and all of it's associations
 */
public class PatientRecord extends AbstractTransientModel
{
	private Demographic demographic;
	private Pharmacy preferredPharmacy;
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
	private List<DxRecord> dxList;

	public PatientRecord()
	{
		appointmentList = new ArrayList<>();
		familyHistoryNoteList = new ArrayList<>();
		socialHistoryNoteList = new ArrayList<>();
		medicalHistoryNoteList = new ArrayList<>();
		encounterNoteList = new ArrayList<>();
		reminderNoteList = new ArrayList<>();
		allergyList = new ArrayList<>();
		labList = new ArrayList<>();
		documentList = new ArrayList<>();
		hrmDocumentList = new ArrayList<>();
		eFormListList = new ArrayList<>();
		medicationList = new ArrayList<>();
		immunizationList = new ArrayList<>();
		concernNoteList = new ArrayList<>();
		riskFactorNoteList = new ArrayList<>();
		measurementList = new ArrayList<>();
		contactList = new ArrayList<>();
		dxList = new ArrayList<>();
	}

	public void addAppointment(Appointment appointment)
	{
		if(this.appointmentList == null)
		{
			this.appointmentList = new ArrayList<>();
		}
		this.appointmentList.add(appointment);
	}

	public void addAppointments(Collection<Appointment> appointments)
	{
		if(this.appointmentList == null)
		{
			this.appointmentList = new ArrayList<>();
		}
		this.appointmentList.addAll(appointments);
	}

	public void addFamilyHistoryNote(FamilyHistoryNote note)
	{
		if(this.familyHistoryNoteList == null)
		{
			this.familyHistoryNoteList = new ArrayList<>();
		}
		this.familyHistoryNoteList.add(note);
	}

	public void addSocialHistoryNote(SocialHistoryNote note)
	{
		if(this.socialHistoryNoteList == null)
		{
			this.socialHistoryNoteList = new ArrayList<>();
		}
		this.socialHistoryNoteList.add(note);
	}

	public void addMedicalHistoryNote(MedicalHistoryNote note)
	{
		if(this.medicalHistoryNoteList == null)
		{
			this.medicalHistoryNoteList = new ArrayList<>();
		}
		this.medicalHistoryNoteList.add(note);
	}

	public void addEncounterNote(EncounterNote note)
	{
		if(this.encounterNoteList == null)
		{
			this.encounterNoteList = new ArrayList<>();
		}
		this.encounterNoteList.add(note);
	}

	public void addReminderNote(ReminderNote note)
	{
		if(this.reminderNoteList == null)
		{
			this.reminderNoteList = new ArrayList<>();
		}
		this.reminderNoteList.add(note);
	}

	public void addRiskFactorNote(RiskFactorNote note)
	{
		if(this.riskFactorNoteList == null)
		{
			this.riskFactorNoteList = new ArrayList<>();
		}
		this.riskFactorNoteList.add(note);
	}

	public void addConcernNote(ConcernNote note)
	{
		if(this.concernNoteList == null)
		{
			this.concernNoteList = new ArrayList<>();
		}
		this.concernNoteList.add(note);
	}

	public void addLab(Lab lab)
	{
		if(this.labList == null)
		{
			this.labList = new ArrayList<>();
		}
		this.labList.add(lab);
	}

	public void addContact(DemographicContact contact)
	{
		if(this.contactList == null)
		{
			this.contactList = new ArrayList<>();
		}
		this.contactList.add(contact);
	}

	public void addHrmDocument(HrmDocument document)
	{
		if(this.hrmDocumentList == null)
		{
			this.hrmDocumentList = new ArrayList<>();
		}
		this.hrmDocumentList.add(document);
	}

	public void addDxRecord(DxRecord dxRecord)
	{
		if(this.dxList == null)
		{
			this.dxList = new ArrayList<>();
		}
		this.dxList.add(dxRecord);
	}

	@Override
	public String toString()
	{
		return new ReflectionToStringBuilder(this).toString();
	}
}
