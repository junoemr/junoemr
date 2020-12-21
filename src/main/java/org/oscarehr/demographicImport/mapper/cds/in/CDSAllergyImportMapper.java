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
package org.oscarehr.demographicImport.mapper.cds.in;

import org.oscarehr.common.xml.cds.v5_0.model.AdverseReactionSeverity;
import org.oscarehr.common.xml.cds.v5_0.model.AllergiesAndAdverseReactions;
import org.oscarehr.common.xml.cds.v5_0.model.DrugCode;
import org.oscarehr.common.xml.cds.v5_0.model.PropertyOfOffendingAgent;
import org.oscarehr.demographicImport.model.allergy.Allergy;
import org.springframework.stereotype.Component;

import static org.oscarehr.allergy.model.Allergy.SEVERITY_CODE_MILD;
import static org.oscarehr.allergy.model.Allergy.SEVERITY_CODE_MODERATE;
import static org.oscarehr.allergy.model.Allergy.SEVERITY_CODE_SEVERE;
import static org.oscarehr.allergy.model.Allergy.SEVERITY_CODE_UNKNOWN;

@Component
public class CDSAllergyImportMapper extends AbstractCDSImportMapper<AllergiesAndAdverseReactions, Allergy>
{
	public CDSAllergyImportMapper()
	{
		super();
	}

	@Override
	public Allergy importToJuno(AllergiesAndAdverseReactions importStructure)
	{
		Allergy allergy = new Allergy();

		allergy.setDescription(importStructure.getOffendingAgentDescription());
		allergy.setTypeCode(getTypeCode(importStructure));
		allergy.setDrugIdentificationNumber(getDin(importStructure));
		//TODO reaction type?
		allergy.setStartDate(toNullablePartialDate(importStructure.getStartDate()));
		allergy.setLifeStage(getLifeStage(importStructure.getLifeStage()));
		allergy.setSeverityOfReaction(getSeverity(importStructure));
		allergy.setReaction(importStructure.getReaction());
		allergy.setEntryDateTime(toNullableLocalDateTime(importStructure.getRecordedDate()));
		allergy.setAnnotation(importStructure.getNotes());

		return allergy;
	}

	protected Integer getTypeCode(AllergiesAndAdverseReactions importStructure)
	{
		PropertyOfOffendingAgent propertyOfOffendingAgent = importStructure.getPropertyOfOffendingAgent();

		Integer typeCode = null;
		if(propertyOfOffendingAgent != null)
		{
			switch(propertyOfOffendingAgent)
			{
				// where do these come from?
				case DR: typeCode = 13; break;
				case ND: typeCode = 0; break;
			}
		}

		return typeCode;
	}

	protected String getDin(AllergiesAndAdverseReactions importStructure)
	{
		DrugCode code = importStructure.getCode();
		String din = null;
		if(code != null)
		{
			din = code.getCodeValue();
		}

		return din;
	}

	protected String getSeverity(AllergiesAndAdverseReactions importStructure)
	{
		AdverseReactionSeverity adverseReactionSeverity = importStructure.getSeverity();
		String severity = null;

		if(adverseReactionSeverity != null)
		{
			switch(adverseReactionSeverity)
			{
				case MI: severity = SEVERITY_CODE_MILD; break;
				case MO: severity = SEVERITY_CODE_MODERATE; break;
				case LT: severity = SEVERITY_CODE_SEVERE; break;
				case NO: severity = SEVERITY_CODE_UNKNOWN; break;
			}
		}

		return severity;
	}
}
