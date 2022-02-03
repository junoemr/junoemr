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

package org.oscarehr.labs.transfer;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BasicLabInfo implements Serializable
{
	int labId;
	private String demographicId;
	private String label;
	private String discipline;
	private LocalDateTime observationDateTime;
	private Boolean abnormal;
	private String reportStatus;
	private String type;

	/**
	 * Basic lab info to display lists of labs. Specifically for the old encounter page lab section
	 * @param labId
	 * @param demographicId
	 * @param label Doesn't always exist
	 * @param discipline Takes place of label if label doesn't exist
	 * @param observationDateTime Observation date or OBR date in the HL7 format. Typicaly the date the lab test was performed
	 * @param abnormal Flag indicating if a lab has abnormal results
	 * @param reportStatus Is 'A' if abnormal
	 * @param type Lab type from the hl7TextMessage table
	 */
	public BasicLabInfo(int labId, String demographicId, String label, String discipline, LocalDateTime observationDateTime, Boolean abnormal, String reportStatus, String type)
	{
		this.labId = labId;
		this.demographicId = demographicId;
		this.label = label;
		this.discipline = discipline;
		this.observationDateTime = observationDateTime;
		this.abnormal = abnormal;
		if(this.abnormal == null)
		{
			this.abnormal = false;
		}
		this.reportStatus = reportStatus;
		this.type = type;
	}

	public String getLabel()
	{
		if(this.label == null || this.label.trim() == "")
		{
			return this.discipline;
		}
		return this.label;
	}

}
