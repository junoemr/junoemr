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

package org.oscarehr.casemgmt.web.formbeans;

import org.apache.struts.action.ActionForm;
import org.oscarehr.casemgmt.dto.EncounterPageData;
import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSection;
import org.oscarehr.ws.rest.to.model.SummaryTo1;

import java.util.List;
import java.util.Map;

public class JunoEncounterFormBean extends ActionForm
{
	private EncounterPageData pageData = null;
	private EncounterNotes notes = null;
	private Map<String, EncounterSection> sections = null;
	private List<String> cppNoteSections = null;
	private List<String> leftNoteSections = null;
	private List<String> rightNoteSections = null;
	private List<SummaryTo1> leftSummaries = null;

	private String appointmentDate;
	private String encType;
	private String reason;

	public EncounterPageData getPageData()
	{
		return pageData;
	}

	public void setPageData(EncounterPageData header)
	{
		this.pageData = header;
	}

	public EncounterNotes getNotes()
	{
		return notes;
	}

	public void setNotes(EncounterNotes notes)
	{
		this.notes = notes;
	}

	public Map<String, EncounterSection> getSections()
	{
		return sections;
	}

	public void setSections(Map<String, EncounterSection> sections)
	{
		this.sections = sections;
	}

	public List<String> getCppNoteSections()
	{
		return cppNoteSections;
	}

	public void setCppNoteSections(List<String> cppNoteSections)
	{
		this.cppNoteSections = cppNoteSections;
	}

	public List<String> getLeftNoteSections()
	{
		return leftNoteSections;
	}

	public void setLeftNoteSections(List<String> leftNoteSections)
	{
		this.leftNoteSections = leftNoteSections;
	}

	public List<SummaryTo1> getLeftSummaries()
	{
		return leftSummaries;
	}

	public void setLeftSummaries(List<SummaryTo1> leftSummaries)
	{
		this.leftSummaries = leftSummaries;
	}

	public List<String> getRightNoteSections()
	{
		return rightNoteSections;
	}

	public void setRightNoteSections(List<String> rightNoteSections)
	{
		this.rightNoteSections = rightNoteSections;
	}

	public String getAppointmentDate()
	{
		return appointmentDate;
	}

	public void setAppointmentDate(String appointmentDate)
	{
		this.appointmentDate = appointmentDate;
	}

	public String getEncType()
	{
		return encType;
	}

	public void setEncType(String encType)
	{
		this.encType = encType;
	}

	public String getReason()
	{
		return reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}
}
