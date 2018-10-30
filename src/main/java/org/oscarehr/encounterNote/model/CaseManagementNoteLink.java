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
package org.oscarehr.encounterNote.model;

import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "model.CaseManagementNoteLink")
@Table(name = "casemgmt_note_link")
public class CaseManagementNoteLink extends AbstractModel<Long>
{
	private static final Integer CASEMGMTNOTE = 1;
	private static final Integer DRUGS = 2;
	private static final Integer ALLERGIES = 3;
	private static final Integer HL7LAB = 4; //represents the hl7TextMessage table
	private static final Integer DOCUMENT = 5;
	private static final Integer EFORMDATA = 6;
	private static final Integer DEMOGRAPHIC = 7;
	private static final Integer PREVENTIONS = 8;
	private static final Integer LABTEST2 = 9; //repesents the labPatientPhysicianInfo table
	private static final Integer TICKLER = 10;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "table_name")
	private Integer tableName = CASEMGMTNOTE;

	@Column(name = "table_id")
	private Integer tableId;

	@Column(name = "other_id")
	private String otherId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "note_id")
	private CaseManagementNote note;

	public CaseManagementNoteLink() {}

	/** construct a copy of the given note */
	public CaseManagementNoteLink(CaseManagementNoteLink linkToCopy)
	{
		this(linkToCopy, null);
	}

	public CaseManagementNoteLink(CaseManagementNoteLink linkToCopy, CaseManagementNote referenceNote)
	{
		this.id = null;
		this.note = referenceNote;
		this.otherId = linkToCopy.otherId;
		this.tableId = linkToCopy.tableId;
		this.tableName = linkToCopy.tableName;
	}

	public void setCaseManagementNote(int noteId)
	{
		setTableIdLink(noteId, CASEMGMTNOTE);
	}

	public void setAllergy(int allergyId)
	{
		setTableIdLink(allergyId, ALLERGIES);
	}

	public void setDrug(int drugId)
	{
		setTableIdLink(drugId, DRUGS);
	}

	public void setHl7Lab(int labId)
	{
		setTableIdLink(labId, HL7LAB);
	}

	public void setDocument(int documentId)
	{
		setTableIdLink(documentId, DOCUMENT);
	}

	public void setEForm(int eformId)
	{
		setTableIdLink(eformId, EFORMDATA);
	}

	public void setDemographic(int demographicId)
	{
		setTableIdLink(demographicId, DEMOGRAPHIC);
	}

	public void setPrevention(int preventionId)
	{
		setTableIdLink(preventionId, PREVENTIONS);
	}

	public void setTickler(int ticklerId)
	{
		setTableIdLink(ticklerId, TICKLER);
	}

	private void setTableIdLink(int tableId, int tableName)
	{
		setTableId(tableId);
		setTableName(tableName);
	}


	@Override
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Integer getTableName()
	{
		return tableName;
	}

	public void setTableName(Integer tableName)
	{
		this.tableName = tableName;
	}

	public Integer getTableId()
	{
		return tableId;
	}

	public void setTableId(Integer tableId)
	{
		this.tableId = tableId;
	}

	public String getOtherId()
	{
		return otherId;
	}

	public void setOtherId(String otherId)
	{
		this.otherId = otherId;
	}

	public CaseManagementNote getNote()
	{
		return note;
	}

	public void setNote(CaseManagementNote note)
	{
		this.note = note;
	}
}
