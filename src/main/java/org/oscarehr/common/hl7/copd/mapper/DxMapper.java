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
package org.oscarehr.common.hl7.copd.mapper;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.model.Dxresearch;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.CaseManagementNoteExt;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DxMapper extends AbstractMapper
{
	private static final String ICD9_CODE_STRING = "icd9";

	public DxMapper(ZPD_ZTR message, int providerRep)
	{
		super(message, providerRep);
	}

	public int getNumDx()
	{
		return provider.getZPBReps();
	}

	public List<Dxresearch> getDxResearchList() throws HL7Exception
	{
		int numDx = getNumDx();
		List<Dxresearch> dxList = new ArrayList<>(numDx);
		for(int i = 0; i < numDx; i++)
		{
			Dxresearch dxresearch = getDxResearch(i);
			if(dxresearch != null)
			{
				dxList.add(dxresearch);
			}
		}
		return dxList;
	}

	public Dxresearch getDxResearch(int rep) throws HL7Exception
	{
		Dxresearch dxresearch = null;
		if(canMapToIcd9(rep))
		{
			dxresearch = new Dxresearch();
			dxresearch.setAssociation(false);
			dxresearch.setCodingSystem(ICD9_CODE_STRING);
			dxresearch.setDxresearchCode(getDiagnosisCodeId(rep));
			dxresearch.setStatus(getProblemStatusCode(rep));
			dxresearch.setStartDate(getDiagnosisDateOrDefault(rep));
			dxresearch.setUpdateDate(getDiagnosisDate(rep));
		}
		return dxresearch;
	}

	public List<CaseManagementNote> getDxResearchNoteList() throws HL7Exception
	{
		int numDx = getNumDx();
		List<CaseManagementNote> dxNoteList = new ArrayList<>(numDx);
		for(int i = 0; i < numDx; i++)
		{
			CaseManagementNote dxresearchNote = getDxResearchNote(i);
			if(dxresearchNote != null)
			{
				dxNoteList.add(dxresearchNote);
			}
		}
		return dxNoteList;
	}

	public CaseManagementNote getDxResearchNote(int rep) throws HL7Exception
	{
		CaseManagementNote note = new CaseManagementNote();
		String description = StringUtils.trimToEmpty(getDiagnosisDescription(rep));
		String noteText = StringUtils.trimToEmpty(
				description + "\n"
				+ "Symptoms: "+ StringUtils.trimToEmpty(StringUtils.trimToEmpty(getSymptomsIdentifier(rep)) + "\n"
				+ StringUtils.trimToEmpty(getSymptomsText(rep))) + "\n"
				+ "Outcome: " + getOutcomeCodeDescription(rep) + "\n"
				+ StringUtils.trimToEmpty(getNoteText(rep))
		);

		Date diagnosisDate = getDiagnosisDate(rep);

		note.setNote(noteText);
		note.setObservationDate(getDiagnosisDateOrDefault(rep));

		if(diagnosisDate != null)
		{
			CaseManagementNoteExt ext = new CaseManagementNoteExt();
			ext.setNote(note);
			ext.setKey(CaseManagementNoteExt.STARTDATE);
			ext.setDateValue(diagnosisDate);
			note.addExtension(ext);
		}
		if(!description.isEmpty())
		{
			CaseManagementNoteExt ext = new CaseManagementNoteExt();
			ext.setNote(note);
			ext.setKey(CaseManagementNoteExt.PROBLEMDESC);
			ext.setValue(description);
			note.addExtension(ext);
		}
		CaseManagementNoteExt ext = new CaseManagementNoteExt();
		ext.setNote(note);
		ext.setKey(CaseManagementNoteExt.PROBLEMSTATUS);
		ext.setValue(getProblemStatusCodeDescription(rep));
		note.addExtension(ext);
		return note;
	}

	public String getDiagnosisDescription(int rep) throws HL7Exception
	{
		return provider.getZPB(rep).getZpb3_diagnosisDescription().getValue();
	}
	public String getDiagnosisCodeId(int rep) throws HL7Exception
	{
		String dxCode = provider.getZPB(rep).getZpb4_diagnosisCode().getCe1_Identifier().getValue();
		return dxCode != null ? dxCode.replaceAll("\\.", "") : null;
	}

	public String getDiagnosisCodeText(int rep) throws HL7Exception
	{
		return provider.getZPB(rep).getZpb4_diagnosisCode().getCe2_Text().getValue();
	}

	public String getDiagnosisCodeCodeSystem(int rep) throws HL7Exception
	{
		return provider.getZPB(rep).getZpb4_diagnosisCode().getCe3_NameOfCodingSystem().getValue();
	}

	public String getSymptomsIdentifier(int rep) throws HL7Exception
	{
		return provider.getZPB(rep).getZpb5_symptomsPresent().getCe1_Identifier().getValue();
	}
	public String getSymptomsText(int rep) throws HL7Exception
	{
		return provider.getZPB(rep).getZpb5_symptomsPresent().getCe2_Text().getValue();
	}

	public String getProblemStatus(int rep) throws HL7Exception
	{
		return provider.getZPB(rep).getZpb8_problemStatus().getValue();
	}
	public String getProblemStatusCode(int rep) throws HL7Exception
	{
		String status = getProblemStatus(rep);
		switch(status)
		{
			case "I":
			case "C": return "C"; // resolved
			case "D": return "D"; // deleted
			default: return "A"; // active
		}
	}
	public String getProblemStatusCodeDescription(int rep) throws HL7Exception
	{
		String statusCode = getProblemStatusCode(rep);
		switch(statusCode)
		{
			case "D": return "Deleted";
			case "C": return "Resolved";
			default:
			case "A": return "Active";
		}
	}

	public String getOutcomeCode(int rep) throws HL7Exception
	{
		return provider.getZPB(rep).getZpb9_outcomeCode().getValue();
	}
	public String getOutcomeCodeDescription(int rep) throws HL7Exception
	{
		String outcomeCode = getOutcomeCode(rep);
		switch(outcomeCode)
		{
			case "01": return "Patient Recovered";
			case "02": return "Patient Recovered With Residual Effects";
			case "03": return "Pending / Patient Convalescing";
			default:
			case "04": return "Unknown";
			case "05": return "Not Applicable";
			case "06": return "Fatal";
		}
	}

	public Date getDiagnosisDateOrDefault(int rep) throws HL7Exception
	{
		Date diagnosisDate = getDiagnosisDate(rep);
		if (diagnosisDate == null)
		{// diagnostic date cannot be null, force to foobar.
			diagnosisDate = new Date(1900, Calendar.JANUARY,1);
		}
		return diagnosisDate;
	}
	public Date getDiagnosisDate(int rep) throws HL7Exception
	{
		return getNullableDate(provider.getZPB(rep)
				.getZpb2_diagnosisDate().getTs1_TimeOfAnEvent().getValue());
	}

	public Date getOnsetDate(int rep) throws HL7Exception
	{
		return getNullableDate(provider.getZPB(rep)
				.getZpb6_onsetDate().getTs1_TimeOfAnEvent().getValue());
	}

	public Date getResolvedDate(int rep) throws HL7Exception
	{
		return getNullableDate(provider.getZPB(rep)
				.getZpb7_dateResolved().getTs1_TimeOfAnEvent().getValue());
	}
	public String getNoteText(int rep) throws HL7Exception
	{
		return provider.getZPB(rep).getZpb10_noteText().getValue();
	}

	private boolean canMapToIcd9(int rep) throws HL7Exception
	{
		String dxCodeId = getDiagnosisCodeId(rep);
		String codingSystem = getDiagnosisCodeCodeSystem(rep);

		return (dxCodeId != null && !dxCodeId.isEmpty() && codingSystem != null && codingSystem.toLowerCase().contains(ICD9_CODE_STRING));
	}
}
