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
package org.oscarehr.demographicImport.mapper.cds.out;

import org.oscarehr.common.xml.cds.v5_0.model.AdverseReactionSeverity;
import org.oscarehr.common.xml.cds.v5_0.model.AdverseReactionType;
import org.oscarehr.common.xml.cds.v5_0.model.AllergiesAndAdverseReactions;
import org.oscarehr.common.xml.cds.v5_0.model.DrugCode;
import org.oscarehr.common.xml.cds.v5_0.model.PropertyOfOffendingAgent;
import org.oscarehr.demographicImport.model.allergy.Allergy;
import org.springframework.stereotype.Component;

import static org.oscarehr.allergy.model.Allergy.SEVERITY_CODE_MILD;
import static org.oscarehr.allergy.model.Allergy.SEVERITY_CODE_MODERATE;
import static org.oscarehr.allergy.model.Allergy.SEVERITY_CODE_SEVERE;
import static org.oscarehr.allergy.model.Allergy.SEVERITY_CODE_UNKNOWN;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.DRUG_IDENTIFICATION_NUMBER;

@Component
public class CDSAllergyExportMapper extends AbstractCDSExportMapper<AllergiesAndAdverseReactions, Allergy>
{
	public CDSAllergyExportMapper()
	{
		super();
	}

	@Override
	public AllergiesAndAdverseReactions exportFromJuno(Allergy exportStructure)
	{
		AllergiesAndAdverseReactions allergiesAndAdverseReactions = objectFactory.createAllergiesAndAdverseReactions();

		allergiesAndAdverseReactions.setOffendingAgentDescription(exportStructure.getDescription());
		allergiesAndAdverseReactions.setPropertyOfOffendingAgent(getPropertyOfOffendingAgent(exportStructure));
		allergiesAndAdverseReactions.setCode(getDrugCode(exportStructure));
		allergiesAndAdverseReactions.setReactionType(getReactionType(exportStructure));
		allergiesAndAdverseReactions.setStartDate(toNullableDateFullOrPartial(exportStructure.getStartDate()));
		allergiesAndAdverseReactions.setLifeStage(getLifeStage(exportStructure.getLifeStage()));
		allergiesAndAdverseReactions.setSeverity(getSeverity(exportStructure.getSeverityOfReaction()));
		allergiesAndAdverseReactions.setReaction(exportStructure.getReaction());
		allergiesAndAdverseReactions.setRecordedDate(toNullableDateTimeFullOrPartial(exportStructure.getEntryDateTime()));
		allergiesAndAdverseReactions.setNotes(exportStructure.getAnnotation());

		return allergiesAndAdverseReactions;
	}

	protected DrugCode getDrugCode(Allergy exportStructure)
	{
		String din = exportStructure.getDrugIdentificationNumber();
		if(din != null && !din.isEmpty())
		{
			DrugCode drugCode = objectFactory.createDrugCode();
			drugCode.setCodeType(DRUG_IDENTIFICATION_NUMBER);
			drugCode.setCodeValue(din);
			return drugCode;
		}
		return null;
	}

	protected AdverseReactionSeverity getSeverity(String severity)
	{
		if(severity != null)
		{
			switch(severity)
			{
				case SEVERITY_CODE_MILD : return AdverseReactionSeverity.MI;
				case SEVERITY_CODE_MODERATE : return AdverseReactionSeverity.MO;
				case SEVERITY_CODE_SEVERE : return AdverseReactionSeverity.LT;
				case SEVERITY_CODE_UNKNOWN:
				default: return AdverseReactionSeverity.NO;
			}
		}
		return null;
	}

	protected PropertyOfOffendingAgent getPropertyOfOffendingAgent(Allergy exportStructure)
	{
		Integer typeCode = exportStructure.getTypeCode();
		PropertyOfOffendingAgent propertyOfOffendingAgent = null;

		if(typeCode != null)
		{
			switch(typeCode)
			{
				// where do these come from?
				case 0: propertyOfOffendingAgent = PropertyOfOffendingAgent.ND; break;
				case 13: propertyOfOffendingAgent = PropertyOfOffendingAgent.DR; break;
				default: propertyOfOffendingAgent = PropertyOfOffendingAgent.UK; break;
			}
		}
		return propertyOfOffendingAgent;
	}

	protected AdverseReactionType getReactionType(Allergy exportStructure)
	{
		return null;
		//AdverseReactionType.AL or AdverseReactionType.AR
		// TODO how to determine this?
	}
}
