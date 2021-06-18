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
package org.oscarehr.dataMigration.mapper.cds.out;

import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.model.allergy.Allergy;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.AdverseReactionSeverity;
import xml.cds.v5_0.AdverseReactionType;
import xml.cds.v5_0.AllergiesAndAdverseReactions;
import xml.cds.v5_0.DrugCode;
import xml.cds.v5_0.PropertyOfOffendingAgent;
import xml.cds.v5_0.ResidualInformation;

import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.DRUG_IDENTIFICATION_NUMBER;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_AGE_OF_ONSET;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_ONSET_REACTION;

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

		String ageOfOnset = exportStructure.getAgeOfOnset() != null ? String.valueOf(exportStructure.getAgeOfOnset()) : null;
		Allergy.REACTION_ONSET onsetOfReaction = exportStructure.getOnsetOfReaction();

		if(ageOfOnset != null || onsetOfReaction != null)
		{
			ResidualInformation residualInformation = objectFactory.createResidualInformation();
			addNonNullDataElements(
					residualInformation,
					CDSConstants.ResidualInfoDataType.NUMERIC,
					RESIDUAL_INFO_DATA_NAME_AGE_OF_ONSET,
					ageOfOnset);
			addNonNullDataElements(
					residualInformation,
					CDSConstants.ResidualInfoDataType.TEXT,
					RESIDUAL_INFO_DATA_NAME_ONSET_REACTION,
					onsetOfReaction.getDescription());
			allergiesAndAdverseReactions.setResidualInfo(residualInformation);
		}

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

	protected AdverseReactionSeverity getSeverity(Allergy.REACTION_SEVERITY severity)
	{
		if(severity != null)
		{
			switch(severity)
			{
				case MILD: return AdverseReactionSeverity.MI;
				case MODERATE: return AdverseReactionSeverity.MO;
				case SEVERE: return AdverseReactionSeverity.LT;
				case UNKNOWN:
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
