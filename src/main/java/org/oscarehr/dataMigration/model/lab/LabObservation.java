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
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.dataMigration.model.AbstractTransientModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * represents a lab observation, OBR with associated data
 */
@Data
public class LabObservation extends AbstractTransientModel
{
	private String name;
	private String procedureCode;
	private boolean blockedResult; // represents data that is blocked (OLIS specific)
	private LocalDateTime observationDateTime;
	private LocalDateTime requestDateTime;
	private Hl7TextInfo.REPORT_STATUS reportStatus;

	// comments represents the NTE section under OBR
	private List<String> comments;
	// results represents the OBX segments
	private List<LabObservationResult> results;

	public LabObservation()
	{
		this.results = new ArrayList<>();
		this.comments = new ArrayList<>();
	}

	public void addResult(LabObservationResult result)
	{
		this.results.add(result);
	}

	public void addComment(String comment)
	{
		this.comments.add(comment);
	}
}
