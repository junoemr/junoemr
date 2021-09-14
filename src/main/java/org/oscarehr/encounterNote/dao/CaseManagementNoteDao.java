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

import org.apache.commons.lang.StringUtils;
import org.oscarehr.casemgmt.dto.EncounterCPPNote;
import org.oscarehr.casemgmt.model.CaseManagementNoteLink;
import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.common.dao.EncounterFormDao;
import org.oscarehr.common.model.EncounterForm;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.rest.to.model.NoteTo1;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
@Transactional
@Repository("encounterNote.dao.CaseManagementNoteDao")
public class CaseManagementNoteDao extends AbstractDao<CaseManagementNote>
{
	public static final String SORT_DATE_ASC = "observation_date_asc";
	public static final String SORT_DATE_DESC = "observation_date_desc";
	public static final String SORT_PROVIDER= "providerName";


	public CaseManagementNoteDao()
	{
		super(CaseManagementNote.class);
	}

	public CaseManagementNote findLatestByUUID(String uuid)
	{
		// select model name must match specified @Entity name in model object
		String queryString = 
				"SELECT x FROM model_CaseManagementNote x " +
				"WHERE x.uuid = :uuid " +
				"ORDER BY x.noteId DESC";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("uuid", uuid);
		query.setMaxResults(1);

		return this.getSingleResultOrNull(query);
	}

