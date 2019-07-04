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
package org.oscarehr.encounterNote.dao;

import org.oscarehr.casemgmt.dto.EncounterCPPNote;
import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@Transactional
@Repository("encounterNote.dao.CaseManagementNoteDao")
public class CaseManagementNoteDao extends AbstractDao<CaseManagementNote>
{
	public CaseManagementNoteDao()
	{
		super(CaseManagementNote.class);
	}

	public CaseManagementNote findLatestByUUID(String uuid)
	{
		// select model name must match specified @Entity name in model object
		String queryString = "SELECT x FROM model.CaseManagementNote x " +
				"WHERE x.uuid=:uuid " +
				"ORDER BY x.noteId DESC";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("uuid", uuid);
		query.setMaxResults(1);

		return this.getSingleResultOrNull(query);
	}

	public List<EncounterCPPNote> getCPPNotes(String demographicNo, long issueId)
	{
/*		String sql = "select\n" +
				"    note.note_id as noteId,\n" +
				"    note.update_date as updateDate,\n" +
				"    note.observation_date as observationDate,\n" +
				"    note.demographic_no as demographicNo,\n" +
				"    note.provider_no as providerNo,\n" +
				"    note.note,\n" +
				"    note.signed,\n" +
				"    note.include_issue_innote as includeIssueInNote,\n" +
				"    note.signing_provider_no as signingProviderNo,\n" +
				"    note.encounter_type as encounterType,\n" +
				"    note.billing_code as billingCode,\n" +
				"    note.program_no as programNo,\n" +
				"    note.reporter_caisi_role as reporterCaisiRole,\n" +
				"    note.reporter_program_team as reporterProgramTeam,\n" +
				"    note.history,\n" +
				"    note.uuid,\n" +
				"    note.password,\n" +
				"    note.locked,\n" +
				"    note.archived,\n" +
				"    note.position,\n" +
				"    note.appointmentNo,\n" +
				"    note.hourOfEncounterTime,\n" +
				"    note.minuteOfEncounterTime,\n" +
				"    note.hourOfEncTransportationTime,\n" +
				"    note.minuteOfEncTransportationTime,\n" +
				"    (select count(cmn.uuid) from casemgmt_note cmn where cmn.uuid = note.uuid) as revision,\n" +
				"    ext_startdate.date_value as extStartDate,\n" +
				"    ext_resolutiondate.date_value as extResolutionDate,\n" +
				"    ext_proceduredate.date_value as extProcedureDate,\n" +
				"    ext_ageatonset.value as extAgeAtOnset,\n" +
				"    ext_treatment.value as extTreatment,\n" +
				"    ext_problemstatus.value as extProblemStatus,\n" +
				"    ext_exposuredetail.value as extExposureDetail,\n" +
				"    ext_relationship.value as extRelationship,\n" +
				"    ext_lifestage.value as extLifeStage,\n" +
				"    ext_hidecpp.value as extHideCpp,\n" +
				"    ext_problemdescription.value as extProblemDescription\n" +
				"from casemgmt_note note\n" +
				"join casemgmt_issue_notes issue_notes\n" +
				"          on note.note_id=issue_notes.note_id\n" +
				"join casemgmt_issue issue\n" +
				"          on issue_notes.id= issue.id\n" +
				"              and issue.demographic_no = note.demographic_no\n" +
				"left join casemgmt_note_ext ext_startdate\n" +
				"          on ext_startdate.note_id = note.note_id\n" +
				"              and ext_startdate.key_val = 'Start Date'\n" +
				"left join casemgmt_note_ext ext_resolutiondate\n" +
				"          on ext_resolutiondate.note_id = note.note_id\n" +
				"              and ext_resolutiondate.key_val = 'Resolution Date'\n" +
				"left join casemgmt_note_ext ext_proceduredate\n" +
				"          on ext_proceduredate.note_id = note.note_id\n" +
				"              and ext_proceduredate.key_val = 'Procedure Date'\n" +
				"left join casemgmt_note_ext ext_ageatonset\n" +
				"          on ext_ageatonset.note_id = note.note_id\n" +
				"              and ext_ageatonset.key_val = 'Age at Onset'\n" +
				"left join casemgmt_note_ext ext_treatment\n" +
				"          on ext_treatment.note_id = note.note_id\n" +
				"              and ext_treatment.key_val = 'Treatment'\n" +
				"left join casemgmt_note_ext ext_problemstatus\n" +
				"          on ext_problemstatus.note_id = note.note_id\n" +
				"              and ext_problemstatus.key_val = 'Problem Status'\n" +
				"left join casemgmt_note_ext ext_exposuredetail\n" +
				"          on ext_exposuredetail.note_id = note.note_id\n" +
				"              and ext_exposuredetail.key_val = 'Exposure Details'\n" +
				"left join casemgmt_note_ext ext_relationship\n" +
				"          on ext_relationship.note_id = note.note_id\n" +
				"              and ext_relationship.key_val = 'Relationship'\n" +
				"left join casemgmt_note_ext ext_lifestage\n" +
				"          on ext_lifestage.note_id = note.note_id\n" +
				"              and ext_lifestage.key_val = 'Life Stage'\n" +
				"left join casemgmt_note_ext ext_hidecpp\n" +
				"          on ext_hidecpp.note_id = note.note_id\n" +
				"              and ext_hidecpp.key_val = 'Hide Cpp'\n" +
				"left join casemgmt_note_ext ext_problemdescription\n" +
				"          on ext_problemdescription.note_id = note.note_id\n" +
				"              and ext_problemdescription.key_val = 'Problem Description'\n" +
				"where (issue.issue_id = :issue_id)\n" +
				"and note.demographic_no = :demographic_no\n" +
				"and note.archived = 0\n" +
				"and note.locked = 0\n" +
				"and note.note_id=(\n" +
				"  select\n" +
				"    max(note_filter_.note_id)\n" +
				"  from casemgmt_note note_filter_\n" +
				"  where note.uuid=note_filter_.uuid\n" +
				"  group by note_filter_.uuid\n" +
				")\n" +
				"order by note.position, note.observation_date desc";*/

		String sql = "select\n" +
				"    note.note_id as noteId,\n" +
				"    note.update_date as updateDate,\n" +
				"    note.observation_date as observationDate,\n" +
				"    note.demographic_no as demographicNo,\n" +
				"    note.provider_no as providerNo,\n" +
				"    note.note,\n" +
				"    note.signed,\n" +
				"    note.include_issue_innote as includeIssueInNote,\n" +
				"    note.signing_provider_no as signingProviderNo,\n" +
				"    note.encounter_type as encounterType,\n" +
				"    note.billing_code as billingCode,\n" +
				"    note.program_no as programNo,\n" +
				"    note.reporter_caisi_role as reporterCaisiRole,\n" +
				"    note.reporter_program_team as reporterProgramTeam,\n" +
				"    note.history,\n" +
				"    note.uuid,\n" +
				"    note.password,\n" +
				"    note.locked,\n" +
				"    note.archived,\n" +
				"    note.position,\n" +
				"    note.appointmentNo,\n" +
				"    note.hourOfEncounterTime,\n" +
				"    note.minuteOfEncounterTime,\n" +
				"    note.hourOfEncTransportationTime,\n" +
				"    note.minuteOfEncTransportationTime,\n" +
				"    note_filter.editors,\n" +
				"    note_filter.revision,\n" +
				"    i.issue_id,\n" +
				"    i.code,\n" +
				"    i.description,\n" +
				"    ext_startdate.date_value as extStartDate,\n" +
				"    ext_resolutiondate.date_value as extResolutionDate,\n" +
				"    ext_proceduredate.date_value as extProcedureDate,\n" +
				"    ext_ageatonset.value as extAgeAtOnset,\n" +
				"    ext_treatment.value as extTreatment,\n" +
				"    ext_problemstatus.value as extProblemStatus,\n" +
				"    ext_exposuredetail.value as extExposureDetail,\n" +
				"    ext_relationship.value as extRelationship,\n" +
				"    ext_lifestage.value as extLifeStage,\n" +
				"    ext_hidecpp.value as extHideCpp,\n" +
				"    ext_problemdescription.value as extProblemDescription\n" +
				"from casemgmt_note note\n" +
				"join (\n" +
				"    select note.uuid,\n" +
				"           max(note.note_id) AS max_note_id,\n" +
				"           group_concat(distinct CONCAT(\n" +
				"               ifnull(p.last_name, ''), \n" +
				"               if(p.first_name is not null and p.last_name is not null, ', ', ''), \n" +
				"               ifnull(p.first_name, '')\n" +
				"           ) separator ';') as editors," +
				"           count(*) AS revision\n" +
				"    from casemgmt_note note\n" +
				"    join casemgmt_issue_notes issue_notes\n" +
				"         on note.note_id=issue_notes.note_id\n" +
				"    join casemgmt_issue issue\n" +
				"         on issue_notes.id= issue.id\n" +
				"             and issue.demographic_no = note.demographic_no\n" +
				"    join provider p\n" +
				"         on p.provider_no = note.provider_no\n" +
				"    where issue.issue_id = :issue_id \n" +
				"      and note.demographic_no = :demographic_no \n" +
				"      and note.archived = 0\n" +
				"      and note.locked = 0\n" +
				"    group by note.uuid\n" +
				") as note_filter\n" +
				"    on note_filter.max_note_id = note.note_id\n" +
				"join casemgmt_issue_notes issue_notes\n" +
				"    on note.note_id=issue_notes.note_id\n" +
				"join casemgmt_issue issue\n" +
				"    on issue_notes.id= issue.id\n" +
				"        and issue.demographic_no = note.demographic_no\n" +
				"join issue i\n" +
				"    on i.issue_id = issue.issue_id\n" +
				"left join casemgmt_note_ext ext_startdate\n" +
				"    on ext_startdate.note_id = note.note_id\n" +
				"        and ext_startdate.key_val = 'Start Date'\n" +
				"left join casemgmt_note_ext ext_resolutiondate\n" +
				"    on ext_resolutiondate.note_id = note.note_id\n" +
				"        and ext_resolutiondate.key_val = 'Resolution Date'\n" +
				"left join casemgmt_note_ext ext_proceduredate\n" +
				"    on ext_proceduredate.note_id = note.note_id\n" +
				"        and ext_proceduredate.key_val = 'Procedure Date'\n" +
				"left join casemgmt_note_ext ext_ageatonset\n" +
				"    on ext_ageatonset.note_id = note.note_id\n" +
				"        and ext_ageatonset.key_val = 'Age at Onset'\n" +
				"left join casemgmt_note_ext ext_treatment\n" +
				"    on ext_treatment.note_id = note.note_id\n" +
				"        and ext_treatment.key_val = 'Treatment'\n" +
				"left join casemgmt_note_ext ext_problemstatus\n" +
				"    on ext_problemstatus.note_id = note.note_id\n" +
				"        and ext_problemstatus.key_val = 'Problem Status'\n" +
				"left join casemgmt_note_ext ext_exposuredetail\n" +
				"    on ext_exposuredetail.note_id = note.note_id\n" +
				"        and ext_exposuredetail.key_val = 'Exposure Details'\n" +
				"left join casemgmt_note_ext ext_relationship\n" +
				"    on ext_relationship.note_id = note.note_id\n" +
				"        and ext_relationship.key_val = 'Relationship'\n" +
				"left join casemgmt_note_ext ext_lifestage\n" +
				"    on ext_lifestage.note_id = note.note_id\n" +
				"        and ext_lifestage.key_val = 'Life Stage'\n" +
				"left join casemgmt_note_ext ext_hidecpp\n" +
				"    on ext_hidecpp.note_id = note.note_id\n" +
				"        and ext_hidecpp.key_val = 'Hide Cpp'\n" +
				"left join casemgmt_note_ext ext_problemdescription\n" +
				"    on ext_problemdescription.note_id = note.note_id\n" +
				"        and ext_problemdescription.key_val = 'Problem Description'\n" +
				"where issue.issue_id = :issue_id \n" +
				"order by note.position, note.observation_date desc";


		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("issue_id", issueId);
		query.setParameter("demographic_no", demographicNo);

		List<Object[]> results = query.getResultList();

		List<EncounterCPPNote> out = new ArrayList<>();

		for(Object[] row: results)
		{
			EncounterCPPNote note = new EncounterCPPNote();

			int column = 0;
			note.setNoteId((int) row[column++]);

			LocalDateTime updateDate = null;
			if(row[column++] != null)
			{
				updateDate = ((Timestamp) row[column - 1]).toLocalDateTime();
			}
			note.setUpdateDate(updateDate);

			LocalDateTime observationDate = null;
			if(row[column++] != null)
			{
				observationDate = ((Timestamp) row[column - 1]).toLocalDateTime();
			}
			note.setObservationDate(observationDate);

			note.setDemographicNo((Integer) row[column++]);
			note.setProviderNo((String) row[column++]);
			note.setNote((String) row[column++]);
			note.setSigned((Boolean) row[column++]);
			note.setIncludeIssueInNote((Boolean) row[column++]);
			note.setSigningProviderNo((String) row[column++]);
			note.setEncounterType((String) row[column++]);
			note.setBillingCode((String) row[column++]);
			note.setProgramNo((String) row[column++]);
			note.setReporterCaisiRole((String) row[column++]);
			note.setReporterProgramTeam((String) row[column++]);
			note.setHistory((String) row[column++]);
			note.setUuid((String) row[column++]);
			note.setPassword((String) row[column++]);

			Boolean locked = null;
			if(row[column++] != null)
			{
				locked = (row[column - 1].equals('1'));
			}
			note.setLocked(locked);

			note.setArchived((Boolean) row[column++]);
			note.setPosition((Integer) row[column++]);
			note.setAppointmentNo((Integer) row[column++]);
			note.setHourOfEncounterTime((Integer) row[column++]);
			note.setMinuteOfEncounterTime((Integer) row[column++]);
			note.setHourOfEncTransportationTime((Integer) row[column++]);
			note.setMinuteOfEncTransportationTime((Integer) row[column++]);
			note.setEditors((String) row[column++]);
			note.setRevision(((BigInteger) row[column++]).intValue());
			note.setIssueId((Integer) row[column++]);
			note.setCode((String) row[column++]);
			note.setDescription((String) row[column++]);

			LocalDate extStartDate = null;
			if(row[column++] != null)
			{
				extStartDate = ((Date) row[column - 1]).toLocalDate();
			}
			note.setExtStartDate(extStartDate);

			LocalDate extResolutionDate = null;
			if(row[column++] != null)
			{
				extResolutionDate = ((Date) row[column - 1]).toLocalDate();
			}
			note.setExtResolutionDate(extResolutionDate);

			LocalDate extProcedureDate = null;
			if(row[column++] != null)
			{
				extProcedureDate = ((Date) row[column - 1]).toLocalDate();
			}
			note.setExtProcedureDate(extProcedureDate);

			note.setExtAgeAtOnset((String) row[column++]);
			note.setExtTreatment((String) row[column++]);
			note.setExtProblemStatus((String) row[column++]);
			note.setExtExposureDetail((String) row[column++]);
			note.setExtRelationship((String) row[column++]);
			note.setExtLifeStage((String) row[column++]);
			note.setExtHideCpp((String) row[column++]);
			note.setExtProblemDescription((String) row[column++]);

			out.add(note);
		}

		return out;
	}
}
