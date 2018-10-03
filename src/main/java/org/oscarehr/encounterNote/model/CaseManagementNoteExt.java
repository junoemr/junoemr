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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity(name = "model.CaseManagementNoteExt")
@Table(name = "casemgmt_note_ext")
public class CaseManagementNoteExt extends AbstractModel<Long>
{

	// Key Value constants ***All date value key must be in format "XXX Date"
	public static String STARTDATE	    = "Start Date"	;
	public static String RESOLUTIONDATE = "Resolution Date"	;
	public static String PROCEDUREDATE  = "Procedure Date"	;

	public static String AGEATONSET	    = "Age at Onset"	;
	public static String TREATMENT	    = "Treatment"	;
	public static String PROBLEMSTATUS  = "Problem Status"	;
	public static String EXPOSUREDETAIL = "Exposure Details";
	public static String RELATIONSHIP   = "Relationship"	;
	public static String LIFESTAGE	    = "Life Stage"	;
	public static String HIDECPP	    = "Hide Cpp"	;
	public static String PROBLEMDESC    = "Problem Description";


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "key_val")
	private String key;

	@Column(name = "value")
	private String value;

	@Column(name = "date_value")
	@Temporal(TemporalType.DATE)
	private Date dateValue;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "note_id")
	private CaseManagementNote note;

	public CaseManagementNoteExt() {}

	/** construct a copy of the given note */
	public CaseManagementNoteExt(CaseManagementNoteExt extToCopy)
	{
		this(extToCopy, null);
	}

	public CaseManagementNoteExt(CaseManagementNoteExt extToCopy, CaseManagementNote referenceNote)
	{
		this.id = null;
		this.note = referenceNote;
		this.dateValue = extToCopy.dateValue;
		this.key = extToCopy.key;
		this.value = extToCopy.value;
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

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public Date getDateValue()
	{
		return dateValue;
	}

	public void setDateValue(Date dateValue)
	{
		this.dateValue = dateValue;
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
