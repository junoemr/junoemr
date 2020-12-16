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

package org.oscarehr.casemgmt.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EncounterCPPNote
{
	private Integer noteId;
	private LocalDateTime updateDate;
	private LocalDateTime observationDate;
	private Integer demographicNo;
	private String providerNo;
	private String note;
	private Boolean signed;
	private Boolean includeIssueInNote;
	private String signingProviderNo;
	private String encounterType;
	private String billingCode;
	private String programNo;
	private String reporterCaisiRole;
	private String reporterProgramTeam;
	private String history;
	private String uuid;
	private String password;
	private Boolean locked;
	private Boolean archived;
	private Integer position;
	private Integer appointmentNo;
	private Integer hourOfEncounterTime;
	private Integer minuteOfEncounterTime;
	private Integer hourOfEncTransportationTime;
	private Integer minuteOfEncTransportationTime;
	private String editors;
	private Integer revision;
	private Integer issueId;
	private String code;
	private String description;
	private LocalDate extStartDate;
	private LocalDate extResolutionDate;
	private LocalDate extProcedureDate;
	private String extAgeAtOnset;
	private String extTreatment;
	private String extProblemStatus;
	private String extExposureDetail;
	private String extRelationship;
	private String extLifeStage;
	private String extHideCpp;
	private String extProblemDescription;

	public Integer getNoteId()
	{
		return noteId;
	}

	public void setNoteId(Integer noteId)
	{
		this.noteId = noteId;
	}

	public LocalDateTime getUpdateDate()
	{
		return updateDate;
	}

	public void setUpdateDate(LocalDateTime updateDate)
	{
		this.updateDate = updateDate;
	}

	public LocalDateTime getObservationDate()
	{
		return observationDate;
	}

	public void setObservationDate(LocalDateTime observationDate)
	{
		this.observationDate = observationDate;
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
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

	public String getSigningProviderNo()
	{
		return signingProviderNo;
	}

	public void setSigningProviderNo(String signingProviderNo)
	{
		this.signingProviderNo = signingProviderNo;
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

	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(String uuid)
	{
		this.uuid = uuid;
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

	public Integer getAppointmentNo()
	{
		return appointmentNo;
	}

	public void setAppointmentNo(Integer appointmentNo)
	{
		this.appointmentNo = appointmentNo;
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

	public String getEditors()
	{
		return editors;
	}

	public void setEditors(String editors)
	{
		this.editors = editors;
	}

	public Integer getRevision()
	{
		return revision;
	}

	public void setRevision(Integer revision)
	{
		this.revision = revision;
	}

	public Integer getIssueId()
	{
		return issueId;
	}

	public void setIssueId(Integer issueId)
	{
		this.issueId = issueId;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public LocalDate getExtStartDate()
	{
		return extStartDate;
	}

	public void setExtStartDate(LocalDate extStartDate)
	{
		this.extStartDate = extStartDate;
	}

	public LocalDate getExtResolutionDate()
	{
		return extResolutionDate;
	}

	public void setExtResolutionDate(LocalDate extResolutionDate)
	{
		this.extResolutionDate = extResolutionDate;
	}

	public LocalDate getExtProcedureDate()
	{
		return extProcedureDate;
	}

	public void setExtProcedureDate(LocalDate extProcedureDate)
	{
		this.extProcedureDate = extProcedureDate;
	}

	public String getExtAgeAtOnset()
	{
		return extAgeAtOnset;
	}

	public void setExtAgeAtOnset(String extAgeAtOnset)
	{
		this.extAgeAtOnset = extAgeAtOnset;
	}

	public String getExtTreatment()
	{
		return extTreatment;
	}

	public void setExtTreatment(String extTreatment)
	{
		this.extTreatment = extTreatment;
	}

	public String getExtProblemStatus()
	{
		return extProblemStatus;
	}

	public void setExtProblemStatus(String extProblemStatus)
	{
		this.extProblemStatus = extProblemStatus;
	}

	public String getExtExposureDetail()
	{
		return extExposureDetail;
	}

	public void setExtExposureDetail(String extExposureDetail)
	{
		this.extExposureDetail = extExposureDetail;
	}

	public String getExtRelationship()
	{
		return extRelationship;
	}

	public void setExtRelationship(String extRelationship)
	{
		this.extRelationship = extRelationship;
	}

	public String getExtLifeStage()
	{
		return extLifeStage;
	}

	public void setExtLifeStage(String extLifeStage)
	{
		this.extLifeStage = extLifeStage;
	}

	public String getExtHideCpp()
	{
		return extHideCpp;
	}

	public void setExtHideCpp(String extHideCpp)
	{
		this.extHideCpp = extHideCpp;
	}

	public String getExtProblemDescription()
	{
		return extProblemDescription;
	}

	public void setExtProblemDescription(String extProblemDescription)
	{
		this.extProblemDescription = extProblemDescription;
	}
}
