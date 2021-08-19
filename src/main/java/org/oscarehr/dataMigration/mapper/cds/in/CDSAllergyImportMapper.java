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
package org.oscarehr.dataMigration.mapper.cds.in;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.dataMigration.model.allergy.Allergy;
import org.oscarehr.dataMigration.service.context.PatientImportContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.AdverseReactionSeverity;
import xml.cds.v5_0.AdverseReactionType;
import xml.cds.v5_0.AllergiesAndAdverseReactions;
import xml.cds.v5_0.DateTimeFullOrPartial;
import xml.cds.v5_0.DrugCode;
import xml.cds.v5_0.PropertyOfOffendingAgent;

import java.time.LocalDateTime;

import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_AGE_OF_ONSET;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_ONSET_REACTION;

@Component
public class CDSAllergyImportMapper extends AbstractCDSImportMapper<AllergiesAndAdverseReactions, Allergy>
{
	@Autowired
	protected PatientImportContextService patientImportContextService;

	public CDSAllergyImportMapper()
	{
		super();
	}

	@Override
	public Allergy importToJuno(AllergiesAndAdverseReactions importStructure)
	{
		Allergy allergy = new Allergy();

		allergy.setDescription(getDescriptionOrDefault(importStructure.getOffendingAgentDescription()));
		allergy.setTypeCode(getTypeCode(importStructure));
		allergy.setDrugIdentificationNumber(getDin(importStructure));
		allergy.setStartDate(toNullablePartialDate(importStructure.getStartDate()));
		allergy.setLifeStage(getLifeStage(importStructure.getLifeStage()));
		allergy.setSeverityOfReaction(getSeverity(importStructure.getSeverity()));
		allergy.setReaction(importStructure.getReaction());
		allergy.setEntryDateTime(getEntryDateTimeWithDefault(importStructure.getRecordedDate()));

		allergy.setAnnotation(generationAnnotation(importStructure.getNotes(), importStructure.getReactionType()));

		allergy.setAgeOfOnset(getResidualDataElementAsLong(importStructure.getResidualInfo(), RESIDUAL_INFO_DATA_NAME_AGE_OF_ONSET));
		String onsetOfReaction = getResidualDataElementAsString(importStructure.getResidualInfo(), RESIDUAL_INFO_DATA_NAME_ONSET_REACTION);
		allergy.setOnsetOfReaction(Allergy.REACTION_ONSET.fromDescription(onsetOfReaction));
		allergy.setResidualInfo(importAllResidualInfo(importStructure.getResidualInfo(),
				RESIDUAL_INFO_DATA_NAME_AGE_OF_ONSET, RESIDUAL_INFO_DATA_NAME_ONSET_REACTION));

		return allergy;
	}

	protected String generationAnnotation(String notes, AdverseReactionType reactionType)
	{
		String annotation = "";
		if (notes != null)
		{
			annotation += notes;
		}
		if (reactionType != null)
		{
			annotation += "\nReaction Type: " + reactionType.value();
		}
		return StringUtils.trimToNull(annotation);
	}

	protected String getDescriptionOrDefault(String dataDescription)
	{
		String description = StringUtils.trimToNull(dataDescription);
		if(description == null)
		{
			description = "No description";
			logEvent("Allergy record had no description, it was set to '" + description + "'");
		}
		return description;
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

	protected Allergy.REACTION_SEVERITY getSeverity(AdverseReactionSeverity adverseReactionSeverity)
	{
		Allergy.REACTION_SEVERITY severity = Allergy.REACTION_SEVERITY.UNKNOWN;

		if(adverseReactionSeverity != null)
		{
			switch(adverseReactionSeverity)
			{
				case MI: severity = Allergy.REACTION_SEVERITY.MILD; break;
				case MO: severity = Allergy.REACTION_SEVERITY.MODERATE; break;
				case LT: severity = Allergy.REACTION_SEVERITY.SEVERE; break;
				case NO: severity = Allergy.REACTION_SEVERITY.UNKNOWN; break;
			}
		}

		return severity;
	}

	protected LocalDateTime getEntryDateTimeWithDefault(DateTimeFullOrPartial recordedDate)
	{
		if (recordedDate == null)
		{
			return patientImportContextService.getContext().getDefaultDate().atStartOfDay();
		}
		return toNullableLocalDateTime(recordedDate);
	}
}
