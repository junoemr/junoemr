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

import org.oscarehr.dataMigration.model.dx.DxCode;
import org.oscarehr.dataMigration.model.encounterNote.MedicalHistoryNote;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.PastHealth;
import xml.cds.v5_0.StandardCoding;

import java.util.List;

@Component
public class CDSPastHealthExportMapper extends AbstractCDSNoteExportMapper<PastHealth, MedicalHistoryNote>
{
	public CDSPastHealthExportMapper()
	{
		super();
	}

	@Override
	public PastHealth exportFromJuno(MedicalHistoryNote exportStructure)
	{
		PastHealth pastHealth = objectFactory.createPastHealth();

		pastHealth.setPastHealthProblemDescriptionOrProcedures(exportStructure.getNoteText());
		pastHealth.setDiagnosisProcedureCode(generateDiagnosisProcedureCode(exportStructure.getDxIssueCodes())); // TODO: How to deal with multiple codes
		// use start date field if we can, otherwise use the observation date
		pastHealth.setOnsetOrEventDate(toNullableDateFullOrPartial(exportStructure.getStartDate(), exportStructure.getObservationDate().toLocalDate()));
		pastHealth.setLifeStage(getLifeStage(exportStructure.getLifeStage()));
		pastHealth.setResolvedDate(toNullableDateFullOrPartial(exportStructure.getResolutionDate()));
		pastHealth.setProcedureDate(toNullableDateFullOrPartial(exportStructure.getProcedureDate()));
		pastHealth.setNotes(exportStructure.getAnnotation());
		pastHealth.setProblemStatus(exportStructure.getTreatment());

		return pastHealth;
	}

	/**
	 * Returns the first code in dxCodesList as a StandardCoding object
	 * Juno Medical History Notes support multiple codes, but CDS does not
	 * @param dxCodes list of DxCodes from the Medical History Note
	 * @return First DxCode in dxCodesList or null
	 */
	protected StandardCoding generateDiagnosisProcedureCode(List<DxCode> dxCodes)
	{
		if(dxCodes != null && !dxCodes.isEmpty())
		{
			return getStandardCoding(dxCodes.get(0));
		}
		return null;
	}

}
