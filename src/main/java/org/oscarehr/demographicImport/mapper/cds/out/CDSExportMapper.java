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
import org.oscarehr.common.xml.cds.v5_0.model.PastHealth;
import org.oscarehr.common.xml.cds.v5_0.model.PatientRecord;
import org.oscarehr.common.xml.cds.v5_0.model.PersonalHistory;
import org.oscarehr.demographicImport.model.demographic.Demographic;

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
		patientRecord.getPersonalHistory().addAll(getPersonalHistoryList(exportStructure));
		patientRecord.getFamilyHistory().addAll(getFamilyHistoryList(exportStructure));
		patientRecord.getPastHealth().addAll(getPastHealthList(exportStructure));
		patientRecord.getClinicalNotes().addAll(getClinicalNotesList(exportStructure));

		omdCds.setPatientRecord(patientRecord);
		return omdCds;
	}

	protected List<Appointments> getAppointmentList(Demographic exportStructure)
	{
		CDSAppointmentExportMapper mapper = new CDSAppointmentExportMapper();
		return mapper.exportAll(exportStructure.getAppointmentList());
	}

	protected List<PersonalHistory> getPersonalHistoryList(Demographic exportStructure)
	{
		CDSPersonalHistoryExportMapper mapper = new CDSPersonalHistoryExportMapper();
		return mapper.exportAll(exportStructure.getSocialHistoryNoteList());
	}

	protected List<FamilyHistory> getFamilyHistoryList(Demographic exportStructure)
	{
		CDSFamilyHistoryExportMapper mapper = new CDSFamilyHistoryExportMapper();
		return mapper.exportAll(exportStructure.getFamilyHistoryNoteList());
	}

	protected List<PastHealth> getPastHealthList(Demographic exportStructure)
	{
		CDSMedicalHistoryExportMapper mapper = new CDSMedicalHistoryExportMapper();
		return mapper.exportAll(exportStructure.getMedicalHistoryNoteList());
	}

	protected List<ClinicalNotes> getClinicalNotesList(Demographic exportStructure)
	{
		CDSEncounterNoteExportMapper mapper = new CDSEncounterNoteExportMapper();
		return mapper.exportAll(exportStructure.getEncounterNoteList());
	}
}
