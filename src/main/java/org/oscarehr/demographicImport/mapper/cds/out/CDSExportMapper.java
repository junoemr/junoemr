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
package org.oscarehr.demographicImport.mapper.cds.out;

import org.oscarehr.common.xml.cds.v5_0.model.Appointments;
import org.oscarehr.common.xml.cds.v5_0.model.ClinicalNotes;
import org.oscarehr.common.xml.cds.v5_0.model.FamilyHistory;
import org.oscarehr.common.xml.cds.v5_0.model.OmdCds;
import org.oscarehr.common.xml.cds.v5_0.model.PatientRecord;
import org.oscarehr.demographicImport.model.appointment.Appointment;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.model.encounterNote.EncounterNote;
import org.oscarehr.demographicImport.model.encounterNote.FamilyHistoryNote;

import java.util.ArrayList;
import java.util.List;

public class CDSExportMapper extends AbstractCDSExportMapper<OmdCds, Demographic>
{
	public CDSExportMapper()
	{
		super();
	}

	@Override
	public OmdCds exportFromJuno(Demographic exportStructure)
	{
		OmdCds omdCds = objectFactory.createOmdCds();
		PatientRecord patientRecord = objectFactory.createPatientRecord();

		patientRecord.setDemographics(new CDSDemographicExportMapper().exportFromJuno(exportStructure));
		patientRecord.getAppointments().addAll(getAppointmentList(exportStructure));
		patientRecord.getFamilyHistory().addAll(getFamilyHistoryList(exportStructure));
		patientRecord.getClinicalNotes().addAll(getClinicalNotesList(exportStructure));

		omdCds.setPatientRecord(patientRecord);
		return omdCds;
	}

	protected List<Appointments> getAppointmentList(Demographic exportStructure)
	{
		CDSAppointmentExportMapper mapper = new CDSAppointmentExportMapper();
		List<Appointments> appointments = new ArrayList<>();

		for(Appointment appointment : exportStructure.getAppointmentList())
		{
			appointments.add(mapper.exportFromJuno(appointment));
		}

		return appointments;
	}

	protected List<FamilyHistory> getFamilyHistoryList(Demographic exportStructure)
	{
		CDSFamilyHistoryExportMapper mapper = new CDSFamilyHistoryExportMapper();
		List<FamilyHistory> familyHistoryList = new ArrayList<>();

		for(FamilyHistoryNote historyNote : exportStructure.getFamilyHistoryNoteList())
		{
			familyHistoryList.add(mapper.exportFromJuno(historyNote));
		}

		return familyHistoryList;
	}

	protected List<ClinicalNotes> getClinicalNotesList(Demographic exportStructure)
	{
		CDSEncounterNoteExportMapper mapper = new CDSEncounterNoteExportMapper();
		List<ClinicalNotes> clinicalNotes = new ArrayList<>();

		for(EncounterNote note : exportStructure.getEncounterNoteList())
		{
			clinicalNotes.add(mapper.exportFromJuno(note));
		}

		return clinicalNotes;
	}
}
