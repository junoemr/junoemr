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

import org.oscarehr.common.xml.cds.v5_0.model.LaboratoryResults;
import org.oscarehr.common.xml.cds.v5_0.model.PersonNameSimple;
import org.oscarehr.common.xml.cds.v5_0.model.ResultNormalAbnormalFlag;
import org.oscarehr.demographicImport.mapper.cds.CDSConstants;
import org.oscarehr.demographicImport.model.lab.Lab;
import org.oscarehr.demographicImport.model.lab.LabObservation;
import org.oscarehr.demographicImport.model.lab.LabObservationResult;
import org.oscarehr.demographicImport.model.lab.Reviewer;

import java.util.ArrayList;
import java.util.List;

public class CDSLabExportMapper extends AbstractCDSExportMapper<List<LaboratoryResults>, Lab>
{
	public CDSLabExportMapper()
	{
		super();
	}

	@Override
	public List<LaboratoryResults> exportFromJuno(Lab exportLab)
	{
		List<LaboratoryResults> laboratoryResults = new ArrayList<>();

		for(LabObservation labObservation : exportLab.getLabObservationList())
		{
			for(LabObservationResult observationResult : labObservation.getResults())
			{
				LaboratoryResults laboratoryResult = objectFactory.createLaboratoryResults();

				laboratoryResult.setLaboratoryName(exportLab.getSendingFacility());
				laboratoryResult.setTestNameReportedByLab(observationResult.getName());
				laboratoryResult.setLabTestCode(observationResult.getIdentifier());
				laboratoryResult.setAccessionNumber(exportLab.getAccessionNumber());

				String resultValue = observationResult.getValue();
				if(resultValue != null)
				{
					LaboratoryResults.Result result = objectFactory.createLaboratoryResultsResult();
					result.setValue(resultValue);
					result.setUnitOfMeasure(observationResult.getUnits());
					laboratoryResult.setResult(result);
				}

				//TODO low/high limits vs text range
				String range = observationResult.getRange();
				LaboratoryResults.ReferenceRange referenceRange = objectFactory.createLaboratoryResultsReferenceRange();
				referenceRange.setReferenceRangeText(range);
				laboratoryResult.setReferenceRange(referenceRange);

				laboratoryResult.setLabRequisitionDateTime(toFullDateTime(labObservation.getRequestDateTime()));
				laboratoryResult.setCollectionDateTime(toFullDateTime(exportLab.getEmrReceivedDateTime()));

				for(Reviewer reviewProvider : exportLab.getReviewers())
				{
					LaboratoryResults.ResultReviewer resultReviewer = objectFactory.createLaboratoryResultsResultReviewer();
					PersonNameSimple simpleName = objectFactory.createPersonNameSimple();
					simpleName.setLastName(reviewProvider.getLastName());
					simpleName.setFirstName(reviewProvider.getFirstName());
					resultReviewer.setName(simpleName);
					resultReviewer.setOHIPPhysicianId(reviewProvider.getOhipNumber());
					resultReviewer.setDateTimeResultReviewed(toFullDateTime(reviewProvider.getReviewDateTime()));

					laboratoryResult.getResultReviewer().add(resultReviewer);
				}

				laboratoryResult.setResultNormalAbnormalFlag(getAbnormalFlag(observationResult.getAbnormal()));
				laboratoryResult.setNotesFromLab(observationResult.getNotes());
				laboratoryResult.setTestResultStatus(observationResult.getResultStatus());

				laboratoryResults.add(laboratoryResult);
			}
		}

		return laboratoryResults;
	}

	private ResultNormalAbnormalFlag getAbnormalFlag(Boolean isAbnormal)
	{
		ResultNormalAbnormalFlag resultNormalAbnormalFlag = objectFactory.createResultNormalAbnormalFlag();

		CDSConstants.LAB_ABNORMAL_FLAG abnormalEnum;
		if(isAbnormal == null)
		{
			abnormalEnum = CDSConstants.LAB_ABNORMAL_FLAG.U;
		}
		else if(isAbnormal)
		{
			abnormalEnum = CDSConstants.LAB_ABNORMAL_FLAG.Y;
		}
		else
		{
			abnormalEnum = CDSConstants.LAB_ABNORMAL_FLAG.N;
		}
		resultNormalAbnormalFlag.setResultNormalAbnormalFlagAsEnum(abnormalEnum.name());

		return resultNormalAbnormalFlag;
	}
}
