/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package org.oscarehr.ws.external.soap.v1.transfer;


import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.springframework.beans.BeanUtils;
import java.util.Date;

public final class NoteTransfer
{
	private Long noteId;
	private Date updateDate;
	private Date observationDate;
	private String note;
	private Boolean signed;
	private Boolean includeIssueInNote;
	private String encounterType;
	private String billingCode;
	private String programNo;
	private String reporterCaisiRole;
	private String reporterProgramTeam;
	private String history;
	private String password;
	private Boolean locked;
	private Boolean archived;
	private Integer position;
	private String uuid;
	private Integer hourOfEncounterTime;
	private Integer minuteOfEncounterTime;
	private Integer hourOfEncTransportationTime;
	private Integer minuteOfEncTransportationTime;

	public Long getNoteId()
	{
		return noteId;
	}

	public void setNoteId(Long noteId)
	{
		this.noteId = noteId;
	}

	public Date getUpdateDate()
	{
		return updateDate;
	}

	public void setUpdateDate(Date updateDate)
	{
		this.updateDate = updateDate;
	}

	public Date getObservationDate()
	{
		return observationDate;
	}

	public void setObservationDate(Date observationDate)
	{
		this.observationDate = observationDate;
	}

	public String getNote()
	{
		return note;
	}

	public void setNote(String note)
	{
		this.note = note;
	}

	public Boolean getSigned()
	{
		return signed;
	}

	public void setSigned(Boolean signed)
	{
		this.signed = signed;
	}

	public Boolean getIncludeIssueInNote()
	{
		return includeIssueInNote;
	}

	public void setIncludeIssueInNote(Boolean includeIssueInNote)
	{
		this.includeIssueInNote = includeIssueInNote;
	}

	public String getEncounterType()
	{
		return encounterType;
	}

	public void setEncounterType(String encounterType)
	{
		this.encounterType = encounterType;
	}

	public String getBillingCode()
	{
		return billingCode;
	}

	public void setBillingCode(String billingCode)
	{
		this.billingCode = billingCode;
	}

	public String getProgramNo()
	{
		return programNo;
	}

	public void setProgramNo(String programNo)
	{
		this.programNo = programNo;
	}

	public String getReporterCaisiRole()
	{
		return reporterCaisiRole;
	}

	public void setReporterCaisiRole(String reporterCaisiRole)
	{
		this.reporterCaisiRole = reporterCaisiRole;
	}

	public String getReporterProgramTeam()
	{
		return reporterProgramTeam;
	}

	public void setReporterProgramTeam(String reporterProgramTeam)
	{
		this.reporterProgramTeam = reporterProgramTeam;
	}

	public String getHistory()
	{
		return history;
	}

	public void setHistory(String history)
	{
		this.history = history;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public Boolean getLocked()
	{
		return locked;
	}

	public void setLocked(Boolean locked)
	{
		this.locked = locked;
	}

	public Boolean getArchived()
	{
		return archived;
	}

	public void setArchived(Boolean archived)
	{
		this.archived = archived;
	}

	public Integer getPosition()
	{
		return position;
	}

	public void setPosition(Integer position)
	{
		this.position = position;
	}

	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}

	public Integer getHourOfEncounterTime()
	{
		return hourOfEncounterTime;
	}

	public void setHourOfEncounterTime(Integer hourOfEncounterTime)
	{
		this.hourOfEncounterTime = hourOfEncounterTime;
	}

	public Integer getMinuteOfEncounterTime()
	{
		return minuteOfEncounterTime;
	}

	public void setMinuteOfEncounterTime(Integer minuteOfEncounterTime)
	{
		this.minuteOfEncounterTime = minuteOfEncounterTime;
	}

	public Integer getHourOfEncTransportationTime()
	{
		return hourOfEncTransportationTime;
	}

	public void setHourOfEncTransportationTime(Integer hourOfEncTransportationTime)
	{
		this.hourOfEncTransportationTime = hourOfEncTransportationTime;
	}

	public Integer getMinuteOfEncTransportationTime()
	{
		return minuteOfEncTransportationTime;
	}

	public void setMinuteOfEncTransportationTime(Integer minuteOfEncTransportationTime)
	{
		this.minuteOfEncTransportationTime = minuteOfEncTransportationTime;
	}

	public static NoteTransfer toTransfer(CaseManagementNote caseManagementNote) {
		if (caseManagementNote == null)
		{
			return null;
		}

		NoteTransfer noteTransfer = new NoteTransfer();
		BeanUtils.copyProperties(caseManagementNote, noteTransfer);

		return noteTransfer;
	}
}