	public List<EncounterCPPNote> getCPPNotes(String demographicNo, String[] issueIds)
	{
		List<String> issueClauses = new ArrayList<>();
		for(int i = 0; i < issueIds.length; i++)
		{
			issueClauses.add("issue.issue_id = :issueId" + i);
		}

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
				//"    where issue.issue_id = :issue_id \n" +
				"    where (" + String.join(" or ", issueClauses) + ")" +
				"      and note.demographic_no = :demographic_no \n" +
				//"      and note.archived = 0\n" +
				//"      and note.locked = 0\n" +
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
				"where (" + String.join(" or ", issueClauses) + ")\n" +
				"    and note.archived = 0\n" +
				"    and note.locked = 0\n" +
				"order by note.position, note.observation_date desc";


		Query query = entityManager.createNativeQuery(sql);

		int count = 0;
		for(String issueId: issueIds)
		{
			query.setParameter("issueId" + count, issueId);
		}

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

	/**
	 * Gets the single note for a demographic that can be edited.
	 * @param demographicNo
	 * @return The note that can be edited.  Returns null if there isn't one.
	 */
	public CaseManagementNote getLatestUnsignedNote(int demographicNo, int providerNo)
	{
		//language=MariaDB
		String sql = "SELECT cmn_outer.note_id\n" +
			"FROM casemgmt_note AS cmn_outer\n" +

			// Remove CPP notes
			"JOIN " +
			"(\n" +
			"SELECT cmn.note_id\n" +
			"FROM casemgmt_note cmn\n" +
			"LEFT JOIN casemgmt_note AS cmn_filter \n" +
			"  ON cmn_filter.uuid = cmn.uuid\n" +
			"AND (cmn.update_date < cmn_filter.update_date " +
			"OR (cmn.update_date = cmn_filter.update_date AND cmn.note_id < cmn_filter.note_id))\n" +
			"LEFT JOIN casemgmt_issue_notes cmin ON cmin.note_id = cmn.note_id\n" +
			"LEFT JOIN casemgmt_issue cmi ON cmin.id = cmi.id\n" +
			"LEFT JOIN issue i\n" +
			"  ON i.issue_id = cmi.issue_id\n" +
			"  AND i.code IN ('OMeds', 'SocHistory', 'MedHistory', 'Concerns', 'FamHistory', 'Reminders', 'RiskFactors','OcularMedication')\n" +
			"LEFT JOIN casemgmt_note_link cnl ON cnl.note_id = cmn.note_id\n" +
			"WHERE " +
			"cmn_filter.note_id IS NULL\n" +
			"AND i.issue_id IS NULL\n" +
			"AND cnl.id IS NULL\n" +
			"AND cmn.demographic_no = :demographicNo\n" +
			"AND cmn.provider_no = :providerNo\n" +
			") AS latest_notes\n" +
			"ON cmn_outer.note_id = latest_notes.note_id\n" +

			// Include notes that have a tmpsave
			"LEFT JOIN casemgmt_tmpsave ct\n" +
			"  ON ct.note_id = cmn_outer.note_id\n" +
			"  AND ct.provider_no = cmn_outer.provider_no\n" +
			"  AND ct.program_id = cmn_outer.program_no\n" +

			// Get latest note that either is unsigned or has a tmpSave
			"WHERE (NOT cmn_outer.signed OR ct.id IS NOT NULL)\n" +
			"ORDER BY cmn_outer.update_date DESC\n";

		Query query = entityManager.createNativeQuery(sql);

		query.setParameter("demographicNo", demographicNo);
		query.setParameter("providerNo", providerNo);

		query.setMaxResults(1);

		Integer note_id;
		try
		{
			note_id = (Integer)query.getSingleResult();
		}
		catch(NoResultException e)
		{
			// No unsigned notes, return nothing
			return null;
		}

		return this.find(note_id.longValue());
	}

	/**
	 * Get the revision of the specified note
	 * @param uuid
	 * @return The revision
	 */
	public Integer getRevision(String uuid)
	{
		String sql = "SELECT COUNT(cmn.uuid)\n" +
				"FROM casemgmt_note cmn\n" +
				"WHERE cmn.uuid = :uuid";

		Query query = entityManager.createNativeQuery(sql);

		query.setParameter("uuid", uuid);

		return ((BigInteger)query.getSingleResult()).intValue();
	}

	/**
	 * Get a list of providers who have edited the specified note.
	 * @param uuid
	 * @return A list of providers
	 */
	public List<ProviderData> getEditors(String uuid)
	{
		String jpql = "SELECT distinct p\n" +
				"FROM ProviderData p, model_CaseManagementNote cmn\n" +
				"WHERE p.id = cmn.provider.id\n" +
				"AND cmn.uuid = :uuid";

		Query query = entityManager.createQuery(jpql);
		query.setParameter("uuid", uuid);

		return query.getResultList();
	}

	/**
	 * Get a list of the names of providers who edited the specified note.
	 * @param uuid
	 * @return A list of provider full names.
	 */
	public List<String> getEditorNames(String uuid)
	{
		List<ProviderData> editors = getEditors(uuid);

		ArrayList<String> editorNames = new ArrayList<String>();

		for (ProviderData editor: editors)
		{
			editorNames.add(editor.getDisplayName());
		}

		return (editorNames);
	}

	/**
	 * Gets a list of encounter notes to display on the encounter page.  Gets Case Management Notes,
	 * eforms and forms.
	 * @param demographicNo
	 * @param limit
	 * @param offset
	 * @return A list of encounter notes.
	 */
	public List<NoteTo1> searchEncounterNotes(
			int demographicNo,
			List<String> providers,
			List<String> roles,
			List<String> issues,
			String sortType,
			Integer limit,
			Integer offset
	)
	{
		List<NoteTo1> noteList = new ArrayList<>();

		String providerFilterCmn = "";
		String providerFilterCmnFilter = "";
		if(providers != null && providers.size() > 0)
		{
			List<String> parameterList = new ArrayList<>();
			for(int i = 0; i < providers.size(); i++)
			{
				parameterList.add(":providerNo" + i);
			}

			providerFilterCmn = " AND cmn.provider_no IN (" + String.join(",", parameterList) + ") ";
			providerFilterCmnFilter = " AND cmn_filter.provider_no IN (" + String.join(",", parameterList) + ") ";
		}

		String roleFilter = "";
		if(roles != null && roles.size() > 0)
		{
			List<String> parameterList = new ArrayList<>();
			for(String roleNo: roles)
			{
				parameterList.add(":roleNo" + roleNo);
			}

			roleFilter = " AND role.role_no IN(" + String.join(",", parameterList) + ") ";
		}

		String issueFilter = "";
		if(issues != null && issues.size() > 0)
		{
			List<String> parameterList = new ArrayList<>();
			for(String issueId: issues)
			{
				parameterList.add(":issueId" + issueId);
			}

			issueFilter = " JOIN (" +
					"  SELECT " +
					"    note.note_id, " +
					"    GROUP_CONCAT(i.description SEPARATOR 0x1D) AS issue_description" +
					"  FROM casemgmt_note note\n" +
					"  JOIN casemgmt_issue_notes cinotes on note.note_id = cinotes.note_id\n" +
					"  JOIN casemgmt_issue ci on cinotes.id = ci.id\n" +
					"  JOIN issue i ON ci.issue_id = i.issue_id\n" +
					"  WHERE note.demographic_no = :demographicNo\n" +
					"  AND i.issue_id IN (" + String.join(",", parameterList) + ") " +
					"  GROUP BY note.note_id\n" +
					") AS issue_filter ON issue_filter.note_id = cmn.note_id\n";
		}

		String sql = "SELECT * FROM ((\n" +
				"SELECT " +
				"    cmn.note_id,\n" +
				"    cmn.observation_date AS observation_date,\n" +
				"    cmn.provider_no AS provider_no,\n" +
				"    prog.name AS program_name,\n" +
				"    cmn.uuid AS uuid,\n" +
				"    cmn.update_date AS update_date,\n" +
				"    doc.document_no,\n" +
				"    doc.docfilename AS document_filename,\n" +
				"    CAST(doc.status AS VARCHAR(255)) as document_status,\n" +
				"    cmn.archived,\n" +
				"    cmn.signed,\n" +
				"    NOT cmn.signed OR NOT cmn.locked AS editable,\n" +
				"    CAST(cmn_revision.count AS CHAR) AS revision,\n" +
				"    CASE\n" +
				"        WHEN prov.last_name IS NOT NULL AND prov.first_name IS NOT NULL\n" +
				"            THEN " +
				"CONCAT(prov.last_name, ', ', prov.first_name)\n" +
				"        WHEN prov.last_name IS NOT NULL THEN prov.last_name\n" +
				"        WHEN prov.first_name IS NOT NULL THEN prov.first_name\n" +
				"        ELSE null\n" +
				"        END AS provider_name,\n" +
				"    CASE\n" +
				"        WHEN cmn.password IS NOT NULL AND LENGTH(cmn.password) > 0\n" +
				"            THEN IF(cmn.locked, 'Locked', 'Unlocked')\n" +
				"        WHEN cmn.signed THEN 'Signed'\n" +
				"        ELSE 'Unsigned'\n" +
				"        END AS status,\n" +
				"    'local' AS location,\n" +
				"    role.role_name,\n" +
				"    cmn.history LIKE '----------------History Record----------------' AS has_history,\n" +
				"    cmn.locked,\n" +
				"    cmn.note,\n" +
				"    doc.document_no IS NOT NULL AS is_document,\n" +
				"    doc.status = 'D' AS deleted,\n" +
				"    cmn_link.table_name = '2' AS rx_annotation,\n" +
				"    drugs.regional_identifier AS regional_identifier,\n" +
				"    drugs.customName AS custom_name,\n" +
				"    false AS eform_data,\n" +
				"    false AS is_encounter_form,\n" +
				"    false AS is_invoice,\n" +
				"    tickler_note.is_tickler_note AS is_tickler_note,\n" +
				"    cmn.encounter_type,\n" +
				"    cmn_revision.editors_string,\n" +
				"    cpp_note.issue_descriptions AS issue_descriptions,\n" +
				"    false AS readonly ,\n" +
				"    false AS is_group_note,\n" +
				"    cpp_note.is_cpp_note,\n" +
				"    CONCAT(\n" +
				"            COALESCE(cmn.hourOfEncounterTime, ''),\n" +
				"            IF(cmn.hourOfEncounterTime != null, ':', ''),\n" +
				"            COALESCE(cmn.minuteOfEncounterTime)\n" +
				"        ) AS encounter_time,\n" +
				"    CONCAT(\n" +
				"            COALESCE(cmn.hourOfEncTransportationTime, ''),\n" +
				"   " +
				"         IF(cmn.hourOfEncTransportationTime != null, ':', ''),\n" +
				"            COAL" +
				"ESCE(cmn.minuteOfEncTransportationTime" +
				")\n" +
				"        " +
				") AS " +
				"encounter_transportation_time\n" +

				"FROM casemgmt_note AS cmn" +
				"\n" +
				"LEFT JOIN " +
				"casemgmt_note AS cmn_filter ON cmn_filter.uuid = cmn.uuid " +
				providerFilterCmnFilter +
				"  " +
				"  AND (cmn.update_date < cmn_filter.update_date " +
				"OR (cmn.update_date = cmn_filter.update_date AND cmn.note_id < cmn_filter.note_id))\n" +
				"JOIN (\n" +
				"    SELECT uuid, count(*) as count, GROUP_CONCAT(DISTINCT\n" +
				"            CASE\n" +
				"     " +
				"           WHEN prov.last_name IS NOT NULL AND prov.first_name IS NOT NULL\n" +
				"                    THEN CONCAT(prov.last_name, ', ', prov.first_name)\n" +
				"                WHEN prov.last_name IS NOT NULL THEN prov.last_name\n" +
				"                WHEN prov.first_name IS NOT NULL THEN prov.first_name\n" +
				"                ELSE null\n" +
				"            END,\n" +
				"            ';'\n" +
				"        ) AS editors_string\n" +
				"    FROM casemgmt_note note\n" +
				"    JOIN provider prov ON note.provider_no = prov.provider_no\n" +
				"    WHERE demographic_no = :demographicNo\n" +
				"    GROUP BY uuid\n" +
				"    " +
				") AS cmn_revision ON cmn.uuid = cmn_revision.uuid\n" +
				"LEFT JOIN casemgmt_note_link AS cmn_link ON cmn.note_id = cmn_link.note_id\n" +
				"LEFT JOIN casemgmt_note_link AS cmn_link_filter\n" +
				"  ON cmn_link.note_id = cmn_link_filter.note_id\n" +
				"  AND cmn_link.id < cmn_link_filter.id\n" +
				"LEFT JOIN document doc ON cmn_link.table_name = :documentTableName AND " +
				"doc.document_no = cmn_link.table_id\n" +
				"LEFT JOIN drugs drugs ON cmn_link.table_name = :drugsTableName AND " +
				"drugs.drugid = cmn_link.table_id\n" +
				"LEFT JOIN program AS prog ON cmn.program_no = prog.id\n" +
				"LEFT JOIN provider AS prov ON cmn.provider_no = prov.provider_no\n" +
				"LEFT JOIN secRole " +
				"AS role ON cmn.reporter_caisi_role = role.role_no\n" +
				"LEFT JOIN (\n" +
				"SELECT note.note_id, true as is_tickler_note\n" +
				"  FROM casemgmt_note note\n" +
				"           JOIN casemgmt_issue_notes cinotes on note.note_id = cinotes.note_id\n" +
				"           JOIN casemgmt_issue ci on cinotes.id = ci.id\n" +
				"           JOIN issue i ON ci.issue_id = i.issue_id\n" +
				"  WHERE note.demographic_no = :demographicNo\n" +
				"    AND i.code = 'TicklerNote'\n" +
				"  GROUP BY note.note_id\n" +
				") AS tickler_note ON tickler_note.note_id = cmn.note_id\n" +
				"LEFT JOIN (\n" +
				"  SELECT " +
				"    note.note_id, \n" +
				"    SUM(i.code IN ('OMeds', 'SocHistory', 'MedHistory', 'Concerns', 'FamHistory', 'Reminders', 'RiskFactors','OcularMedication','TicklerNote')) > 0 as is_cpp_note, \n" +
				// This uses a non-character separator because it is is going to be separated
				// below.  This is not ideal and is done for performance.
				"    GROUP_CONCAT(i.description SEPARATOR 0x1D) AS issue_descriptions\n" +
				"  FROM casemgmt_note note\n" +
				"           JOIN casemgmt_issue_notes cinotes on note.note_id = cinotes.note_id\n" +
				"           JOIN casemgmt_issue ci on cinotes.id = ci.id\n" +
				"           JOIN issue i ON ci.issue_id = i.issue_id\n" +
				"  WHERE note.demographic_no = :demographicNo\n" +
				"    GROUP BY note.note_id\n" +
				") AS cpp_note ON cpp_note.note_id = cmn.note_id\n" +
				issueFilter +
				"WHERE cmn.demographic_no = :demographicNo \n" +
				"AND cmn_filter.note_id IS NULL\n" +
				"AND cmn_link_filter.id IS NULL\n" +
				providerFilterCmn +
				roleFilter;

		if(
			(providers == null || providers.size() == 0) &&
			(roles == null || roles.size() == 0) &&
			(issues == null || issues.size() == 0)
		)
		{
			sql += ") UNION ALL (" +

					"SELECT " +
					"    ed.fdid AS note_id,\n" +
					"    CAST(CONCAT(ed.form_date, ' ', ed.form_time) AS DATETIME) AS observation_date,\n" +
					"    ed.form_provider AS provider_no,\n" +
					"    '' AS program_name,\n" +
					"    '' AS uuid,\n" +
					"    CAST(CONCAT(ed.form_date, ' ', ed.form_time) AS DATETIME) AS update_date,\n" +
					"    0 AS document_no,\n" +
					"    '' AS document_filename,\n" +
					"    CAST('' AS VARCHAR(255)) AS document_status,\n" +
					"    false AS archived,\n" +
					"    false AS signed,\n" +
					"    false AS editable,\n" +
					"    '' AS revision,\n" +
					"    '' AS provider_name,\n" +
					"    '' AS status,\n" +
					"    '' AS location,\n" +
					"    '' AS role_name,\n" +
					"    false AS has_history,\n" +
					"    CAST('0' AS CHARACTER) AS locked,\n" +
					"    CONCAT(ed.form_name, ' : ', ed.subject) AS note,\n" +
					"    false AS is_document,\n" +
					"    false AS deleted,\n" +
					"    false AS rx_annotation,\n" +
					"    false AS regional_identifier,\n" +
					"    false AS custom_name,\n" +
					"    true AS eform_data,\n" +
					"    false AS is_encounter_form,\n" +
					"    false AS is_invoice,\n" +
					"    false AS is_tickler_note,\n" +
					"    '' AS encounter_type,\n" +
					"    '' AS editors_string,\n" +
					"    '' AS issue_descriptions,\n" +
					"    false AS readonly ,\n" +
					"    false AS is_group_note,\n" +
					"    false AS is_cpp_note,\n" +
					"    '' AS encounter_time,\n" +
					"    '' AS encounter_transportation_time\n" +
					"FROM eform_data ed\n" +
					"LEFT JOIN eform_instance ei\n" +
					"    ON ed.eform_instance_id = ei.id\n" +
					"WHERE ed.demographic_no = :demographicNo\n" +
					"AND ed.patient_independent = false\n" +
					"AND ((ei.id IS NULL AND ed.status) OR NOT ei.deleted)\n" +
			") UNION ALL (" +
				buildFormQuery();
		}
		sql += ")) AS full_query\n";


		if(SORT_PROVIDER.equals(sortType))
		{
			sql += "ORDER BY full_query.editors_string DESC, full_query.observation_date DESC\n";
		}
		else if(SORT_DATE_DESC.equals(sortType))
		{
			sql += "ORDER BY full_query.observation_date ASC\n";
		}
		else
		{
			sql += "ORDER BY full_query.observation_date DESC\n";
		}


		Query query = entityManager.createNativeQuery(sql);

		int count = 0;

		query.setParameter("documentTableName", CaseManagementNoteLink.DOCUMENT);
		query.setParameter("drugsTableName", CaseManagementNoteLink.DRUGS);
		query.setParameter("demographicNo", demographicNo);

		if(providers != null && providers.size() > 0)
		{
			for(int i = 0; i < providers.size(); i++)
			{
				query.setParameter("providerNo" + i, providers.get(i));
			}
		}

		if(roles != null && roles.size() > 0)
		{
			for(String roleNo: roles)
			{
				query.setParameter("roleNo" + roleNo, roleNo);
			}
		}

		if(issues != null && issues.size() > 0)
		{
			for(String issueId: issues)
			{
				query.setParameter("issueId" + issueId, issueId);
			}
		}

		if(limit != null)
		{
			query.setMaxResults(limit);
		}

		if(offset != null)
		{
			query.setFirstResult(offset);
		}



		List<Object[]> results = query.getResultList();


		for(Object[] row: results)
		{
			NoteTo1 note = new NoteTo1();

			int column = 0;
			note.setNoteId(getInteger(row[column++]));
			note.setObservationDate(getDateFromSql(row[column++]));
			note.setProviderNo((String) row[column++]);
			note.setProgramName((String) row[column++]);
			note.setUuid((String) row[column++]);
			note.setUpdateDate(getDateFromSql(row[column++]));
			note.setDocumentId(getInteger(row[column++]));
			note.setDocumentFilename((String) row[column++]);
			note.setDocumentStatus((String) row[column++]);
			note.setArchived(getBooleanFromInteger(row[column++]));
			note.setIsSigned(getBooleanFromInteger(row[column++]));
			note.setIsEditable(getBooleanFromInteger(row[column++]));
			note.setRevision((String) row[column++]);
			note.setProviderName((String) row[column++]);
			note.setStatus((String) row[column++]);
			note.setLocation((String) row[column++]);
			note.setRoleName((String) row[column++]);
			//note.setRemoteFacilityId((int) row[column++]);
			note.setHasHistory(getBooleanFromInteger(row[column++]));
			note.setLocked(getBooleanFromCharacter(row[column++]));
			note.setNote((String) row[column++]);
			note.setDocument(getBooleanFromInteger(row[column++]));
			note.setDeleted(getBooleanFromInteger(row[column++]));
			note.setRxAnnotation(getBooleanFromInteger(row[column++]));
			note.setRegionalIdentifier((String) row[column++]);
			note.setCustomName((String) row[column++]);
			note.setEformData(getBooleanFromInteger(row[column++]));
			if(note.isEformData())
			{
				//TODO why is the note id fake and also the eform id???
				note.setEformDataId(note.getNoteId());
				note.setNoteId(null);
			}
			note.setEncounterForm(getBooleanFromInteger(row[column++]));
			if(note.isEncounterForm())
			{
				note.setEncounterFormId(note.getNoteId());
			}
			note.setInvoice(getBooleanFromInteger(row[column++]));
			note.setTicklerNote(getBooleanFromInteger(row[column++]));
			note.setEncounterType((String) row[column++]);
			note.setEditorNames(new ArrayList<String>(
					Arrays.asList(((String) row[column++]).split(";"))
				)
			);

/*
			ArrayList<String> issueDescriptions = new ArrayList<>();
			issueDescriptions.add((String) row[column++]);
*/

			note.setIssueDescriptions(parseIssueDescriptions((String) row[column++]));
			note.setReadOnly(getBooleanFromInteger(row[column++]));
			note.setGroupNote(getBooleanFromInteger(row[column++]));
			note.setCpp(getBooleanFromInteger(row[column++]));
			note.setEncounterTime((String) row[column++]);
			note.setEncounterTransportationTime((String) row[column++]);

			noteList.add(note);
		}

		return noteList;
	}

	private String buildFormQuery()
	{

		EncounterFormDao encounterFormDao = (EncounterFormDao) SpringUtils.getBean("encounterFormDao");
		List<EncounterForm> encounterForms = encounterFormDao.findAll();
		Collections.sort(encounterForms, EncounterForm.BC_FIRST_COMPARATOR);

		// grab patient forms for all the above form types grouped by date of edit
		List<String> formQueryArray = new ArrayList<>();
		for (EncounterForm encounterForm : encounterForms) {
			String table = StringUtils.trimToNull(encounterForm.getFormTable());
			if (table != null)
			{
				if (!table.equals("form"))
				{
					String formSql = "SELECT " +
							"    MAX(ed.ID) AS note_id,\n" +
							"    COALESCE(MAX(ed.formEdited), MAX(ed.formCreated)) AS observation_date,\n" +
							"    COALESCE(MAX(ed.formEdited), MAX(ed.formCreated)) AS update_date,\n" +
							"    '" + encounterForm.getFormName() + "' AS note\n" +
							"FROM " + table + " AS ed " +
							"WHERE demographic_no = :demographicNo " +
							"GROUP BY DATE(ed.formEdited)";

					formQueryArray.add(formSql);
				}
				else
				{
					String formSql = "SELECT " +
							"    form_no AS note_id,\n" +
							"    ed.form_date AS observation_date,\n" +
							"    ed.form_date AS update_date,\n" +
							"    '" + encounterForm.getFormName() + "' AS note\n" +
							"from " + table + " where demographic_no = :demographicNo ";

					formQueryArray.add(formSql);
				}
			}
		}

		String sql = "SELECT " +
				"    full_form_query.note_id,\n" +
				"    full_form_query.observation_date,\n" +
				"    '' AS provider_no,\n" +
				"    '' AS program_name,\n" +
				//"  cmn.reporter_caisi_role as reporter_caisi_role,\n" +
				"    '' AS uuid,\n" +
				"    full_form_query.update_date,\n" +
				"    0 AS document_no,\n" +
				"    '' AS document_filename,\n" +
				"    '' AS document_status,\n" +
				//"    CAST(0 AS INTEGER) AS eform_data_id,\n" +
				"    false AS archived,\n" +
				"    false AS signed,\n" +
				"    false AS editable,\n" +
				"    '' AS revision,\n" +
				"    '' AS provider_name,\n" +
				"    '' AS status,\n" +
				"    '' AS location,\n" +
				"    '' AS role_name,\n" +
				//"    0 AS remote_facility_id,\n" +
				"    false AS has_history,\n" +
				"    CAST('0' AS CHARACTER) AS locked,\n" +
				"    full_form_query.note,\n" +
				"    false AS is_document,\n" +
				"    false AS deleted,\n" +
				"    false AS rx_annotation,\n" +
				"    false AS regional_identifier,\n" +
				"    false AS custom_name,\n" +
				"    false AS eform_data,\n" +
				"    true AS is_encounter_form,\n" +
				"    false AS is_invoice,\n" +
				"    false AS is_tickler_note,\n" +
				"    '' AS encounter_type,\n" +
				"    '' AS editors_string,\n" +
				"    '' AS issue_descriptions,\n" +
				"    false AS readonly ,\n" +
				"    false AS is_group_note,\n" +
				"    false AS is_cpp_note,\n" +
				"    '' AS encounter_time,\n" +
				"    '' AS encounter_transportation_time\n" +
				"FROM ((";

		sql += String.join(") UNION ALL (", formQueryArray);

		sql += ")) AS full_form_query ";

		return sql;
	}

	private ArrayList<String> parseIssueDescriptions(String issueDescriptionString)
	{
		if(issueDescriptionString == null)
		{
			return null;
		}

		// Split on non-character value as described in query above
		String[] issueDescriptions = issueDescriptionString.split("\\x1d");

		return new ArrayList<>(Arrays.asList(issueDescriptions));
	}

	private java.util.Date getDateFromSql(Object value)
	{
		if(value == null)
		{
			return null;
		}

		return Date.from(((Timestamp) value).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant());
	}

	private Integer getInteger(Object value)
	{
		if(value == null)
		{
			return null;
		}

		if(value instanceof Integer)
		{
			return ((Integer) value);
		}
		else if(value instanceof BigInteger)
		{
			return ((BigInteger) value).intValue();
		}

		throw new RuntimeException("Object is not a BigInteger or an Integer");
	}

	private boolean getBooleanFromInteger(Object value)
	{
		if(value == null)
		{
			return false;
		}

		if(value instanceof Boolean)
		{
			return ((Boolean) value).booleanValue();
		}
		else if(value instanceof Integer)
		{
			return (!((Integer) value).equals(0));
		}
		else if(value instanceof BigInteger)
		{
			return (!((BigInteger) value).equals(BigInteger.ZERO));
		}

		throw new RuntimeException("Object is not a BigInteger or an Integer");
	}

	private boolean getBooleanFromCharacter(Object value)
	{
		if(value == null)
		{
			return false;
		}

		if(value instanceof Character)
		{
			return (!((Character) value).equals('0'));
		}
		else if(value instanceof String)
		{
			return (!((String) value).equals("0"));
		}

		throw new RuntimeException("Object is not a Character or a String");
	}

	public List<CaseManagementNote> findByDemographicAndIssue(Integer demographicNo, Long issueId)
	{
		String queryString =
				"SELECT cm FROM model_CaseManagementNote cm " +
				"WHERE cm.demographic.demographicId=:demographicNo " +
				"AND :issueId = ANY (" +
					"SELECT cin.id.caseManagementIssue.issue.issueId " +
					"FROM cm.issueNoteList cin" +
				")";

		Query query = entityManager.createQuery(queryString);
		query.setParameter("demographicNo", demographicNo);
		query.setParameter("issueId", issueId);
		return query.getResultList();
	}

	/**
	* Return the demographic's notes. For each note, return only the latest, non-archived revision.
	 * @param demographicNo the demographic number of the patient in question
	 * @param isCPPNote <code>true</code>if you are trying to get cpp notes,
	 *                  <code>false</code> otherwise.
	 * @return <code>List<CaseManagementNote></code> if there are 1 or more existing notes;
	 * 		   <code>null</code> otherwise.
	 */
	public List<CaseManagementNote> findLatestRevisionOfAllNotes(Integer demographicNo, boolean isCPPNote)
	{
		/* Grabs every column from the casemgmt_note table by joining the following:
		  --casemgmt_note_filter: grabs the most-recently updated version of the note
		  --cpp_note: determines if the note is a cpp note */
		String queryString =
			"SELECT cm.note_id, cm.update_date, cm.observation_date, cm.demographic_no, cm.provider_no, cm.note,\n" +
					"cm.signed, cm.include_issue_innote, cm.signing_provider_no, cm.encounter_type, cm.billing_code, cm.program_no, cm.reporter_caisi_role,\n" +
					"cm.reporter_program_team, cm.history, cm.password, cm.locked, cm.archived, cm.position, cm.uuid, cm.appointmentNo,\n" +
					"cm.hourOfEncounterTime, cm.minuteOfEncounterTime, cm.hourOfEncTransportationTime, cm.minuteOfEncTransportationTime\n" +
			"FROM casemgmt_note cm\n" +
			"LEFT JOIN casemgmt_note cm_filter\n" +
			"ON cm.uuid = cm_filter.uuid\n" +
				"AND (cm_filter.update_date > cm.update_date\n" +
					"OR (cm_filter.update_date = cm.update_date AND cm_filter.note_id > cm.note_id)\n" +
				")\n" +
			"LEFT JOIN (\n" +
				"SELECT note.note_id\n" +
				"FROM casemgmt_note note\n" +
				"JOIN casemgmt_issue_notes cinotes on note.note_id = cinotes.note_id\n" +
				"JOIN casemgmt_issue ci on cinotes.id = ci.id\n" +
				"JOIN issue i ON ci.issue_id = i.issue_id\n" +
				"WHERE note.demographic_no = :demographicNo\n" +
				"GROUP BY note.note_id\n" +
				"HAVING SUM(COALESCE(i.code IN\n" +
				"('OMeds', 'SocHistory', 'MedHistory', 'Concerns', 'FamHistory', 'Reminders', 'RiskFactors', 'OcularMedication', 'TicklerNote')\n" +
				", 0)) > 0\n" +
				")\n" +
			"AS is_cpp_note ON is_cpp_note.note_id = cm.note_id\n" +
			"LEFT JOIN casemgmt_note_ext cme " +
				"ON cm.note_id = cme.note_id " +
				"AND cme.key_val = 'Hide Cpp' " +
				"AND cme.value = 1 " +
			"WHERE cm.demographic_no = :demographicNo\n" +
			"AND cme.value IS NULL\n";

		if (isCPPNote)
		{
			queryString +=
				"AND is_cpp_note.note_id IS NOT NULL\n";
		}
		else
		{
			queryString +=
				"AND is_cpp_note.note_id IS NULL\n";
		}

		queryString +=
			"AND cm.archived = 0\n" +
			"AND cm_filter.note_id IS NULL\n" +
			"ORDER BY cm.observation_date ASC";

		Query query = entityManager.createNativeQuery(queryString, CaseManagementNote.class);
		query.setParameter("demographicNo", demographicNo);

		List<CaseManagementNote> results = query.getResultList();

		return results;
	}
}
