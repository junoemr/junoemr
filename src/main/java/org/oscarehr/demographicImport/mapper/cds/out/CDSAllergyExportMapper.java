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
		allergiesAndAdverseReactions.setPropertyOfOffendingAgent(PropertyOfOffendingAgent.DR); //TODO how to determine this

		String din = exportStructure.getDrugIdentificationNumber();
		if(din != null && !din.isEmpty())
		{
			DrugCode drugCode = objectFactory.createDrugCode();
			drugCode.setCodeType("DIN");
			drugCode.setCodeValue(din);
			allergiesAndAdverseReactions.setCode(drugCode);
		}

		allergiesAndAdverseReactions.setReactionType(AdverseReactionType.AL); //TODO how to determine this
		allergiesAndAdverseReactions.setStartDate(toNullableDateFullOrPartial(exportStructure.getStartDate()));
		allergiesAndAdverseReactions.setLifeStage(getLifeStage(exportStructure.getLifeStage()));

		allergiesAndAdverseReactions.setSeverity(getSeverity(exportStructure.getSeverityOfReaction()));
		allergiesAndAdverseReactions.setReaction(exportStructure.getReaction());
		allergiesAndAdverseReactions.setRecordedDate(toNullableDateTimeFullOrPartial(exportStructure.getEntryDate()));
		allergiesAndAdverseReactions.setNotes(exportStructure.getAnnotation());

		return allergiesAndAdverseReactions;
	}

	protected AdverseReactionSeverity getSeverity(String severity)
	{
		switch(severity)
		{
			case "1" : return AdverseReactionSeverity.MI;
			case "2" : return AdverseReactionSeverity.MO;
			case "3" : return AdverseReactionSeverity.LT;
			default: return AdverseReactionSeverity.NO;
		}
	}
}
