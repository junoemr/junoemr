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
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_PROVIDER;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.model.Prevention;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreventionMapper
{
	private static final Logger logger = MiscUtils.getLogger();
	private final ZPD_ZTR message;
	private final ZPD_ZTR_PROVIDER provider;

	private List<String> validPreventionTypes;
	private static Map<String, String> preventionTypeMap = new HashMap<>();

	public PreventionMapper()
	{
		message = null;
		provider = null;
		validPreventionTypes = null;
	}
	public PreventionMapper(ZPD_ZTR message, int providerRep)
	{
		this.message = message;
		this.provider = message.getPATIENT().getPROVIDER(providerRep);
		this.validPreventionTypes = null;
	}

	public void setValidPreventionTypes(List<String> validPreventionTypes)
	{
		this.validPreventionTypes = validPreventionTypes;
	}

	public int getNumPreventions()
	{
		return provider.getIMMUNIZATIONReps();
	}

	public List<Prevention> getPreventionList() throws HL7Exception
	{
		int numImmunizations = getNumPreventions();
		List<Prevention> preventionList = new ArrayList<>(numImmunizations);
		for(int i=0; i< numImmunizations; i++)
		{
			Prevention prevention = getPrevention(i);
			if(prevention != null)
			{
				preventionList.add(prevention);
			}
		}
		return preventionList;
	}

	public Prevention getPrevention(int rep) throws HL7Exception
	{
		Prevention prevention = new Prevention();
		Date immunizationDate = getImmunizationDate(rep);

		prevention.setPreventionDate(immunizationDate);
		prevention.setLastUpdateDate(immunizationDate);
		prevention.setPreventionType(getVaccineCode(rep));
		prevention.setRefused(false);
		prevention.setNever(false);

		return prevention;
	}

	public Date getImmunizationDate(int rep) throws HL7Exception
	{
		String dateStr = provider.getIMMUNIZATION(rep).getZIM().getZim2_immunizationDate().getTs1_TimeOfAnEvent().getValue();
		if(dateStr==null || dateStr.trim().isEmpty() || dateStr.equals("00000000"))
		{
			return null;
		}
		return ConversionUtils.fromDateString(dateStr, "yyyyMMdd");
	}

	public String getVaccineCode(int rep) throws HL7Exception
	{
		String typeCode = StringUtils.trimToNull(provider.getIMMUNIZATION(rep).getZIM().getZim3_vaccineCode().getValue());

		if(validPreventionTypes.contains(typeCode))
		{
			return typeCode;
		}
		if(preventionTypeMap.containsKey(typeCode))
		{
			logger.warn("Invalid vaccine code was mapped: " + typeCode);
			return preventionTypeMap.get(typeCode);
		}
		logger.error("Invalid or unknown vaccine code: " + typeCode);
		//TODO attempt to map values to valid codes

		return "OtherA";// default to generic 'other' type
	}

	static
	{
		preventionTypeMap.put("Infanrix", "OtherA");
		preventionTypeMap.put("Meningococcal C-TT Conjugate", "MenconC");
		preventionTypeMap.put("rotovirus", "Rot");
		preventionTypeMap.put("Prevnar", "OtherA");
		preventionTypeMap.put("MMR/Varicella", "MMR-Var");
		preventionTypeMap.put("Varicella", "MMR-Var");
		preventionTypeMap.put("Pneumococcal 7-Conjugate", "PNEU-C");
		preventionTypeMap.put("dPT/Polio/HIB", "DPT");
	}
}
