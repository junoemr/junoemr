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

@Entity(name = "model_CaseManagementNoteLink")
@Table(name = "casemgmt_note_link")
public class CaseManagementNoteLink extends AbstractModel<Long>
{
	public static final int CASEMGMTNOTE = 1;
	public static final int DRUGS = 2;
	public static final int ALLERGIES = 3;
	public static final int HL7LAB = 4; //represents the hl7TextMessage table
	public static final int DOCUMENT = 5;
	public static final int EFORMDATA = 6;
	public static final int DEMOGRAPHIC = 7;
	public static final int PREVENTIONS = 8;
	public static final int LABTEST2 = 9; //repesents the labPatientPhysicianInfo table
	public static final int TICKLER = 10;


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

	public CaseManagementNoteLink(CaseManagementNote referenceNote)
	{
		this.id = null;
		this.note = referenceNote;
		referenceNote.addNoteLink(this);
	}

	public void setLinkedCaseManagementNoteId(int noteId)
	{
		setTableIdLink(noteId, CASEMGMTNOTE);
	}

	public void setLinkedAllergyId(int allergyId)
	{
		setTableIdLink(allergyId, ALLERGIES);
	}

	public void setLinkedDrugId(int drugId)
	{
		setTableIdLink(drugId, DRUGS);
	}

	public void setLinkedHl7LabId(int labId)
	{
		setTableIdLink(labId, HL7LAB);
	}

	public void setLinkedDocumentId(int documentId)
	{
		setTableIdLink(documentId, DOCUMENT);
	}

	public void setLinkedEFormId(int eformId)
	{
		setTableIdLink(eformId, EFORMDATA);
	}

	public void setLinkedDemographicId(int demographicId)
	{
		setTableIdLink(demographicId, DEMOGRAPHIC);
	}

	public void setLinkedPreventionId(int preventionId)
	{
		setTableIdLink(preventionId, PREVENTIONS);
	}

	public void setLinkedTicklerId(int ticklerId)
	{
		setTableIdLink(ticklerId, TICKLER);
	}

	public void setLinkedLabPhysicianInfoId(int infoId)
	{
		setTableIdLink(infoId, LABTEST2);
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
