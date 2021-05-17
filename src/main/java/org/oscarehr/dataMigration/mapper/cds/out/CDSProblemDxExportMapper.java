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

import org.oscarehr.dataMigration.model.dx.DxRecord;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.ProblemList;
import xml.cds.v5_0.StandardCoding;

@Component
public class CDSProblemDxExportMapper extends AbstractCDSNoteExportMapper<ProblemList, DxRecord>
{
	public CDSProblemDxExportMapper()
	{
		super();
	}

	@Override
	public ProblemList exportFromJuno(DxRecord exportStructure)
	{
		ProblemList problemList = objectFactory.createProblemList();

		// if diagnosis code is filled and there is no free text, ProblemDiagnosisDescription should match the code description
		problemList.setProblemDiagnosisDescription(exportStructure.getCodeDescription());
		problemList.setDiagnosisCode(getDiagnosisCode(exportStructure));

		problemList.setProblemStatus(exportStructure.getStatus().name());
		problemList.setOnsetDate(toNullableDateFullOrPartial(exportStructure.getStartDate()));

		if(DxRecord.Status.COMPLETE.equals(exportStructure.getStatus()))
		{
			problemList.setResolutionDate(toNullableDateFullOrPartial(exportStructure.getUpdateDate().toLocalDate()));
		}

		return problemList;
	}

	protected StandardCoding getDiagnosisCode(DxRecord exportStructure)
	{
		String codingSystem = (exportStructure.getCodingSystem() != null) ? exportStructure.getCodingSystem().getValue() : DxRecord.CodingSystem.ICD9.getValue();

		StandardCoding standardCoding = objectFactory.createStandardCoding();
		standardCoding.setStandardCodingSystem(codingSystem);
		standardCoding.setStandardCode(exportStructure.getDxCode());
		standardCoding.setStandardCodeDescription(exportStructure.getCodeDescription());
		return standardCoding;
	}
}
