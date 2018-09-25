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
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_PROVIDER;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.model.Dxresearch;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DxMapper
{
	private static final Logger logger = MiscUtils.getLogger();
	private final ZPD_ZTR message;
	private final ZPD_ZTR_PROVIDER provider;

	public DxMapper()
	{
		message = null;
		provider = null;
	}
	public DxMapper(ZPD_ZTR message, int providerRep)
	{
		this.message = message;
		this.provider = message.getPATIENT().getPROVIDER(providerRep);
	}

	public int getNumDx()
	{
		return provider.getZPBReps();
	}

	public List<Dxresearch> getDxResearchList() throws HL7Exception
	{
		int numDx = getNumDx();
		List<Dxresearch> dxList = new ArrayList<>(numDx);
		for(int i=0; i< numDx; i++)
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

		String dxCodeId = getDiagnosisCodeId(rep);
		String codingSystem = getDiagnosisCodeCodeSystem(rep);

		if(dxCodeId != null && !dxCodeId.isEmpty() && codingSystem != null && codingSystem.toLowerCase().contains("icd9"))
		{
			dxresearch = new Dxresearch();
			dxresearch.setAssociation(false);
			dxresearch.setCodingSystem("icd9");
			dxresearch.setDxresearchCode(dxCodeId);
			dxresearch.setStatus(getProblemStatus(rep));
			dxresearch.setStartDate(getDiagnosisDate(rep));
			dxresearch.setUpdateDate(getDiagnosisDate(rep));
		}
		return dxresearch;
	}

	public String getDiagnosisCodeId(int rep) throws HL7Exception
	{
		String dxCode = provider.getZPB(rep).getZpb4_diagnosisCode().getCe1_Identifier().getValue();
		return dxCode.replaceAll("\\.", "");
	}

	public String getDiagnosisCodeText(int rep) throws HL7Exception
	{
		return provider.getZPB(rep).getZpb4_diagnosisCode().getCe2_Text().getValue();
	}

	public String getDiagnosisCodeCodeSystem(int rep) throws HL7Exception
	{
		return provider.getZPB(rep).getZpb4_diagnosisCode().getCe3_NameOfCodingSystem().getValue();
	}

	public String getProblemStatus(int rep) throws HL7Exception
	{
		String status = provider.getZPB(rep).getZpb8_problemStatus().getValue();
		switch(status)
		{
			case "I":
			case "D": return "D"; // deleted
			case "C": return "C"; // resolved
			default: return "A"; // active
		}
	}

	public Date getDiagnosisDate(int rep) throws HL7Exception
	{
		return ConversionUtils.fromDateString(provider.getZPB(rep).getZpb2_diagnosisDate().getTs1_TimeOfAnEvent().getValue(), "yyyyMMdd");
	}

	public Date getOnsetDate(int rep) throws HL7Exception
	{
		return ConversionUtils.fromDateString(provider.getZPB(rep).getZpb6_onsetDate().getTs1_TimeOfAnEvent().getValue(), "yyyyMMdd");
	}

	public Date getResolvedDate(int rep) throws HL7Exception
	{
		return ConversionUtils.fromDateString(provider.getZPB(rep).getZpb7_dateResolved().getTs1_TimeOfAnEvent().getValue(), "yyyyMMdd");
	}
}
