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
import org.oscarehr.common.model.Appointment;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.provider.model.ProviderData;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "model_CaseManagementNote")
@Table(name = "casemgmt_note")
public class CaseManagementNote extends AbstractModel<Long>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "note_id")
	private Long noteId;

	@Column(name = "update_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	@Column(name = "observation_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date observationDate;

	@Column(name = "note")
	private String note;

	@Column(name = "signed")
	private Boolean signed = false;

	@Column(name = "include_issue_innote")
	private Boolean includeIssueInNote = false;

	@Column(name = "encounter_type")
	private String encounterType;

	@Column(name = "billing_code")
	private String billingCode;

	@Deprecated
	@Column(name = "program_no")
	private String programNo;

	@Deprecated
	@Column(name = "reporter_caisi_role")
	private String reporterCaisiRole;

	@Deprecated
	@Column(name = "reporter_program_team")
	private String reporterProgramTeam;

	@Column(name = "history")
	private String history;

	@Column(name = "password")
	private String password;

	@Column(name = "locked")
	private Boolean locked = false;

	@Column(name = "archived")
	private Boolean archived = false;

	@Column(name = "position")
	private Integer position;

	@Column(name = "uuid")
	private String uuid;

	@Column(name = "hourOfEncounterTime")
	private Integer hourOfEncounterTime;

	@Column(name = "minuteOfEncounterTime")
	private Integer minuteOfEncounterTime;

	@Column(name = "hourOfEncTransportationTime")
	private Integer hourOfEncTransportationTime;

	@Column(name = "minuteOfEncTransportationTime")
	private Integer minuteOfEncTransportationTime;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "demographic_no")
	private Demographic demographic;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "provider_no")
	private ProviderData provider;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "signing_provider_no")
	private ProviderData signingProvider;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "appointmentNo")
	private Appointment appointment;

	// with cascade, these entities will be persisted/merged/deleted when this class is.
	@OneToMany(fetch=FetchType.LAZY, mappedBy = "note", cascade = CascadeType.ALL)
	private List<CaseManagementNoteExt> noteExtensionList;

	// with cascade, these entities will be persisted/merged/deleted when this class is.
	@OneToMany(fetch=FetchType.LAZY, mappedBy = "note", cascade = CascadeType.ALL)
	private List<CaseManagementNoteLink> noteLinkList;

	// with cascade, these entities will be persisted when this class is.
	@OneToMany(fetch=FetchType.LAZY, mappedBy = "id.caseManagementNote", cascade = CascadeType.PERSIST)
	private List<CaseManagementIssueNote> issueNoteList;

	// with cascade, these entities will be persisted/merged/deleted when this class is.
	@OneToMany(fetch=FetchType.LAZY, mappedBy = "note", cascade = CascadeType.ALL)
	private List<CaseManagementNoteResidualInfo> residualInfoList;

	public CaseManagementNote() {}

	/** construct a copy of the given note */
	public CaseManagementNote(CaseManagementNote noteToCopy)
	{
		this.noteId = null;
		this.appointment = noteToCopy.appointment;
		this.archived = noteToCopy.archived;
		this.billingCode = noteToCopy.billingCode;
		this.demographic = noteToCopy.demographic;
		this.encounterType = noteToCopy.encounterType;
		this.history = noteToCopy.history;
		this.hourOfEncounterTime = noteToCopy.hourOfEncounterTime;
		this.hourOfEncTransportationTime = noteToCopy.hourOfEncTransportationTime;
		this.includeIssueInNote = noteToCopy.includeIssueInNote;
		this.locked = noteToCopy.locked;
		this.minuteOfEncounterTime = noteToCopy.minuteOfEncounterTime;
		this.minuteOfEncTransportationTime = noteToCopy.minuteOfEncTransportationTime;
		this.note = noteToCopy.note;
		this.observationDate = noteToCopy.observationDate;
		this.password = noteToCopy.password;
		this.position = noteToCopy.position;
		this.programNo = noteToCopy.programNo;
		this.provider = noteToCopy.provider;
		this.reporterCaisiRole = noteToCopy.reporterCaisiRole;
		this.reporterProgramTeam = noteToCopy.reporterProgramTeam;
		this.signed = noteToCopy.signed;
		this.signingProvider = noteToCopy.signingProvider;
		this.updateDate = noteToCopy.updateDate;
		this.uuid = noteToCopy.uuid;

		/* also make copies of the note extensions, links, and issues */
		if(noteToCopy.noteExtensionList != null)
		{
			this.noteExtensionList = new ArrayList<>(noteToCopy.noteExtensionList.size());
			for(CaseManagementNoteExt extToCopy : noteToCopy.noteExtensionList)
			{
				noteExtensionList.add(new CaseManagementNoteExt(extToCopy, this));
			}
		}
		if(noteToCopy.noteLinkList != null)
		{
			this.noteLinkList = new ArrayList<>(noteToCopy.noteLinkList.size());
			for(CaseManagementNoteLink linkToCopy : noteToCopy.noteLinkList)
			{
				noteLinkList.add(new CaseManagementNoteLink(linkToCopy, this));
			}
		}

		if(noteToCopy.issueNoteList != null)
		{
			this.issueNoteList = new ArrayList<>(noteToCopy.issueNoteList.size());
			for(CaseManagementIssueNote issueNoteToCopy : noteToCopy.issueNoteList)
			{
				issueNoteList.add(new CaseManagementIssueNote(issueNoteToCopy, this));
			}
		}

		if(noteToCopy.residualInfoList != null)
		{
			this.residualInfoList = new ArrayList<>(noteToCopy.residualInfoList.size());
			for(CaseManagementNoteResidualInfo noteResidualInfo : noteToCopy.residualInfoList)
			{
				residualInfoList.add(new CaseManagementNoteResidualInfo(noteResidualInfo, this));
			}
		}
	}

	public Long getId()
	{
		return noteId;
	}

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

	@Deprecated
	public String getProgramNo()
	{
		return programNo;
	}

	@Deprecated
	public void setProgramNo(String programNo)
	{
		this.programNo = programNo;
	}

	@Deprecated
	public String getReporterCaisiRole()
	{
		return reporterCaisiRole;
	}

	@Deprecated
	public void setReporterCaisiRole(String reporterCaisiRole)
	{
		this.reporterCaisiRole = reporterCaisiRole;
	}

	@Deprecated
	public String getReporterProgramTeam()
	{
		return reporterProgramTeam;
	}

	@Deprecated
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

	public Demographic getDemographic()
	{
		return demographic;
	}

	public void setDemographic(Demographic demographic)
	{
		this.demographic = demographic;
	}

	public ProviderData getProvider()
	{
		return provider;
	}

	public void setProvider(ProviderData provider)
	{
		this.provider = provider;
	}

	public ProviderData getSigningProvider()
	{
		return signingProvider;
	}

	public void setSigningProvider(ProviderData signingProvider)
	{
		this.signingProvider = signingProvider;
	}

	public Appointment getAppointment()
	{
		return appointment;
	}

	public void setAppointment(Appointment appointment)
	{
		this.appointment = appointment;
	}

	public List<CaseManagementNoteExt> getNoteExtensionList()
	{
		return noteExtensionList;
	}

	public void setNoteExtensionList(List<CaseManagementNoteExt> noteExtensionList)
	{
		this.noteExtensionList = noteExtensionList;
	}

	public List<CaseManagementNoteResidualInfo> getResidualInfoList()
	{
		return residualInfoList;
	}

	public void setResidualInfoList(List<CaseManagementNoteResidualInfo> residualInfoList)
	{
		this.residualInfoList = residualInfoList;
	}

	public void addExtension(CaseManagementNoteExt ext)
	{
		if(noteExtensionList == null)
		{
			noteExtensionList = new ArrayList<>(1);
		}
		noteExtensionList.add(ext);
	}

	public List<CaseManagementNoteLink> getNoteLinkList()
	{
		return noteLinkList;
	}

	public void setNoteLinkList(List<CaseManagementNoteLink> noteLinkList)
	{
		this.noteLinkList = noteLinkList;
	}

	public void addNoteLink(CaseManagementNoteLink link)
	{
		if(noteLinkList == null)
		{
			noteLinkList = new ArrayList<>(1);
		}
		noteLinkList.add(link);
	}

	public List<CaseManagementIssueNote> getIssueNoteList()
	{
		return issueNoteList;
	}

	public void setIssueNoteList(List<CaseManagementIssueNote> issueNoteList)
	{
		this.issueNoteList = issueNoteList;
	}

	public void addIssueNote(CaseManagementIssueNote issueNote)
	{
		if(issueNoteList == null)
		{
			issueNoteList = new ArrayList<>(1);
		}
		issueNoteList.add(issueNote);
	}

	public String getStatus()
	{
		String status = "";
		if (getSigned())
		{
			status = "Signed";
		}
		else
		{
			status = "Unsigned";
		}

		if (getPassword() != null && getPassword().length() > 0)
		{
			// locked note - can be temporarily unlocked
			if (locked)
			{
				status += "/Locked";
			}
			else
			{
				status += "/Unlocked";
			}
		}

		return status;
	}

	public boolean getHasHistory()
	{
		if(getHistory() != null)
		{
			if(getHistory().indexOf("----------------History Record----------------") != -1)
			{
				return true;
			}
		}

		return false;
	}

	public ArrayList<String> getIssueDescriptions()
	{
		ArrayList<String> issueDescriptions = new ArrayList<String>();

		for (CaseManagementIssueNote issue : getIssueNoteList())
		{
			issueDescriptions.add(issue.getId().getCaseManagementIssue().getIssue().getDescription());
		}

		return (issueDescriptions);
	}

	public String getEncounterTime()
	{
		StringBuilder et = new StringBuilder();

		if(getHourOfEncounterTime()!=null)
		{
			et.append(getHourOfEncounterTime());
			et.append(":");
		}

		if(getMinuteOfEncounterTime()!=null)
		{
			et.append(getMinuteOfEncounterTime());
		}

		return et.toString();
	}

	public String getEncounterTransportationTime()
	{
		StringBuilder et = new StringBuilder();

		if(getHourOfEncTransportationTime()!=null)
		{
			et.append(getHourOfEncTransportationTime());
			et.append(":");
		}

		if(getMinuteOfEncTransportationTime()!=null)
		{
			et.append(getMinuteOfEncTransportationTime());
		}

		return et.toString();
	}
}
