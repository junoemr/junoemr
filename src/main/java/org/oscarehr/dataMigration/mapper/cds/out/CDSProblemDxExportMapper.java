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
import org.oscarehr.dataMigration.model.dx.DxRecord;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.ProblemList;

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

		DxCode dxCode = exportStructure.getDxCode();
		// export with coding system if possible
		if(dxCode != null && dxCode.getCodingSystem() != null)
		{
			// if diagnosis code is filled and there is no free text, ProblemDiagnosisDescription should match the code description
			problemList.setProblemDiagnosisDescription(dxCode.getDescription());
			problemList.setDiagnosisCode(getStandardCoding(dxCode));
		}
		else if(dxCode != null) // export similar to notes
		{
			problemList.setProblemDescription(
					((dxCode.getCodingSystem() != null) ? dxCode.getCodingSystem().getValue() : "") +
							"[" + dxCode.getCode() + "]: "
							+ dxCode.getDescription());
		}
		else
		{
			logEvent("Dx Record is missing a required code");
		}

		problemList.setProblemStatus(exportStructure.getStatus().name());
		problemList.setOnsetDate(toNullableDateFullOrPartial(exportStructure.getStartDate()));

		if(DxRecord.Status.COMPLETE.equals(exportStructure.getStatus()))
		{
			problemList.setResolutionDate(toNullableDateFullOrPartial(exportStructure.getUpdateDate().toLocalDate()));
		}

		return problemList;
	}
}
