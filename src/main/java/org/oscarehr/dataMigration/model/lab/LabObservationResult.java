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
package org.oscarehr.dataMigration.model.lab;

import lombok.Data;
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import org.oscarehr.dataMigration.model.encounterNote.EncounterNote;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * represents a lab result, OBX with associated data
 */
@Data
public class LabObservationResult extends AbstractTransientModel
{
	private String name;
	private String identifier;
	private String value;
	private String units;
	private String range;
	private String resultStatus;
	private Boolean abnormal;
	private LocalDateTime observationDateTime;
	// comments represents the NTE section under OBX
	private List<String> comments;
	// annotation is a linked chart note
	private EncounterNote annotation;

	public LabObservationResult()
	{
		this.comments = new ArrayList<>();
	}

	public void addComment(String comment)
	{
		this.comments.add(comment);
	}
}
