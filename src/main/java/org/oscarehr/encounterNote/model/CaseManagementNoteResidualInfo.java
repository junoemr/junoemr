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

import lombok.Data;
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

@Data
@Entity(name = "model_CaseManagementNoteResidualInfo")
@Table(name = "casemgmt_note_residual_info")
public class CaseManagementNoteResidualInfo extends AbstractModel<Long>
{
	public CaseManagementNoteResidualInfo()
	{
	}
	public CaseManagementNoteResidualInfo(CaseManagementNoteResidualInfo infoToCopy, CaseManagementNote referenceNote)
	{
		this.id = null;
		this.note = referenceNote;
		this.contentKey = infoToCopy.contentKey;
		this.contentValue = infoToCopy.contentValue;
		this.contentType = infoToCopy.contentType;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "note_id", nullable = false)
	private CaseManagementNote note;

	@Column(name = "content_key", nullable = false)
	private String contentKey;

	@Column(name = "content_value")
	private String contentValue;

	@Column(name = "content_type", nullable = false)
	private String contentType;
}