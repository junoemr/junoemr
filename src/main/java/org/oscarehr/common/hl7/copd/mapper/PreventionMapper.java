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
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.prevention.model.PreventionExt;
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
		int numPreventions = getNumPreventions();
		List<Prevention> preventionList = new ArrayList<>(numPreventions);
		for(int i=0; i< numPreventions; i++)
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
		prevention.setPreventionType(getTranslatedVaccineCode(rep));
		prevention.setRefused(false);
		prevention.setNever(false);

		// set the original data in the 'comment' extension
		PreventionExt commentExt = new PreventionExt();
		commentExt.setPrevention(prevention);
		commentExt.setCommentKeyValue(getVaccineCode(rep));
		prevention.addExtension(commentExt);

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
		return StringUtils.trimToNull(provider.getIMMUNIZATION(rep).getZIM().getZim3_vaccineCode().getValue());
	}

	public String getTranslatedVaccineCode(int rep) throws HL7Exception
	{
		String typeCode = getVaccineCode(rep);

		// according to the CoPD spec, the hl7 should contain a correct code that can be directly added to the database
		// however we have found that we need to map some values to codes sometimes
		if(validPreventionTypes.contains(typeCode))
		{
			return typeCode;
		}
		if(preventionTypeMap.containsKey(typeCode))
		{
			String mapValue = preventionTypeMap.get(typeCode);
			logger.warn("Invalid vaccine code '" + typeCode + "' was mapped to '" + mapValue + "'");
			if(!validPreventionTypes.contains(mapValue))
			{
				logger.warn("Mapped vaccine code value does not exist in oscar's preventions. default value used");
				return "OtherA";
			}
			return mapValue;
		}
		logger.error("Invalid or unknown vaccine code: " + typeCode);
		return "OtherA";// default to generic 'other' type
	}

	static
	{
		// unofficial mapping of values we have encountered during imports
		preventionTypeMap.put("Adacel", "dTap");
		preventionTypeMap.put("Adacel/Polio", "Tdap-IPV");
		preventionTypeMap.put("Adacel/Polio (Quad)", "Tdap-IPV");
		preventionTypeMap.put("dPT/Polio/HIB", "TdP-IPV-Hib");
		preventionTypeMap.put("dPT/Polio", "DT-IPV");
		preventionTypeMap.put("Engerix B", "HepB");
		preventionTypeMap.put("Fluad", "Flu");
		preventionTypeMap.put("flu shot", "Flu");
		preventionTypeMap.put("Hepatitis A", "HepA");
		preventionTypeMap.put("Hepatitis B", "HepB");
		preventionTypeMap.put("Hepatitis B Recombinant", "HepB");
		preventionTypeMap.put("Hep A and Hep B Combined", "HepAB");
		preventionTypeMap.put("Infanrix", "DTaP");
		preventionTypeMap.put("Influenza", "Flu");
		preventionTypeMap.put("Meningococcal C-TT Conjugate", "MenconC");
		preventionTypeMap.put("MMR/Varicella", "MMRV");
		preventionTypeMap.put("penta", "DPTP-IPV-Hib");
		preventionTypeMap.put("Pentacel", "DTaP-IPV-Hib");
		preventionTypeMap.put("Pneumococcal 7-Conjugate", "PNEU-C7");
		preventionTypeMap.put("Pneumococcal 13-Conjugate", "PNEU-C");
		preventionTypeMap.put("Pneumococcal 23-Polyvalent", "Pneumovax");
		preventionTypeMap.put("Pneumovax 23", "Pneumovax");
		preventionTypeMap.put("Prevnar", "Pneu-C");
		preventionTypeMap.put("quad", "DPTP-IPV");
		preventionTypeMap.put("Quadracel", "Tdap-IPV");
		preventionTypeMap.put("rotovirus", "Rot");
		preventionTypeMap.put("Td Adsorbed", "Td");
		preventionTypeMap.put("Varicella", "VZ");
		preventionTypeMap.put("varivax", "VZ");
	}
}
